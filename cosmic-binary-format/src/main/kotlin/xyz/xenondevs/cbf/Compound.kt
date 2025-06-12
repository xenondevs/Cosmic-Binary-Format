package xyz.xenondevs.cbf

import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.cbf.io.byteWriter
import xyz.xenondevs.cbf.serializer.VersionedBinarySerializer
import xyz.xenondevs.cbf.util.withLocksOrdered
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.reflection.createStarProjectedType
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * An entry in a [Compound].
 */
private sealed interface CompoundEntry<T> {
    
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
    fun set(type: KType, value: T)
    
    /**
     * Gets the value of this entry as [type] [T].
     * @throws IllegalArgumentException If the given [type] is incompatible with this entry.
     */
    fun get(type: KType): T
    
    /**
     * Serializes this entry to a byte array or null if no data is stored.
     */
    fun serialize(): ByteArray?
    
    /**
     * Transfers the data of this entry to [entry].
     */
    fun transferTo(entry: CompoundEntry<T>)
    
    /**
     * Creates a new [DirectCompoundEntry] with the same data as this entry, or null if this entry [isEmpty].
     */
    fun toDirectEntry(): DirectCompoundEntry<T & Any>?
    
    /**
     * Check whether this entry is empty.
     */
    fun isEmpty(): Boolean
    
    /**
     * Check whether this entry is not empty.
     */
    fun isNotEmpty(): Boolean = !isEmpty()
    
}

private class DirectCompoundEntry<T : Any> private constructor(
    private var bin: ByteArray?,
    override var type: KType?,
    override var cachedValue: T?
) : CompoundEntry<T> {
    
    constructor(bin: ByteArray) : this(bin, null, null)
    
    constructor(type: KType, value: T) : this(null, type, value) {
        require(!type.isMarkedNullable)
    }
    
    // unchecked: R is supertype of T, i.e. T is String, R can be String? or String
    @OptIn(UncheckedApi::class)
    fun <R> toProviderEntry(type: KType, default: () -> R): ProviderCompoundEntry<R> {
        if (this.type != null && this.cachedValue != null) {
            if (!type.isSupertypeOf(this.type!!))
                throw IllegalArgumentException("$type (return type) is not a supertype of ${this.type} (entry type)")
            
            @Suppress("UNCHECKED_CAST")
            return ProviderCompoundEntry(type, this.cachedValue as R, default)
        } else {
            assert(bin != null)
            val value: R = Cbf.read(type, bin!!)
                ?: throw AssertionError("Serialized value is null, but $type was expected")
            return ProviderCompoundEntry(type, value, default)
        }
    }
    
    override fun toDirectEntry(): DirectCompoundEntry<T> {
        return DirectCompoundEntry(bin, type, cachedValue)
    }
    
    override fun set(type: KType, value: T) {
        require(!type.isMarkedNullable)
        
        this.type = type
        this.cachedValue = value
        this.bin = null
    }
    
    @OptIn(UncheckedApi::class)
    override fun get(type: KType): T {
        val type = type.withNullability(false)
        
        if (this.type != null && this.cachedValue != null) {
            if (!type.isSupertypeOf(this.type!!))
                throw IllegalArgumentException("$type (return type) is not a supertype of ${this.type} (entry type)")
            return this.cachedValue as T
        } else {
            assert(bin != null)
            
            set(
                type.withNullability(false),
                Cbf.read(type, bin!!)
                    ?: throw AssertionError("Serialized value is null, but $type was expected")
            )
            
            return this.cachedValue!!
        }
    }
    
    @OptIn(UncheckedApi::class)
    override fun serialize(): ByteArray? {
        if (type != null && cachedValue != null) {
            return Cbf.write(type!!, cachedValue)
        } else {
            return bin
        }
    }
    
    @OptIn(UncheckedApi::class)
    override fun transferTo(entry: CompoundEntry<T>) {
        when (entry) {
            is DirectCompoundEntry<T> -> {
                entry.bin = bin
                entry.type = type
                entry.cachedValue = cachedValue
            }
            
            is ProviderCompoundEntry<T> -> {
                if (type != null && cachedValue != null) {
                    entry.set(type!!, cachedValue!!)
                } else {
                    // no type check possible
                    assert(bin != null)
                    val value = Cbf.read<T>(entry.type, bin!!)
                        ?: throw AssertionError("Serialized value is null, but $type was expected")
                    entry.set(entry.type, value)
                }
            }
        }
    }
    
    // DirectCompoundEntry only exists as non-empty
    override fun isEmpty(): Boolean = false
    
}

