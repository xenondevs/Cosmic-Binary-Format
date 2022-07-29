package xyz.xenondevs.cbf

import xyz.xenondevs.cbf.Compound.CompoundBinaryAdapter
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.adapter.default.*
import xyz.xenondevs.cbf.buffer.ByteBuffer
import xyz.xenondevs.cbf.buffer.ByteBufferProvider
import xyz.xenondevs.cbf.instancecreator.InstanceCreator
import xyz.xenondevs.cbf.instancecreator.default.EnumMapInstanceCreator
import xyz.xenondevs.cbf.util.representedKClass
import xyz.xenondevs.cbf.util.type
import java.lang.reflect.Type
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

@Suppress("UNCHECKED_CAST")
object CBF {
    
    var defaultBufferProvider: ByteBufferProvider? = null
    
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
        binaryAdapters[clazz] = adapter
    }
    
    fun <T : Any> registerBinaryHierarchyAdapter(clazz: KClass<T>, adapter: BinaryAdapter<T>) {
        binaryHierarchyAdapters[clazz] = adapter
    }
    
    fun <T : Any> registerInstanceCreator(clazz: KClass<T>, creator: InstanceCreator<T>) {
        instanceCreators[clazz] = creator
    }
    
    inline fun <reified T> read(buf: ByteBuffer): T? {
        return read(type<T>(), buf)
    }
    
    inline fun <reified T> read(bytes: ByteArray): T? {
        return read(type<T>(), bytes)
    }
    
    fun <T> read(type: Type, buf: ByteBuffer): T? {
        if (buf.readBoolean()) {
            val clazz = type.representedKClass
            val typeAdapter = getBinaryAdapter<T>(clazz)
            return typeAdapter.read(type, buf)
        }
        
        return null
    }
    
    fun <T> read(type: Type, bytes: ByteArray): T? {
        val wrapped = wrappedBuffer(bytes)
        return read(type, wrapped)
    }
    
    fun write(obj: Any?, buf: ByteBuffer) {
        if (obj != null) {
            buf.writeBoolean(true)
            
            val clazz = obj::class
            val typeAdapter = getBinaryAdapter<Any>(clazz)
            typeAdapter.write(obj, buf)
        } else buf.writeBoolean(false)
    }
    
    fun write(obj: Any?): ByteArray {
        val buf = buffer()
        write(obj, buf)
        return buf.toByteArray()
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
    
    fun buffer() = defaultBufferProvider?.getBuffer()
        ?: throw IllegalStateException("No default buffer provider set")
    
    fun wrappedBuffer(bytes: ByteArray) = defaultBufferProvider?.wrappedBuffer(bytes)
        ?: throw IllegalStateException("No default buffer provider set")
    
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