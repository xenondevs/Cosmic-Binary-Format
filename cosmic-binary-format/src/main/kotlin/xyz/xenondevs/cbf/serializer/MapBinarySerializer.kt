package xyz.xenondevs.cbf.serializer

import xyz.xenondevs.cbf.CBF
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

private typealias MapCreator<K, V> = (size: Int) -> MutableMap<K, V>

internal class MapBinarySerializer<K, V>(
    private val keySerializer: BinarySerializer<K>,
    private val valueSerializer: BinarySerializer<V>,
    private val createMap: MapCreator<K, V>
) : UnversionedBinarySerializer<Map<K, V>>() {
    
    override fun readUnversioned(reader: ByteReader): Map<K, V> {
        val size = reader.readVarInt()
        val map = createMap(size)
        repeat(size) {
            map[keySerializer.read(reader)] = valueSerializer.read(reader)
        }
        
        return map
    }
    
    override fun writeUnversioned(obj: Map<K, V>, writer: ByteWriter) {
        // count serialized elements instead of obj.size to prevent concurrent modifications from causing corrupted data
        var size = 0
        val temp = byteWriter {
            for ((key, value) in obj) {
                keySerializer.write(key, this)
                valueSerializer.write(value, this)
                size++
            }
        }
        
        writer.writeVarInt(size)
        writer.writeBytes(temp)
    }
    
    override fun copyNonNull(obj: Map<K, V>): Map<K, V> {
        val copy = createMap(obj.size)
        for ((key, value) in obj) {
            copy[keySerializer.copy(key)] = valueSerializer.copy(value)
        }
        return copy
    }
    
    companion object : BinarySerializerFactory {
        
        val mapCreators = HashMap<KClass<out MutableMap<*, *>>, MapCreator<Any?, Any?>>()
        
        @OptIn(UncheckedApi::class)
        override fun create(type: KType): BinarySerializer<*>? {
            if (!type.isSubtypeOf<Map<*, *>?>())
                return null
            
            val keyType = type.arguments.getOrNull(0)?.type
                ?: return null
            val valueType = type.arguments.getOrNull(1)?.type
                ?: return null
            val mapCreator = getMapCreator(type, keyType)
                ?: return null
            
            return MapBinarySerializer(
                CBF.getSerializer(keyType),
                CBF.getSerializer(valueType),
                mapCreator
            )
        }
        
        private fun getMapCreator(type: KType, keyType: KType): MapCreator<Any?, Any?>? {
            val mapClass = type.classifierClass!!
            val keyClass = keyType.classifierClass!!
            
            var mapCreator = mapCreators[mapClass]
            if (mapCreator == null) {
                if (keyType.isSubtypeOf<Enum<*>>() && mapClass.isSuperclassOf(EnumMap::class)) {
                    mapCreator = createEnumMapCreator(keyClass.java)
                } else if (mapClass.isSuperclassOf(HashMap::class)) {
                    mapCreator = ::HashMap
                }
            }
            
            return mapCreator
        }
        
        @Suppress("UNCHECKED_CAST")
        private fun <E : Enum<E>> createEnumMapCreator(clazz: Class<*>): (Int) -> MutableMap<Any?, Any?> {
            return { EnumMap<E, Any>(clazz as Class<E>) as MutableMap<Any?, Any?> }
        }
        
    }
    
}