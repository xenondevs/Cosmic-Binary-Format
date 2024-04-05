@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package xyz.xenondevs.cbf

import xyz.xenondevs.cbf.Compound.CompoundBinaryAdapter
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.adapter.ComplexBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.BooleanArrayBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.BooleanBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.ByteArrayBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.ByteBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.CharArrayBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.CharBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.CollectionBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.DoubleArrayBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.DoubleBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.EnumBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.FloatArrayBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.FloatBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.IntArrayBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.IntBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.LongArrayBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.LongBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.MapBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.PairBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.ShortArrayBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.ShortBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.StringArrayBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.StringBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.TripleBinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.UUIDBinaryAdapter
import xyz.xenondevs.cbf.instancecreator.InstanceCreator
import xyz.xenondevs.cbf.instancecreator.impl.EnumMapInstanceCreator
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.cbf.security.CBFSecurityException
import xyz.xenondevs.cbf.security.CBFSecurityManager
import xyz.xenondevs.commons.reflection.classifierClass
import xyz.xenondevs.commons.reflection.createStarProjectedType
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.superclasses
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
object CBF {
    
    var securityManager: CBFSecurityManager? = null
        set(value) {
            check(field == null) { "CBFSecurityManager has already been set" }
            field = value
        }
    
    private val binaryAdapters = HashMap<KType, BinaryAdapter<*>>()
    private val binaryAdaptersByClass = HashMap<KClass<*>, HashMap<KType, BinaryAdapter<*>>>()
    private val binaryHierarchyAdapters = HashMap<KType, BinaryAdapter<*>>()
    private val instanceCreators = HashMap<KClass<*>, InstanceCreator<*>>()
    
    init {
        // default binary adapters
        registerBinaryAdapter(BooleanBinaryAdapter)
        registerBinaryAdapter(BooleanArrayBinaryAdapter)
        registerBinaryAdapter(ByteBinaryAdapter)
        registerBinaryAdapter(ByteArrayBinaryAdapter)
        registerBinaryAdapter(ShortBinaryAdapter)
        registerBinaryAdapter(ShortArrayBinaryAdapter)
        registerBinaryAdapter(IntBinaryAdapter)
        registerBinaryAdapter(IntArrayBinaryAdapter)
        registerBinaryAdapter(LongBinaryAdapter)
        registerBinaryAdapter(LongArrayBinaryAdapter)
        registerBinaryAdapter(FloatBinaryAdapter)
        registerBinaryAdapter(FloatArrayBinaryAdapter)
        registerBinaryAdapter(DoubleBinaryAdapter)
        registerBinaryAdapter(DoubleArrayBinaryAdapter)
        registerBinaryAdapter(CharBinaryAdapter)
        registerBinaryAdapter(CharArrayBinaryAdapter)
        registerBinaryAdapter(StringBinaryAdapter)
        registerBinaryAdapter(StringArrayBinaryAdapter)
        registerBinaryAdapter(UUIDBinaryAdapter)
        registerBinaryAdapter(PairBinaryAdapter)
        registerBinaryAdapter(TripleBinaryAdapter)
        registerBinaryAdapter(CompoundBinaryAdapter)
        
        // default binary hierarchy adapters
        registerBinaryHierarchyAdapter(EnumBinaryAdapter)
        registerBinaryHierarchyAdapter(CollectionBinaryAdapter)
        registerBinaryHierarchyAdapter(MapBinaryAdapter)
        
        // default instance creators
        registerInstanceCreator(EnumMap::class, EnumMapInstanceCreator)
    }
    
    /**
     * Registers a new [BinaryAdapter] for the reified type [T].
     */
    inline fun <reified T : Any> registerBinaryAdapter(adapter: BinaryAdapter<T>) {
        registerBinaryAdapter(typeOf<T>(), adapter)
    }
    
    /**
     * Registers a new [BinaryAdapter] for the [type].
     */
    fun <T : Any> registerBinaryAdapter(type: KType, adapter: BinaryAdapter<T>) {
        if (securityManager?.canRegisterAdapter(type, adapter) == false)
            throw CBFSecurityException()
        
        binaryAdapters[type] = adapter
        binaryAdaptersByClass.getOrPut(type.classifierClass!!, ::HashMap)[type] = adapter
    }
    
    /**
     * Registers a new [BinaryAdapter] for the reified type [T].
     */
    inline fun <reified T : Any> registerBinaryHierarchyAdapter(adapter: BinaryAdapter<T>) {
        registerBinaryHierarchyAdapter(typeOf<T>(), adapter)
    }
    
    /**
     * Registers a new [BinaryAdapter] for the [type].
     */
    fun <T : Any> registerBinaryHierarchyAdapter(type: KType, adapter: BinaryAdapter<T>) {
        if (securityManager?.canRegisterHierarchyAdapter(type, adapter) == false)
            throw CBFSecurityException()
        
        binaryHierarchyAdapters[type] = adapter
    }
    
    /**
     * Registers a new [InstanceCreator] for the reified type [T].
     */
    inline fun <reified T : Any> registerInstanceCreator(instanceCreator: InstanceCreator<T>) {
        registerInstanceCreator(T::class, instanceCreator)
    }
    
    
    /**
     * Registers a new [InstanceCreator] for the [clazz].
     */
    fun <T : Any> registerInstanceCreator(clazz: KClass<T>, creator: InstanceCreator<T>) {
        if (securityManager?.canRegisterInstanceCreator(clazz, creator) == false)
            throw CBFSecurityException()
        
        instanceCreators[clazz] = creator
    }
    
