@file:Suppress("UNCHECKED_CAST")

package xyz.xenondevs.cbf

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.flatMapMutable
import xyz.xenondevs.commons.provider.mapNonNull
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.reflection.createStarProjectedType
import xyz.xenondevs.commons.reflection.equalsIgnoreNullability
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * An entry in a [Compound].
 */
private sealed interface CompoundEntry<T : Any> {
    
    /**
     * The serialized binary data of this entry.
     * Is null if this entry stores no data.
     */
    val bin: ByteArray?
    
    /**
     * The type of the data stored in this entry, if known.
     * If the type is not known, [cachedValue] will be null as well.
     */
    val type: KType?
    
    /**
     * The deserialized value of this entry.
     * Non-null if [type] is known and entry [isNotEmpty].
     */
    val cachedValue: T?
    
    /**
     * Sets the type and value of this entry.
     * @throws IllegalArgumentException If the given [type] is incompatible with this entry.
     */
    fun set(type: KType, value: T?)
    
    /**
     * Gets the value of this entry as [type] [T].
     * @throws IllegalArgumentException If the given [type] is incompatible with this entry.
     */
    fun get(type: KType): T?
    
    /**
     * Clears this entry, removing all data.
     */
    fun clear()
    
    /**
     * Transfers the data of this entry to [entry].
     */
    fun transferTo(entry: CompoundEntry<T>)
    
    /**
     * Creates a new [DirectCompoundEntry] with the same data as this entry.
     */
    fun toDirectEntry(): DirectCompoundEntry<T>
    
    /**
     * Check whether this entry is empty.
     */
    fun isEmpty(): Boolean
    
    /**
     * Check whether this entry is not empty.
     */
    fun isNotEmpty(): Boolean
    
}

private class DirectCompoundEntry<T : Any> private constructor(
    bin: ByteArray?,
    override var type: KType?,
    override var cachedValue: T?
) : CompoundEntry<T> {
    
    private var _bin: ByteArray? = bin
    override val bin: ByteArray?
        get() {
            if (type != null && cachedValue != null) {
                return CBF.write(cachedValue, type)
            } else {
                return _bin
            }
        }
    
    constructor(bin: ByteArray) : this(bin, null, null)
    
    constructor(type: KType, value: T?) : this(null, type, value)
    
    fun <T : Any> toProviderEntry(type: KType): ProviderCompoundEntry<T> {
        if (this.type != null && this.cachedValue != null) {
            if (!this.type.equalsIgnoreNullability(type))
                throw IllegalArgumentException("Type mismatch: $type != ${this.type}")
            return ProviderCompoundEntry(type, this.cachedValue as T)
        } else if (_bin != null) {
            val value = CBF.read<T>(type, _bin!!)
            return ProviderCompoundEntry(type, value)
        } else {
            return ProviderCompoundEntry(type, null)
        }
    }
    
    override fun toDirectEntry(): DirectCompoundEntry<T> {
        return DirectCompoundEntry(_bin, type, cachedValue)
    }
    
    override fun set(type: KType, value: T?) {
        this.type = type
        this.cachedValue = value
        this._bin = null
    }
    
    override fun get(type: KType): T? {
        if (this.type != null && this.cachedValue != null) {
            if (!this.type.equalsIgnoreNullability(type))
                throw IllegalArgumentException("Type mismatch: $type != ${this.type}")
            return this.cachedValue as T
        }
        
        if (_bin != null)
            return CBF.read(type, _bin!!, strict = true)
        
        return null
    }
    
    override fun clear() {
        _bin = null
        type = null
        cachedValue = null
    }
    
    override fun transferTo(entry: CompoundEntry<T>) {
        when (entry) {
            is DirectCompoundEntry<T> -> {
                entry._bin = _bin
                entry.type = type
                entry.cachedValue = cachedValue
            }
            
            is ProviderCompoundEntry<T> -> {
                if (type != null) {
                    entry.set(type!!, cachedValue!!)
                } else {
                    // no type check possible
                    entry.binProvider.set(_bin)
                }
            }
        }
    }
    
    override fun isEmpty(): Boolean {
        return _bin == null && cachedValue == null
    }
    
    override fun isNotEmpty(): Boolean {
        return _bin != null || cachedValue != null
    }
    
}

private fun <T : Any> ProviderCompoundEntry(type: KType): ProviderCompoundEntry<T> {
    val binProvider: MutableProvider<ByteArray?> = mutableProvider(null)
    val valueProvider: MutableProvider<T?> = binProvider.mapNonNull(
        { CBF.read(type, it, strict = true) },
        { CBF.write(it, type) }
    )
    return ProviderCompoundEntry(binProvider, type, valueProvider)
}