private class ProviderCompoundEntry<T>(
    override val type: KType,
    val valueProvider: MutableProvider<T>
) : CompoundEntry<T> {
    
    override val cachedValue: T by valueProvider
    
    constructor(type: KType, default: () -> T) : this(type, mutableProvider(default))
    
    constructor(type: KType, value: T, default: () -> T) : this(
        type,
        if (value == null)
            mutableProvider(default)
        else mutableProvider(value)
    )
    
    override fun set(type: KType, value: T) {
        if (!type.isSubtypeOf(this.type))
            throw IllegalArgumentException("$type (value type) is not a subtype of ${this.type} (entry type)")
        
        valueProvider.set(value)
    }
    
    override fun get(type: KType): T {
        if (!type.isSupertypeOf(this.type))
            throw IllegalArgumentException("$type (return type) is not a supertype of ${this.type} (entry type)")
        
        return valueProvider.get()
    }
    
    @OptIn(UncheckedApi::class)
    override fun serialize(): ByteArray? {
        return cachedValue?.let { Cbf.write(type, it) }
    }
    
    override fun transferTo(entry: CompoundEntry<T>) {
        entry.set(type, valueProvider.get())
    }
    
    override fun toDirectEntry(): DirectCompoundEntry<T & Any>? {
        val value = valueProvider.get()
        if (value == null)
            return null
        return DirectCompoundEntry(type, value)
    }
    
    override fun isEmpty(): Boolean =
        valueProvider.get() == null
    
}

