package xyz.xenondevs.cbf

import xyz.xenondevs.cbf.Compound.CompoundBinaryAdapter
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.adapter.default.*
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.cbf.instancecreator.InstanceCreator
import xyz.xenondevs.cbf.instancecreator.default.EnumMapInstanceCreator
import xyz.xenondevs.cbf.security.CBFSecurityException
import xyz.xenondevs.cbf.security.CBFSecurityManager
import xyz.xenondevs.cbf.util.representedKClass
import xyz.xenondevs.cbf.util.type
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

@Suppress("UNCHECKED_CAST")
object CBF {
    
    var securityManager: CBFSecurityManager? = null
        set(value) {
            check(field == null) { "CBFSecurityManager has already been set" }
            field = value
        }
    
    private val binaryAdapters = HashMap<KClass<*>, BinaryAdapter<*>>()
    private val binaryHierarchyAdapters = HashMap<KClass<*>, BinaryAdapter<*>>()
    
    private val instanceCreators = HashMap<KClass<*>, InstanceCreator<*>>()
    
    init {
        // default binary adapters
        registerBinaryAdapter(Boolean::class, BooleanBinaryAdapter)
        registerBinaryAdapter(BooleanArray::class, BooleanArrayBinaryAdapter)
        registerBinaryAdapter(Byte::class, ByteBinaryAdapter)
        registerBinaryAdapter(ByteArray::class, ByteArrayBinaryAdapter)
        registerBinaryAdapter(Short::class, ShortBinaryAdapter)
        registerBinaryAdapter(ShortArray::class, ShortArrayBinaryAdapter)
        registerBinaryAdapter(Int::class, IntBinaryAdapter)
        registerBinaryAdapter(IntArray::class, IntArrayBinaryAdapter)
        registerBinaryAdapter(Long::class, LongBinaryAdapter)
        registerBinaryAdapter(LongArray::class, LongArrayBinaryAdapter)
        registerBinaryAdapter(Float::class, FloatBinaryAdapter)
        registerBinaryAdapter(FloatArray::class, FloatArrayBinaryAdapter)
        registerBinaryAdapter(Double::class, DoubleBinaryAdapter)
        registerBinaryAdapter(DoubleArray::class, DoubleArrayBinaryAdapter)
        registerBinaryAdapter(Char::class, CharBinaryAdapter)
        registerBinaryAdapter(CharArray::class, CharArrayBinaryAdapter)
        registerBinaryAdapter(String::class, StringBinaryAdapter)
        registerBinaryAdapter(Array<String>::class, StringArrayBinaryAdapter)
        registerBinaryAdapter(UUID::class, UUIDBinaryAdapter)
        registerBinaryAdapter(Pair::class, PairBinaryAdapter)
        registerBinaryAdapter(Triple::class, TripleBinaryAdapter)
        registerBinaryAdapter(Compound::class, CompoundBinaryAdapter)
        
        // default binary hierarchy adapters
        registerBinaryHierarchyAdapter(Enum::class, EnumBinaryAdapter)
        registerBinaryHierarchyAdapter(Collection::class, CollectionBinaryAdapter)
        registerBinaryHierarchyAdapter(Map::class, MapBinaryAdapter)
        
        // default instance creators
        registerInstanceCreator(EnumMap::class, EnumMapInstanceCreator)
    }
    
    fun <T : Any> registerBinaryAdapter(clazz: KClass<T>, adapter: BinaryAdapter<T>) {
        if (securityManager?.canRegisterAdapter(clazz, adapter) == false)
            throw CBFSecurityException()
        
        binaryAdapters[clazz] = adapter
    }
    
    fun <T : Any> registerBinaryHierarchyAdapter(clazz: KClass<T>, adapter: BinaryAdapter<T>) {
        if (securityManager?.canRegisterHierarchyAdapter(clazz, adapter) == false)
            throw CBFSecurityException()
        
        binaryHierarchyAdapters[clazz] = adapter
    }
    
    fun <T : Any> registerInstanceCreator(clazz: KClass<T>, creator: InstanceCreator<T>) {
        if (securityManager?.canRegisterInstanceCreator(clazz, creator) == false)
            throw CBFSecurityException()
        
        instanceCreators[clazz] = creator
    }
    
    inline fun <reified T> read(buf: ByteReader): T? {
        return read(type<T>(), buf)
    }
    
    inline fun <reified T> read(bytes: ByteArray): T? {
        return read(type<T>(), bytes)
    }
    
    fun <T> read(type: Type, buf: ByteReader): T? {
        if (buf.readBoolean()) {
            val clazz = type.representedKClass
            val typeAdapter = getBinaryAdapter<T>(clazz)
            return typeAdapter.read(type, buf)
        }
        
        return null
    }
    
    fun <T> read(type: Type, bytes: ByteArray): T? {
        return read(type, ByteReader.fromStream(ByteArrayInputStream(bytes)))
    }
    
    fun write(obj: Any?, writer: ByteWriter) {
        if (obj != null) {
            writer.writeBoolean(true)
            
            val clazz = obj::class
            val typeAdapter = getBinaryAdapter<Any>(clazz)
            typeAdapter.write(obj, writer)
        } else writer.writeBoolean(false)
    }
    
    fun write(obj: Any?): ByteArray {
        val out = ByteArrayOutputStream()
        write(obj, ByteWriter.fromStream(out))
        return out.toByteArray()
    }
    
    fun <T> createInstance(type: Type): T? {
        val clazz = type.representedKClass
        
        val creator = instanceCreators[clazz]
        if (creator != null)
            return creator.createInstance(type) as T
        
        return clazz.constructors
            .firstOrNull { it.parameters.isEmpty() }
            ?.call() as T?
    }
    
    private fun <R> getBinaryAdapter(clazz: KClass<*>): BinaryAdapter<R> {
        val typeAdapter: BinaryAdapter<*>? =
            if (clazz in binaryAdapters)
                binaryAdapters[clazz]
            else binaryHierarchyAdapters.entries.firstOrNull { it.key.isSuperclassOf(clazz) }?.value
        
        if (typeAdapter == null)
            throw IllegalStateException("No binary adapter registered for $clazz")
        
        return typeAdapter as BinaryAdapter<R>
    }
    
    
}