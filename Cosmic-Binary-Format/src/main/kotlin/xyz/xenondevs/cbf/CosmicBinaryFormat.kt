@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package xyz.xenondevs.cbf

import xyz.xenondevs.cbf.Compound.CompoundBinaryAdapter
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.adapter.impl.*
import xyz.xenondevs.cbf.instancecreator.InstanceCreator
import xyz.xenondevs.cbf.instancecreator.impl.EnumMapInstanceCreator
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.cbf.security.CBFSecurityException
import xyz.xenondevs.cbf.security.CBFSecurityManager
import xyz.xenondevs.commons.reflection.classifierClass
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
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
    
    inline fun <reified T : Any> registerBinaryAdapter(adapter: BinaryAdapter<T>) {
        registerBinaryAdapter(typeOf<T>(), adapter)
    }
    
    fun <T : Any> registerBinaryAdapter(type: KType, adapter: BinaryAdapter<T>) {
        if (securityManager?.canRegisterAdapter(type, adapter) == false)
            throw CBFSecurityException()
        
        binaryAdapters[type] = adapter
        binaryAdaptersByClass.getOrPut(type.classifierClass!!, ::HashMap)[type] = adapter
    }
    
    inline fun <reified T : Any> registerBinaryHierarchyAdapter(adapter: BinaryAdapter<T>) {
        registerBinaryHierarchyAdapter(typeOf<T>(), adapter)
    }
    
    fun <T : Any> registerBinaryHierarchyAdapter(type: KType, adapter: BinaryAdapter<T>) {
        if (securityManager?.canRegisterHierarchyAdapter(type, adapter) == false)
            throw CBFSecurityException()
        
        binaryHierarchyAdapters[type] = adapter
    }
    
    inline fun <reified T : Any> registerInstanceCreator(instanceCreator: InstanceCreator<T>) {
        registerInstanceCreator(T::class, instanceCreator)
    }
    
    fun <T : Any> registerInstanceCreator(clazz: KClass<T>, creator: InstanceCreator<T>) {
        if (securityManager?.canRegisterInstanceCreator(clazz, creator) == false)
            throw CBFSecurityException()
        
        instanceCreators[clazz] = creator
    }
    
    inline fun <reified T> read(buf: ByteReader): T? {
        return read(typeOf<T>(), buf)
    }
    
    inline fun <reified T> read(bytes: ByteArray): T? {
        return read(typeOf<T>(), bytes)
    }
    
    fun <T> read(type: KType, buf: ByteReader): T? {
        if (buf.readBoolean()) {
            val nonNullType = type.withNullability(false)
            val adapter = getBinaryAdapter<T>(nonNullType)
            return adapter.read(nonNullType, buf)
        }
        
        return null
    }
    
    fun <T> read(type: KType, bytes: ByteArray): T? {
        return read(type, ByteReader.fromStream(ByteArrayInputStream(bytes)))
    }
    
    inline fun <reified T> write(obj: T, writer: ByteWriter) {
        write(obj, typeOf<T>(), writer)
    }
    
    fun write(obj: Any?, type: KType?, writer: ByteWriter) {
        if (obj != null) {
            write(obj, type, writer)
        } else writer.writeBoolean(false)
    }
    
    @JvmName("write1")
    fun write(obj: Any, type: KType?, writer: ByteWriter) {
        writer.writeBoolean(true)
        
        val nonNullType = type?.withNullability(false) ?: obj::class.createType()
        val adapter = getBinaryAdapter<Any>(nonNullType)
        adapter.write(obj, nonNullType, writer)
    }
    
    inline fun <reified T> write(obj: T): ByteArray {
        return write(obj, typeOf<T>())
    }
    
    fun write(obj: Any?, type: KType?): ByteArray {
        val out = ByteArrayOutputStream()
        write(obj, type, ByteWriter.fromStream(out))
        return out.toByteArray()
    }
    
    fun <T> createInstance(type: KType): T? {
        val clazz = type.classifierClass!!
        
        val creator = instanceCreators[clazz]
        if (creator != null)
            return creator.createInstance(type) as T
        
        return clazz.constructors
            .firstOrNull { it.parameters.isEmpty() }
            ?.call() as T?
    }
    
    private fun <R> getBinaryAdapter(type: KType): BinaryAdapter<R> {
        var adapter: BinaryAdapter<*>? = binaryAdapters[type]
        if (adapter != null)
            return adapter as BinaryAdapter<R>
        
        val clazz = type.classifierClass!!
        
        adapter = binaryAdaptersByClass[clazz]?.entries?.firstOrNull { type.isSubtypeOf(it.key) }?.value
        if (adapter != null)
            return adapter as BinaryAdapter<R>
        
        adapter = binaryHierarchyAdapters.entries.firstOrNull { it.key.isSubtypeOf(type) }?.value
        if (adapter != null)
            return adapter as BinaryAdapter<R>
        
        throw IllegalStateException("No binary adapter registered for $clazz")
    }
    
}