/**
 * A compound is a key-value storage intended for serialization via [Cbf].
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
    @OptIn(UncheckedApi::class)
    inline operator fun <reified T> set(key: String, value: T) =
        set(typeOf<T>(), key, value)
    
    /**
     * Puts [value] into the compound under [key], remembering [type] for serialization.
     */
    @UncheckedApi
    fun <T> set(type: KType, key: String, value: T): Unit = lock.withLock {
        @Suppress("UNCHECKED_CAST")
        val entry = entryMap[key] as CompoundEntry<T>?
        
        when (entry) {
            is DirectCompoundEntry<T> -> {
                if (value != null) {
                    entry.set(type.withNullability(false), value)
                } else {
                    entryMap -= key
                }
            }
            
            is ProviderCompoundEntry<T> -> entry.set(type, value)
            null -> {
                if (value != null) {
                    entryMap[key] = DirectCompoundEntry(type.withNullability(false), value)
                }
            }
        }
    }
    
    /**
     * Creates a [MutableProvider] of the non-null type [T] that is linked to the value of this compound under [key].
     * If this compound does not have an entry under [key], [defaultValue] is used to create it lazily.
     * If there is already an entry provider for [key] that matches [T], [defaultValue] will be ignored and the existing provider will be returned.
     *
     * @throws IllegalArgumentException If there already is an entry provider for [key], but for a different type.
     */
    @OptIn(UncheckedApi::class)
    @JvmName("entry0")
    inline fun <reified T : Any> entry(key: String, noinline defaultValue: () -> T): MutableProvider<T> =
        entry(typeOf<T>(), key, defaultValue)
    
    /**
     * Creates a [MutableProvider] of the type [T] that is linked to the value of this compound under [key].
     * If this compound does not have an entry under [key], [defaultValue] is used to create it lazily.
     * If there is already an entry provider for [key] that matches [T], [defaultValue] will be ignored and the existing provider will be returned.
     *
     * @throws IllegalArgumentException If there already is an entry provider for [key], but for a different type.
     */
    @OptIn(UncheckedApi::class)
    @JvmName("entry1")
    inline fun <reified T : Any> entry(key: String, noinline defaultValue: () -> T? = { null }): MutableProvider<T?> =
        entry(typeOf<T>().withNullability(true), key, defaultValue)
    
    /**
     * Creates a [MutableProvider] of [type] that is linked to the value of this compound under [key].
     * If this compound does not have an entry under [key], [defaultValue] is used to create it lazily.
     * If there is already an entry provider for [key] that matches [type], [defaultValue] will be ignored and the existing provider will be returned.
     *
     * @throws IllegalArgumentException If there already is an entry provider for [key], but for a different type.
     */
    @UncheckedApi
    @JvmName("entry1")
    fun <T> entry(type: KType, key: String, defaultValue: () -> T): MutableProvider<T> = lock.withLock {
        var providerEntry: ProviderCompoundEntry<T>
        when (val existingEntry = entryMap[key]) {
            is ProviderCompoundEntry<*> -> {
                if (existingEntry.type != type)
                    throw IllegalArgumentException("An entry provider for $key already exists, but for a different type. (existing: ${existingEntry.type}, requested: $type)")
                
                @Suppress("UNCHECKED_CAST")
                providerEntry = existingEntry as ProviderCompoundEntry<T> // default value is ignored for existing provider entries
            }
            
            is DirectCompoundEntry<*> -> {
                providerEntry = existingEntry.toProviderEntry(type, defaultValue)
                entryMap[key] = providerEntry
            }
            
            null -> {
                providerEntry = ProviderCompoundEntry(type, defaultValue)
                entryMap[key] = providerEntry
            }
        }
        
        return providerEntry.valueProvider
    }
    
    /**
     * Gets the value under [key] as [type] [T] or null if it doesn't exist.
     */
    @Suppress("UNCHECKED_CAST")
    @UncheckedApi
    fun <T : Any> get(type: KType, key: String): T? = lock.withLock {
        return (entryMap[key] as CompoundEntry<T>?)?.get(type)
    }
    
    /**
     * Gets the value under [key] as [T] or null if it doesn't exist.
     */
    @OptIn(UncheckedApi::class)
    inline operator fun <reified T : Any> get(key: String): T? {
        return get(typeOf<T>().withNullability(true), key)
    }
    
    /**
     * Gets the value under [key] as [type] [T] or puts and returns
     * the value generated by [defaultValue] if it doesn't exist.
     */
    @UncheckedApi
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
    @OptIn(UncheckedApi::class)
    inline fun <reified T : Any> getOrPut(key: String, noinline defaultValue: () -> T): T {
        return getOrPut(typeOf<T>(), key, defaultValue)
    }
    
    /**
     * Puts all entries from [other] into this compound.
     */
    @Suppress("UNCHECKED_CAST")
    fun putAll(other: Compound) {
        if (other === this)
            return
        
        withLocksOrdered(lock, other.lock) {
            for ((key, otherEntry) in other.entryMap) {
                if (otherEntry.isEmpty())
                    continue
                
                val entry = entryMap[key]
                if (entry != null) {
                    otherEntry as CompoundEntry<Any>
                    entry as CompoundEntry<Any>
                    otherEntry.transferTo(entry)
                } else {
                    entryMap[key] = otherEntry.toDirectEntry() ?: continue
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
     * Creates a deep copy of this compound, copying all deserialized values using [Cbf.copy].
     */
    @OptIn(UncheckedApi::class)
    fun copy(): Compound = lock.withLock {
        copy { type, obj -> Cbf.copy(type, obj)!! }
    }
    
    /**
     * Creates a deep copy of this compound, copying all deserialized values using [copyFunc].
     */
    @UncheckedApi
    fun copy(copyFunc: (KType, Any) -> Any): Compound = lock.withLock {
        val entryMapCopy = HashMap<String, CompoundEntry<*>>()
        
        for ((key, entry) in entryMap) {
            if (entry.isEmpty())
                continue
            
            val type = entry.type
            val value = entry.cachedValue
            if (type != null && value != null) {
                entryMapCopy[key] = DirectCompoundEntry(type, copyFunc(type, value))
            } else {
                val entry = entry.toDirectEntry()
                if (entry != null) {
                    entryMapCopy[key] = entry
                }
            }
        }
        
        return Compound(entryMapCopy)
    }
    
    /**
     * Creates a shallow copy of this compound, not copying any values.
     */
    @OptIn(UncheckedApi::class)
    fun shallowCopy(): Compound = copy { _, obj -> obj }
    
    override fun toString(): String = lock.withLock {
        val builder = StringBuilder()
        builder.append("{")
        
        for (key in TreeSet(keys)) {
            val entry = entryMap[key]!!
            if (entry.type != null) {
                builder.append("\n\"$key\": ${entry.cachedValue}")
            } else {
                val hexStr = HexFormat.of().formatHex(entry.serialize())
                builder.append("\n\"$key\": (serialized) $hexStr")
            }
        }
        
        return builder.toString().replace("\n", "\n  ") + "\n}"
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
    
    internal object CompoundBinarySerializer : VersionedBinarySerializer<Compound>(2U) {
        
        override fun writeVersioned(obj: Compound, writer: ByteWriter) {
            var size = 0
            val temp = byteWriter {
                for ((key, entry) in obj.entryMap) {
                    val bytes = entry.serialize()
                    if (bytes == null)
                        continue
                    
                    writeString(key)
                    writeVarInt(bytes.size)
                    writeBytes(bytes)
                    size++
                }
            }
            
            writer.writeVarInt(size)
            writer.writeBytes(temp)
        }
        
        override fun readVersioned(version: UByte, reader: ByteReader): Compound {
            val mapSize = reader.readVarInt()
            val entryMap = HashMap<String, CompoundEntry<*>>(mapSize)
            
            repeat(mapSize) {
                val key = reader.readString()
                val length = reader.readVarInt()
                val bytes = reader.readBytes(length)
                
                // v1 wrote null values into the binary format, which we want to ignore now
                if (version == 1.toUByte() && length == 1 && bytes[0] == 0.toByte())
                    return@repeat
                
                entryMap[key] = DirectCompoundEntry<Any>(bytes)
            }
            
            return Compound(entryMap)
        }
        
        override fun copyNonNull(obj: Compound): Compound {
            return obj.copy()
        }
        
    }
    
}

/**
 * Creates a [MutableProvider] of non-null type [T] that is linked to the value under [key] of this provider's [Compound].
 * If the compound does not have an entry under [key], [defaultValue] is used to create it lazily.
 * If there is already an entry provider for [key] that matches [T], [defaultValue] will be ignored and the existing provider will be returned.
 *
 * On resolving, the returned provider can throw [IllegalArgumentException] if there already is an entry provider for [key], but for a different type.
 *
 * @see Compound.entry
 */
@JvmName("entry0")
inline fun <reified T : Any> Provider<Compound>.entry(key: String, noinline defaultValue: () -> T): MutableProvider<T> =
    flatMapMutable { it.entry<T>(key, defaultValue) }

/**
 * Creates a [MutableProvider] of type [T] that is linked to the value under [key] of this provider's [Compound].
 * If the compound does not have an entry under [key], [defaultValue] is used to create it lazily.
 * If there is already an entry provider for [key] that matches [T], [defaultValue] will be ignored and the existing provider will be returned.
 *
 * On resolving, the returned provider can throw [IllegalArgumentException] if there already is an entry provider for [key], but for a different type.
 *
 * @see Compound.entry
 */
@JvmName("entry1")
inline fun <reified T : Any> Provider<Compound?>.entry(key: String, noinline defaultValue: () -> T? = { null }): MutableProvider<T?> =
    flatMapMutable { it?.entry<T>(key, defaultValue) ?: mutableProvider(null) }