    /**
     * Reads the reified type [T] from the [buf]. Can return null if null was written.
     */
    inline fun <reified T : Any> read(buf: ByteReader): T? {
        return read(typeOf<T>(), buf)
    }
    
    /**
     * Reads the reified type [T] from the [byte array][bytes] Can return null if null was written.
     */
    inline fun <reified T : Any> read(bytes: ByteArray): T? {
        return read(typeOf<T>(), bytes)
    }
    
    /**
     * Reads the [type] from the [buf]. Can return null if null was written.
     */
    fun <T : Any> read(type: KType, buf: ByteReader): T? {
        val b = buf.readUnsignedByte()
        if (b != 0.toUByte()) {
            val nonNullType = type.withNullability(false)
            return when (val adapter = getBinaryAdapterExact<T>(nonNullType)) {
                is ComplexBinaryAdapter<T> -> adapter.read(type, b, buf)
                else -> adapter.read(type, buf)
            }
        }
        
        return null
    }
    
    /**
     * Reads the [type] from the [byte array][bytes]. Can return null if null was written.
     */
    fun <T : Any> read(type: KType, bytes: ByteArray): T? {
        return read(type, ByteReader.fromStream(ByteArrayInputStream(bytes)))
    }
    
    /**
     * Writes the given [obj] as the reified type [T] to the given [writer].
     */
    inline fun <reified T : Any> write(obj: T?, writer: ByteWriter) {
        write(obj, typeOf<T>(), writer)
    }
    
    /**
     * Writes the [obj] as [type] to the given [writer].
     *
     * If the [type] is null, a star projected type for the [obj]'s class will be used.
     */
    fun write(obj: Any?, type: KType?, writer: ByteWriter) {
        if (obj != null) {
            write(obj, type, writer)
        } else writer.writeBoolean(false)
    }
    
    /**
     * Writes a non-null [obj] of the given [type] to the given [writer].
     *
     * If the [type] is null, a star projected type for the [obj]'s class will be used.
     */
    @JvmName("writeNonNull")
    fun write(obj: Any, type: KType?, writer: ByteWriter) {
        val nonNullType = type
            ?.withNullability(false)
            ?: obj::class.createStarProjectedType()
        
        val adapter = getBinaryAdapterExact<Any>(nonNullType)
        if (adapter !is ComplexBinaryAdapter<*>)
            writer.writeBoolean(true)
        adapter.write(obj, nonNullType, writer)
    }
    
    /**
     * Writes the given [obj] as the reified type [T] to a new [ByteArray] and returns it.
     */
    inline fun <reified T : Any> write(obj: T): ByteArray {
        return write(obj, typeOf<T>())
    }
    
    /**
     * Writes the [obj] as [type] to a new [ByteArray] and returns it.
     *
     * If the [type] is null, a star projected type for the [obj]'s class will be used.
     */
    fun write(obj: Any?, type: KType?): ByteArray {
        val out = ByteArrayOutputStream()
        write(obj, type, ByteWriter.fromStream(out))
        return out.toByteArray()
    }
    
    /**
     * Creates a new instance of the reified type [T].
     *
     * Returns null if no instance creator is registered for the type or if the type has no no-arg constructor.
     */
    inline fun <reified T : Any> createInstance(): T? {
        return createInstance(typeOf<T>())
    }
    
    /**
     * Creates a new instance of the [type] or null if no instance creator is registered for the type or if the type has no no-arg constructor.
     */
    fun <T : Any> createInstance(type: KType): T? {
        val clazz = type.classifierClass!!
        
        val creator = instanceCreators[clazz]
        if (creator != null)
            return creator.createInstance(type) as T
        
        return clazz.constructors
            .firstOrNull { it.parameters.isEmpty() }
            ?.call() as T?
    }
    
    /**
     * Copies the given [obj] as the reified type [T].
     */
    inline fun <reified T : Any> copy(obj: T): T {
        return copy(obj, typeOf<T>())
    }
    
    /**
     * Copies the given [obj] as the [type].
     */
    fun <T : Any> copy(obj: T, type: KType): T {
        val binaryAdapter = getBinaryAdapter<T>(type)
        return binaryAdapter.copy(obj, type)
    }
    
    /**
     * Gets the registered [BinaryAdapter] for the [type] without nullability.
     */
    internal fun <T : Any> getBinaryAdapter(type: KType): BinaryAdapter<T> {
        return getBinaryAdapterExact(type.withNullability(false))
    }
    
    /**
     * Gets the registered [BinaryAdapter] for that exact [type].
     */
    private fun <T : Any> getBinaryAdapterExact(type: KType): BinaryAdapter<T> {
        var adapter: BinaryAdapter<*>? = binaryAdapters[type]
        if (adapter != null)
            return adapter as BinaryAdapter<T>
        
        val clazz = type.classifierClass!!
        
        adapter = binaryAdaptersByClass[clazz]?.entries?.firstOrNull { type.isSubtypeOf(it.key) }?.value
        if (adapter != null)
            return adapter as BinaryAdapter<T>
        
        adapter = binaryHierarchyAdapters.entries.firstOrNull { type.isSubtypeOf(it.key) }?.value
        if (adapter != null)
            return adapter as BinaryAdapter<T>
        
        throw IllegalStateException("No binary adapter registered for $clazz")
    }
    
}