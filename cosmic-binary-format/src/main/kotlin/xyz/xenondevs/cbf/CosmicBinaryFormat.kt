@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package xyz.xenondevs.cbf

import xyz.xenondevs.cbf.Compound.CompoundBinarySerializer
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.cbf.serializer.BinarySerializer
import xyz.xenondevs.cbf.serializer.BinarySerializerFactory
import xyz.xenondevs.cbf.serializer.BooleanArrayBinarySerializer
import xyz.xenondevs.cbf.serializer.BooleanBinarySerializer
import xyz.xenondevs.cbf.serializer.ByteArrayBinarySerializer
import xyz.xenondevs.cbf.serializer.ByteBinarySerializer
import xyz.xenondevs.cbf.serializer.CharArrayBinarySerializer
import xyz.xenondevs.cbf.serializer.CharBinarySerializer
import xyz.xenondevs.cbf.serializer.CollectionBinarySerializer
import xyz.xenondevs.cbf.serializer.DoubleArrayBinarySerializer
import xyz.xenondevs.cbf.serializer.DoubleBinarySerializer
import xyz.xenondevs.cbf.serializer.EnumBinarySerializer
import xyz.xenondevs.cbf.serializer.FloatArrayBinarySerializer
import xyz.xenondevs.cbf.serializer.FloatBinarySerializer
import xyz.xenondevs.cbf.serializer.IntArrayBinarySerializer
import xyz.xenondevs.cbf.serializer.IntBinarySerializer
import xyz.xenondevs.cbf.serializer.KotlinUuidBinarySerializer
import xyz.xenondevs.cbf.serializer.LongArrayBinarySerializer
import xyz.xenondevs.cbf.serializer.LongBinarySerializer
import xyz.xenondevs.cbf.serializer.MapBinarySerializer
import xyz.xenondevs.cbf.serializer.PairBinarySerializer
import xyz.xenondevs.cbf.serializer.ShortArrayBinarySerializer
import xyz.xenondevs.cbf.serializer.ShortBinarySerializer
import xyz.xenondevs.cbf.serializer.StringArrayBinarySerializer
import xyz.xenondevs.cbf.serializer.StringBinarySerializer
import xyz.xenondevs.cbf.serializer.TripleBinarySerializer
import xyz.xenondevs.cbf.serializer.UUIDBinarySerializer
import xyz.xenondevs.cbf.serializer.read
import xyz.xenondevs.cbf.serializer.write
import xyz.xenondevs.commons.reflection.equalsIgnoreNullability
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object CBF {
    
    private var securityManager: CBFSecurityManager? = null
    private val factories = ArrayList<BinarySerializerFactory>()
    private val cachedSerializers = ConcurrentHashMap<KType, BinarySerializer<*>>()
    
    init {
        registerSerializer(BooleanBinarySerializer)
        registerSerializer(BooleanArrayBinarySerializer)
        registerSerializer(ByteBinarySerializer)
        registerSerializer(ByteArrayBinarySerializer)
        registerSerializer(ShortBinarySerializer)
        registerSerializer(ShortArrayBinarySerializer)
        registerSerializer(IntBinarySerializer)
        registerSerializer(IntArrayBinarySerializer)
        registerSerializer(LongBinarySerializer)
        registerSerializer(LongArrayBinarySerializer)
        registerSerializer(FloatBinarySerializer)
        registerSerializer(FloatArrayBinarySerializer)
        registerSerializer(DoubleBinarySerializer)
        registerSerializer(DoubleArrayBinarySerializer)
        registerSerializer(CharBinarySerializer)
        registerSerializer(CharArrayBinarySerializer)
        registerSerializer(StringBinarySerializer)
        registerSerializer(StringArrayBinarySerializer)
        registerSerializer(UUIDBinarySerializer)
        registerSerializer(KotlinUuidBinarySerializer)
        registerSerializer(CompoundBinarySerializer)
        
        registerSerializerFactory(PairBinarySerializer)
        registerSerializerFactory(TripleBinarySerializer)
        registerSerializerFactory(EnumBinarySerializer)
        registerSerializerFactory(CollectionBinarySerializer)
        registerSerializerFactory(MapBinarySerializer)
        
        addMapCreator(::HashMap)
        addMapCreator(::LinkedHashMap)
        addMapCreator(::WeakHashMap)
        addMapCreator(::IdentityHashMap)
        addMapCreator(::ConcurrentHashMap)
        addMapCreator { TreeMap() }
        
        addCollectionCreator(::ArrayList)
        addCollectionCreator { LinkedList() }
        addCollectionCreator { CopyOnWriteArrayList() }
        addCollectionCreator(::HashSet)
        addCollectionCreator(::LinkedHashSet)
        addCollectionCreator { TreeSet() }
        addCollectionCreator { CopyOnWriteArraySet() }
    }
    
    /**
     * Sets the [CBFSecurityManager] that decides whether a serializer for a given type can be created.
     *
     * @throws IllegalStateException if a security manager has already been set
     */
    fun setSecurityManager(securityManager: CBFSecurityManager) {
        if (this.securityManager != null)
            throw IllegalStateException("CBFSecurityManager has already been set")
        this.securityManager = securityManager
    }
    
    /**
     * Registers a [BinarySerializer] that serializes, deserializes, and copies the exact type [T?][T].
     */
    inline fun <reified T : Any> registerSerializer(serializer: BinarySerializer<T?>) {
        val serializerType = typeOf<T>()
        val factory = object : BinarySerializerFactory {
            override fun create(type: KType): BinarySerializer<*>? =
                if (type.equalsIgnoreNullability(serializerType)) serializer else null
        }
        registerSerializerFactory(factory)
    }
    
    /**
     * Registers a [BinarySerializerFactory] that will be queried for serializers.
     */
    fun registerSerializerFactory(factory: BinarySerializerFactory) {
        this.factories += factory
    }
    
    /**
     * Reads a value of the reified type [T?][T] from the [reader]. 
     * Can return null if null was written.
     *
     * @throws IllegalStateException if no serializer is registered for the type
     * @throws CBFSecurityException if the creation of a serializer was prevented by the security manager
     */
    inline fun <reified T : Any> read(reader: ByteReader): T? {
        return getSerializer<T?>().read(reader)
    }
    
    /**
     * Reads a value of the reified type [T?][T] from [bytes].
     * Can return null if null was written.
     *
     * @throws IllegalArgumentException if [strict] is true and the byte array contains more data than was read
     * @throws IllegalStateException if no serializer is registered for the type
     * @throws CBFSecurityException if the creation of a serializer was prevented by the security manager
     */
    inline fun <reified T : Any> read(bytes: ByteArray, strict: Boolean = true): T? {
        return getSerializer<T?>().read(bytes, strict)
    }
    
    /**
     * Reads a value of the type [T?][T] ([type]) from the [reader]. 
     * Can return null if null was written.
     * 
     * @throws IllegalStateException if no serializer is registered for the type
     * @throws CBFSecurityException if the creation of a serializer was prevented by the security manager
     */
    @UncheckedApi
    fun <T : Any> read(type: KType, reader: ByteReader): T? {
        return getSerializer<T?>(type).read(reader)
    }
    
    /**
     * Reads a value of the type [T?][T] ([type]) from [bytes].
     * Can return null if null was written.
     * 
     * @throws IllegalArgumentException if [strict] is true and the byte array contains more data than was read
     * @throws IllegalStateException if no serializer is registered for the type
     * @throws CBFSecurityException if the creation of a serializer was prevented by the security manager
     */
    @UncheckedApi
    fun <T : Any> read(type: KType, bytes: ByteArray, strict: Boolean = true): T? {
        return getSerializer<T?>(type).read(bytes, strict)
    }
    
    /**
     * Writes [value] of the reified type [T?][T] to the [writer].
     *
     * @throws IllegalStateException if no serializer is registered for the type
     * @throws CBFSecurityException if the creation of a serializer was prevented by the security manager
     */
    inline fun <reified T : Any> write(value: T?, writer: ByteWriter) {
        getSerializer<T?>().write(value, writer)
    }
    
    /**
     * Writes [value] of the reified type [T?][T] to a [ByteArray].
     *
     * @throws IllegalStateException if no serializer is registered for the type
     * @throws CBFSecurityException if the creation of a serializer was prevented by the security manager
     */
    inline fun <reified T : Any> write(value: T?): ByteArray {
        return getSerializer<T?>().write(value)
    }
    
    /**
     * Writes [value] of the type [T?][T] ([type]) to the [writer].
     * 
     * @throws IllegalStateException if no serializer is registered for the type
     * @throws CBFSecurityException if the creation of a serializer was prevented by the security manager
     */
    @UncheckedApi
    fun <T : Any> write(type: KType, value: T?, writer: ByteWriter) {
        getSerializer<T?>(type).write(value, writer)
    }
    
    /**
     * Writes [value] of the type [T?][T] ([type]) to a [ByteArray].
     * 
     * @throws IllegalStateException if no serializer is registered for the type
     * @throws CBFSecurityException if the creation of a serializer was prevented by the security manager
     */
    @UncheckedApi
    fun <T : Any> write(type: KType, value: T?): ByteArray {
        return getSerializer<T?>(type).write(value)
    }
    
    /**
     * Creates a deep copy of [value].
     *
     * @throws IllegalStateException if no serializer is registered for the type
     * @throws CBFSecurityException if the creation of a serializer was prevented by the security manager
     */
    inline fun <reified T> copy(value: T): T {
        return getSerializer<T>().copy(value)
    }
    
    /**
     * Creates a deep copy of [value] of the type [T] ([type]).
     * 
     * @throws IllegalStateException if no copier is registered for the type
     * @throws CBFSecurityException if the creation of a copier was prevented by the security manager
     */
    @UncheckedApi
    fun <T> copy(type: KType, value: T): T {
        return getSerializer<T>(type).copy(value)
    }
    
    /**
     * Gets a [BinarySerializer] for the reified type [T].
     *
     * @throws IllegalStateException if no serializer is registered for the type
     * @throws CBFSecurityException if the creation of a serializer was prevented by the security manager
     */
    @OptIn(UncheckedApi::class)
    inline fun <reified T> getSerializer(): BinarySerializer<T> {
        return getSerializer(typeOf<T>())
    }
    
    /**
     * Gets a [BinarySerializer] for the given [type].
     *
     * @throws IllegalStateException if no serializer is registered for the type
     * @throws CBFSecurityException if the creation of a serializer was prevented by the security manager
     */
    @UncheckedApi
    @Suppress("UNCHECKED_CAST")
    fun <T> getSerializer(type: KType): BinarySerializer<T> {
        // use getOrPut instead of computeIfAbsent:
        // BinarySerializerFactory#create may recursively call getSerializer, which computeIfAbsent does not allow.
        // Using getOrPut instead may cause multiple serializer for the same type to be created, but that is not a problem.
        return cachedSerializers.getOrPut(type) {
            val serializer = factories.asReversed().firstNotNullOfOrNull { it.create(type) }
            if (serializer == null)
                throw IllegalStateException("No binary serializer registered for $type")
            if (securityManager?.isAllowed(type, serializer) == false)
                throw CBFSecurityException(type, serializer)
            return@getOrPut serializer
        } as BinarySerializer<T>
    }
    
    /**
     * Configures which [Enum] types should be serialized by their ordinal value instead of their name,
     * when using the built-in enum serializers.
     */
    fun addOrdinalEnums(vararg classes: KClass<out Enum<*>>) {
        EnumBinarySerializer.ordinalEnums.addAll(classes)
    }
    
    /**
     * Adds a way to create a specific [MutableCollection] type when using the built-in collection serializer.
     */
    inline fun <reified C : MutableCollection<Any?>> addCollectionCreator(
        noinline creator: (size: Int) -> C
    ) {
        addCollectionCreator(C::class, creator)
    }
    
    @PublishedApi
    internal fun <C : MutableCollection<Any?>> addCollectionCreator(
        clazz: KClass<out C>,
        creator: (size: Int) -> C
    ) {
        CollectionBinarySerializer.collectionCreators[clazz] = creator
    }
    
    /**
     * Adds a way to create a specific [MutableMap] type when using the built-in map serializer.
     */
    inline fun <reified M : MutableMap<Any?, Any?>> addMapCreator(
        noinline creator: (size: Int) -> M
    ) {
        addMapCreator(M::class, creator)
    }
    
    @PublishedApi
    internal fun <M : MutableMap<Any?, Any?>> addMapCreator(
        clazz: KClass<out M>,
        creator: (size: Int) -> M
    ) {
        MapBinarySerializer.mapCreators[clazz] = creator
    }
    
}