private fun <T : Any> ProviderCompoundEntry(type: KType, value: T?): ProviderCompoundEntry<T> {
    val binProvider: MutableProvider<ByteArray?> = mutableProvider(null)
    val valueProvider: MutableProvider<T?> = binProvider.mapNonNull(
        { CBF.read(type, it, strict = true) },
        { CBF.write(it, type) }
    )
    valueProvider.set(value)
    return ProviderCompoundEntry(binProvider, type, valueProvider)
}

private class ProviderCompoundEntry<T : Any>(
    val binProvider: MutableProvider<ByteArray?>,
    override val type: KType,
    val valueProvider: MutableProvider<T?>
) : CompoundEntry<T> {
    
    override val bin: ByteArray? by binProvider
    override val cachedValue: T? by valueProvider
    
    override fun set(type: KType, value: T?) {
        if (!this.type.equalsIgnoreNullability(type))
            throw IllegalArgumentException("Type mismatch: $type != ${this.type}")
        
        valueProvider.set(value)
    }
    
    override fun get(type: KType): T? {
        if (!this.type.equalsIgnoreNullability(type))
            throw IllegalArgumentException("Type mismatch: $type != ${this.type}")
        
        return valueProvider.get()
    }
    
    override fun clear() {
        valueProvider.set(null)
    }
    
    override fun transferTo(entry: CompoundEntry<T>) {
        entry.set(type, valueProvider.get())
    }
    
    override fun toDirectEntry(): DirectCompoundEntry<T> {
        return DirectCompoundEntry(type, valueProvider.get())
    }
    
    override fun isEmpty(): Boolean =
        valueProvider.get() == null
    
    override fun isNotEmpty(): Boolean =
        valueProvider.get() != null
    
}

/**
 * A compound is a key-value storage intended for serialization via [CBF].
 *
 * After deserialization, a [Compound] contains only binary data of values of unknown types
 * (and the keys under which they're stored), which is then lazily deserialized
 * to the requested type when accessed via [get].
 * Conversely, the type information for serialization is remembered during [set].
 */
