package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.instancecreator.impl.EnumMapInstanceCreator
import xyz.xenondevs.cbf.internal.nonNullTypeArguments
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

internal object MapBinaryAdapter : BinaryAdapter<Map<*, *>> {
    
    override fun write(obj: Map<*, *>, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        val (keyType, valueType) = type.nonNullTypeArguments
        obj.forEach { (key, value) ->
            CBF.write(key, keyType, writer)
            CBF.write(value, valueType, writer)
        }
    }
    
    override fun read(type: KType, reader: ByteReader): Map<*, *> {
        val size = reader.readVarInt()
        val typeArguments = type.nonNullTypeArguments
        
        val map = createMapInstance(type)
        repeat(size) { map[CBF.read(typeArguments[0], reader)] = CBF.read(typeArguments[1], reader) }
        
        return map
    }
    
    override fun copy(obj: Map<*, *>, type: KType): Map<*, *> {
        val (keyType, valueType) = type.nonNullTypeArguments
        val keyTypeBinaryAdapter = CBF.getBinaryAdapter<Any>(keyType)
        val valueTypeBinaryAdapter = CBF.getBinaryAdapter<Any>(valueType)
        val map = createMapInstance(type)
        
        obj.forEach { (key, value) ->
            val keyCopy = key?.let { keyTypeBinaryAdapter.copy(it, keyType) }
            val valueCopy = value?.let { valueTypeBinaryAdapter.copy(it, valueType) }
            map[keyCopy] = valueCopy
        }
        
        return map
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun createMapInstance(type: KType): MutableMap<Any?, Any?> {
        val map = CBF.createInstance<Map<*, *>>(type)
        if (map != null)
            return map as MutableMap<Any?, Any?>
        
        if (type.nonNullTypeArguments[0].isSubtypeOf(typeOf<Enum<*>>()))
            return EnumMapInstanceCreator.createInstance(type) as MutableMap<Any?, Any?>
        
        return HashMap()
    }
    
}