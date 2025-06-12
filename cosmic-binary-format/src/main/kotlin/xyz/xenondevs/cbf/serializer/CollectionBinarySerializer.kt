package xyz.xenondevs.cbf.serializer

import xyz.xenondevs.cbf.Cbf
import xyz.xenondevs.cbf.UncheckedApi
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.cbf.io.byteWriter
import xyz.xenondevs.commons.reflection.classifierClass
import xyz.xenondevs.commons.reflection.isSubtypeOf
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSuperclassOf

private typealias CollectionCreator<E> = (size: Int) -> MutableCollection<E>

internal class CollectionBinarySerializer<E : Any, C : Collection<E>>(
    private val elementSerializer: BinarySerializer<E>,
    private val createCollection: CollectionCreator<E?>
) : UnversionedBinarySerializer<C>() {
    
    override fun readUnversioned(reader: ByteReader): C {
        val size = reader.readVarInt()
        val collection = createCollection(size)
        
        repeat(size) {
            collection += elementSerializer.read(reader)
        }
        
        // assumption: collection creator returns intersection MutableCollection<E> & C
        @Suppress("UNCHECKED_CAST")
        return collection as C
    }
    
    override fun writeUnversioned(obj: C, writer: ByteWriter) {
        // count serialized elements instead of obj.size to prevent concurrent modifications from causing corrupted data
        var size = 0
        val temp = byteWriter {
            for (entry in obj) {
                elementSerializer.write(entry, this)
                size++
            }
        }
        
        writer.writeVarInt(size)
        writer.writeBytes(temp)
    }
    
    override fun copyNonNull(obj: C): C {
        val copy = createCollection(obj.size)
        for (entry in obj) {
            copy += elementSerializer.copy(entry)
        }
        
        // assumption: collection creator returns intersection MutableCollection<E> & C
        @Suppress("UNCHECKED_CAST")
        return copy as C
    }
    
    companion object : BinarySerializerFactory {
        
        val collectionCreators = HashMap<KClass<out MutableCollection<*>>, CollectionCreator<Any?>>()
        
        @OptIn(UncheckedApi::class)
        override fun create(type: KType): BinarySerializer<*>? {
            if (!type.isSubtypeOf<Collection<*>?>())
                return null
            val elementType = type.arguments.getOrNull(0)?.type
                ?: return null
            val creator = getCollectionCreator(type, elementType)
                ?: return null
            
            return CollectionBinarySerializer(
                Cbf.getSerializer(elementType),
                creator
            )
        }
        
        private fun getCollectionCreator(type: KType, elementType: KType): CollectionCreator<Any?>? {
            val collectionClass = type.classifierClass!!
            val elementClass = elementType.classifierClass!!
            
            var collectionCreator = collectionCreators[collectionClass]
            if (collectionCreator == null) {
                if (type.isSubtypeOf<List<*>?>()) {
                    if (collectionClass.isSuperclassOf(ArrayList::class)) {
                        collectionCreator = ::ArrayList
                    }
                } else if (type.isSubtypeOf<Set<*>?>()) {
                    if (elementType.isSubtypeOf<Enum<*>>() && collectionClass.isSuperclassOf(EnumSet::class)) {
                        collectionCreator = createEnumSetCreator(elementClass.java)
                    } else if (collectionClass.isSuperclassOf(HashSet::class)) {
                        collectionCreator = ::HashSet
                    }
                }
            }
            
            return collectionCreator
        }
        
        @Suppress("UNCHECKED_CAST")
        private fun <E : Enum<E>> createEnumSetCreator(clazz: Class<*>): CollectionCreator<Any?> {
            return { EnumSet.noneOf<E>(clazz as Class<E>) as MutableSet<Any?> }
        }
        
    }
    
}