class Compound private constructor(
    private val entryMap: HashMap<String, CompoundEntry<*>>
) {
    
    private val lock = ReentrantLock()
    
    /**
     * A set of all keys in this compound.
     */
    val keys: Set<String>
        get() = lock.withLock {
            this@Compound.entryMap.mapNotNullTo(HashSet()) { (key, entry) ->
                key.takeUnless { entry.isEmpty() }
            }
        }
    
    constructor() : this(HashMap())
    
    /**
     * Puts [value] into the compound under [key], remembering [T] for serialization.
     */
    inline operator fun <reified T> set(key: String, value: T) =
        set(typeOf<T>(), key, value)
    
    /**
     * Puts [value] into the compound under [key], remembering [type] for serialization.
     */
    fun set(type: KType, key: String, value: Any?): Unit = lock.withLock {
        val entry = entryMap[key]
        if (entry != null) {
            entry as CompoundEntry<Any>
            entry.set(type, value)
        } else if (value != null) {
            entryMap[key] = DirectCompoundEntry(type, value)
        }
        
        removeStaleEntries()
    }
    
    inline fun <reified T> entry(key: String): MutableProvider<T?> =
        entry(typeOf<T>(), key)
    
    fun <T : Any> entry(type: KType, key: String): MutableProvider<T?> = lock.withLock {
        var providerEntry: ProviderCompoundEntry<T>
        when (val existingEntry = entryMap[key]) {
            is ProviderCompoundEntry<*> -> {
                if (!existingEntry.type.equalsIgnoreNullability(type))
                    throw IllegalArgumentException("Type mismatch for key $key: ${existingEntry.type} != $type")
                providerEntry = existingEntry as ProviderCompoundEntry<T>
            }
            
            is DirectCompoundEntry<*> -> {
                providerEntry = existingEntry.toProviderEntry(type)
                entryMap[key] = providerEntry
            }
            
            null -> {
                providerEntry = ProviderCompoundEntry(type)
                entryMap[key] = providerEntry
            }
        }
        
        return providerEntry.valueProvider
    }
    
    /**
     * Gets the value under [key] as [type] [T] or null if it doesn't exist.
     */
    fun <T : Any> get(type: KType, key: String): T? {
        return (entryMap[key] as CompoundEntry<T>?)?.get(type)
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
        val existingValue = get<T>(type, key)
        if (existingValue != null)
            return existingValue
        
        val default = defaultValue()
        set(type, key, default)
        return default
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
                for ((key, otherEntry) in other.entryMap) {
                    val entry = entryMap[key]
                    if (entry != null) {
                        otherEntry as CompoundEntry<Any>
                        entry as CompoundEntry<Any>
                        otherEntry.transferTo(entry)
                    } else {
                        entryMap[key] = otherEntry.toDirectEntry()
                    }
                }
            }
        }
    }
    
    /**
     * Checks whether this compound has an entry under [key].
     */
    operator fun contains(key: String): Boolean = lock.withLock {
        return entryMap[key]?.isNotEmpty() == true
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
    fun remove(key: String): Unit = lock.withLock {
        entryMap[key]?.clear()
        removeStaleEntries()
    }
    
    /**
     * Renames an entry from [old] to [new].
     * If there already is an entry under [new], it will be overwritten.
     */
    fun rename(old: String, new: String): Unit = lock.withLock {
        val entry = entryMap.remove(old) ?: return
        entryMap[new] = entry
    }
    
    /**
     * Checks whether this compound is empty.
     */
    fun isEmpty(): Boolean = lock.withLock {
        entryMap.values.all { it.isEmpty() }
    }
    
    /**
     * Checks whether this compound is not empty.
     */
    fun isNotEmpty(): Boolean = lock.withLock {
        entryMap.values.any { it.isNotEmpty() }
    }
    
    /**
     * Creates a deep copy of this compound, copying all deserialized values using [copyFunc],
     * which defaults to [CBF.copy].
     */
    fun copy(copyFunc: (KType, Any) -> Any = { type, obj -> CBF.copy(obj, type) }): Compound = lock.withLock {
        val entryMapCopy = HashMap<String, CompoundEntry<*>>()
        
        for ((key, entry) in entryMap) {
            if (entry.isEmpty())
                continue
            
            val type = entry.type
            val value = entry.cachedValue
            if (type != null && value != null) {
                entryMapCopy[key] = DirectCompoundEntry(type, copyFunc(type, value))
            } else {
                entryMapCopy[key] = entry.toDirectEntry()
            }
        }
        
        return Compound(entryMapCopy)
    }
    
    /**
     * Creates a shallow copy of this compound, not copying any values.
     */
    fun shallowCopy(): Compound = copy { _, obj -> obj }
    
    override fun toString(): String = lock.withLock {
        val builder = StringBuilder()
        builder.append("{")
        
        for (key in TreeSet(keys)) {
            val entry = entryMap[key]!!
            if (entry.type != null) {
                builder.append("\n\"$key\": ${entry.cachedValue}")
            } else {
                val hexStr = HexFormat.of().formatHex(entry.bin)
                builder.append("\n\"$key\": (serialized) $hexStr")
            }
        }
        
        return builder.toString().replace("\n", "\n  ") + "\n}"
    }
    
    private fun removeStaleEntries() {
        assert(lock.isHeldByCurrentThread)
        this.entryMap.entries.removeIf { (_, entry) -> entry.isEmpty() }
    }
    
    companion object {
        
        /**
         * Creates a new [Compound] with the given [map] as its values.
         */
        fun of(map: Map<String, Any>): Compound {
            val entryMap = map.mapValuesTo(HashMap<String, CompoundEntry<*>>()) { (_, value) ->
                val type = createStarProjectedType(value::class)
                DirectCompoundEntry(type, value)
            }
            
            return Compound(entryMap)
        }
        
    }
    
    internal object CompoundBinaryAdapter : BinaryAdapter<Compound> {
        
        override fun write(obj: Compound, type: KType, writer: ByteWriter): Unit = obj.lock.withLock {
            val compoundOut = ByteArrayOutputStream()
            val compoundDataWriter = ByteWriter.fromStream(compoundOut)
            var count = 0
            for ((key, entry) in obj.entryMap) {
                val bytes = entry.bin
                if (bytes == null)
                    continue
                
                compoundDataWriter.writeString(key)
                compoundDataWriter.writeVarInt(bytes.size)
                compoundDataWriter.writeBytes(bytes)
                count++
            }
            
            writer.writeVarInt(count)
            writer.writeBytes(compoundOut.toByteArray())
        }
        
        override fun read(type: KType, reader: ByteReader): Compound {
            val mapSize = reader.readVarInt()
            val entryMap = HashMap<String, CompoundEntry<*>>(mapSize)
            
            repeat(mapSize) {
                val key = reader.readString()
                val length = reader.readVarInt()
                val bytes = reader.readBytes(length)
                
                entryMap[key] = DirectCompoundEntry<Any>(bytes)
            }
            
            return Compound(entryMap)
        }
        
        override fun copy(obj: Compound, type: KType): Compound {
            return obj.copy()
        }
        
    }
    
}

inline fun <reified T : Any> Provider<Compound?>.entry(key: String): MutableProvider<T?> =
    flatMapMutable { it?.entry(typeOf<T>(), key) ?: mutableProvider(null) }