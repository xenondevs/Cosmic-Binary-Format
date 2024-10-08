@file:Suppress("UNCHECKED_CAST")

package xyz.xenondevs.cbf

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * Typealias for [Compound] entry watchers that are called when a value is set/removed.
 */
typealias EntryWatcher = (type: KType?, value: Any?) -> Unit

/**
 * Typealias for [Compound] weak entry watchers that are called when a value is set/removed.
 */
typealias WeakEntryWatcher<T> = (owner: T, type: KType?, value: Any?) -> Unit

/**
 * A compound is a key-value storage intended for serialization via [CBF].
 *
 * After deserialization, a [Compound] contains only binary data of values of unknown types
 * (and the keys under which they're stored), which is then lazily deserialized
 * to the requested type when accessed via [get].
 * Conversely, the type information for serialization is remembered during [set].
 */
class Compound internal constructor(
    private val binMap: HashMap<String, ByteArray>,
    private val map: HashMap<String, Any?> = HashMap(),
    private val types: HashMap<String, KType> = HashMap()
) {
    
    private var entryWatchers: HashMap<String, HashSet<EntryWatcher>>? = null
    private var weakEntryWatchers: WeakHashMap<Any, HashMap<String, HashSet<WeakEntryWatcher<*>>>>? = null
    
    private val lock = ReentrantLock()
    
    /**
     * A set of all keys in this compound.
     */
    val keys: Set<String>
        get() = lock.withLock { binMap.keys + map.keys }
    
    constructor() : this(HashMap(), HashMap(), HashMap())
    
    /**
     * Puts [value] into the compound under [key], remembering [T] for serialization.
     */
    inline operator fun <reified T> set(key: String, value: T) {
        set(typeOf<T>(), key, value)
    }
    
    /**
     * Puts [value] into the compound under [key], remembering [type] for serialization.
     */
    fun set(type: KType, key: String, value: Any?) {
        lock.withLock {
            binMap -= key
            map[key] = value
            types[key] = type
        }
        
        notifyWatchers(key, type, value)
    }
    
    /**
     * Gets the value under [key] as [type] [T] or null if it doesn't exist.
     */
    fun <T : Any> get(type: KType, key: String): T? = lock.withLock {
        map[key]?.let { return it as T }
        
        val bytes = binMap[key] ?: return null
        val value = CBF.read<T>(type, bytes)
        
        types[key] = type
        map[key] = value
        binMap -= key
        
        return value
    }
    
    /**
     * Gets the value under [key] as [T] or null if it doesn't exist.
     */
    inline operator fun <reified T : Any> get(key: String): T? {
        return get(typeOf<T>(), key)
    }
    
    /**
     * Gets the value under [key] as [type] [T] or puts and returns
     * the value generated by [defaultValue] if it doesn't exist.
     */
    fun <T : Any> getOrPut(type: KType, key: String, defaultValue: () -> T): T = lock.withLock {
        return get(type, key) ?: defaultValue().also { set(type, key, it) }
    }
    
    /**
     * Gets the value under [key] as [T] or puts and returns
     * the value generated by [defaultValue] if it doesn't exist.
     */
    inline fun <reified T : Any> getOrPut(key: String, noinline defaultValue: () -> T): T {
        return getOrPut(typeOf<T>(), key, defaultValue)
    }
    
    /**
     * Puts all entries from [other] into this compound.
     */
    fun putAll(other: Compound) {
        lock.withLock { 
            other.lock.withLock {
                binMap.putAll(other.binMap)
                map.putAll(other.map)
                types.putAll(other.types)
            }
        }
    }
    
    /**
     * Checks whether this compound has an entry under [key].
     */
    operator fun contains(key: String): Boolean = lock.withLock {
        return map.containsKey(key) || binMap.containsKey(key)
    }
    
    /**
     * Removes the value under [key].
     */
    operator fun minusAssign(key: String) {
        remove(key)
    }
    
    /**
     * Removes the value under [key].
     */
    fun remove(key: String) {
        lock.withLock {
            binMap.remove(key)
            map.remove(key)
            types.remove(key)
        }
        
        notifyWatchers(key, null, null)
    }
    
    /**
     * Renames an entry from [old] to [new].
     * If there already is an entry under [new], it will be overwritten.
     */
    fun rename(old: String, new: String): Unit = lock.withLock {
        if (old in map) {
            map[new] = map.remove(old)!!
            types[new] = types.remove(old)!!
        } else if (old in binMap) {
            binMap[new] = binMap.remove(old)!!
        }
    }
    
    /**
     * Checks whether this compound is empty.
     */
    fun isEmpty(): Boolean =
        lock.withLock { map.isEmpty() && binMap.isEmpty() }
    
    /**
     * Checks whether this compound is not empty.
     */
    fun isNotEmpty(): Boolean =
        lock.withLock { map.isNotEmpty() || binMap.isNotEmpty() }
    
    /**
     * Creates a deep copy of this compound, copying all values via [CBF.copy].
     */
    fun copy(): Compound = lock.withLock {
        val copy = Compound(HashMap(binMap), HashMap(), HashMap(types))
        
        for ((key, value) in map) {
            val valueType = types[key]!!
            copy.map[key] = value?.let { CBF.copy(it, valueType.withNullability(false)) }
        }
        
        return copy
    }
    
    /**
     * Creates a shallow copy of this compound, not copying any values.
     */
    fun shallowCopy(): Compound = lock.withLock {
        return Compound(HashMap(binMap), HashMap(map), HashMap(types))
    }
    
    /**
     * Registers an [EntryWatcher] for [key].
     */
    fun addEntryWatcher(key: String, watcher: EntryWatcher) {
        if (entryWatchers == null)
            entryWatchers = HashMap()
        entryWatchers!!.getOrPut(key, ::HashSet) += watcher
    }
    
    /**
     * Registers a weak [EntryWatcher] for [key] that is automatically removed when [owner] is garbage collected.
     */
    fun <T> addWeakEntryWatcher(owner: T, key: String, watcher: WeakEntryWatcher<T>) {
        if (weakEntryWatchers == null)
            weakEntryWatchers = WeakHashMap()
        weakEntryWatchers!!.getOrPut(owner, ::HashMap).getOrPut(key, ::HashSet) += watcher
    }
    
    /**
     * Removes the non-weak [watcher] under [key].
     */
    fun removeEntryWatcher(key: String, watcher: EntryWatcher) {
        entryWatchers?.get(key)?.remove(watcher)
    }
    
    /**
     * Removes the weak [watcher] under [owner] and [key].
     */
    fun removeWeakEntryWatcher(owner: Any, key: String, watcher: WeakEntryWatcher<*>) {
        weakEntryWatchers?.get(owner)?.get(key)?.remove(watcher)
    }
    
    /**
     * Removes all weak entry watchers registered by [owner].
     */
    fun removeWeakEntryWatchers(owner: Any) {
        weakEntryWatchers?.remove(owner)
    }
    
    private fun notifyWatchers(key: String, type: KType?, value: Any?) {
        entryWatchers?.get(key)?.forEach { it(type, value) }
        weakEntryWatchers?.forEach { (owner, watchers) ->
            watchers[key]?.forEach { watcher ->
                watcher as (Any?, KType?, Any?) -> Unit
                watcher(owner, type, value) }
        }
    }
    
    override fun toString(): String = lock.withLock {
        val builder = StringBuilder()
        builder.append("{")
        
        val keys = TreeSet<String>()
        keys += binMap.keys
        keys += map.keys
        
        for (key in keys) {
            if (key in binMap) {
                val hexStr = HexFormat.of().formatHex(binMap[key])
                builder.append("\n\"$key\": (serialized) $hexStr")
            } else {
                builder.append("\n\"$key\": ${map[key]}")
            }
        }
        
        return builder.toString().replace("\n", "\n  ") + "\n}"
    }
    
    companion object {
        
        /**
         * Creates a new [Compound] with the given [map] as its values.
         */
        fun of(map: Map<String, Any?>): Compound =
            Compound(HashMap(), HashMap(map), HashMap())
        
    }
    
    internal object CompoundBinaryAdapter : BinaryAdapter<Compound> {
        
        override fun write(obj: Compound, type: KType, writer: ByteWriter): Unit = obj.lock.withLock {
            writer.writeVarInt(obj.binMap.size + obj.map.size)
            
            obj.binMap.forEach { (key, binData) ->
                writer.writeString(key)
                writer.writeVarInt(binData.size)
                writer.writeBytes(binData)
            }
            
            obj.map.forEach { (key, value) ->
                writer.writeString(key)
                val binData = CBF.write(value, obj.types[key])
                writer.writeVarInt(binData.size)
                writer.writeBytes(binData)
            }
        }
        
        override fun read(type: KType, reader: ByteReader): Compound {
            val mapSize = reader.readVarInt()
            val binMap = HashMap<String, ByteArray>(mapSize)
            
            repeat(mapSize) {
                val key = reader.readString()
                val length = reader.readVarInt()
                binMap[key] = reader.readBytes(length)
            }
            
            return Compound(binMap)
        }
        
        override fun copy(obj: Compound, type: KType): Compound {
            return obj.copy()
        }
        
    }
    
}