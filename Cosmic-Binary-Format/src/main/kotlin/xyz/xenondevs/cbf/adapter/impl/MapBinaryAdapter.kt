package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.internal.nonNullTypeArguments
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import kotlin.reflect.KType

internal object MapBinaryAdapter : BinaryAdapter<Map<*, *>> {
    
    override fun write(obj: Map<*, *>, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        val arguments = type.arguments
        val keyType = arguments[0].type
        val valueType = arguments[1].type
        obj.forEach { (key, value) ->
            CBF.write(key, keyType, writer)
            CBF.write(value, valueType, writer)
        }
    }
    
    override fun read(type: KType, reader: ByteReader): Map<*, *> {
        val size = reader.readVarInt()
        val typeArguments = type.nonNullTypeArguments
        
        val map = CBF.createInstance<MutableMap<Any?, Any?>>(type) ?: HashMap()
        repeat(size) { map[CBF.read(typeArguments[0], reader)] = CBF.read(typeArguments[1], reader) }
        
        return map
    }
    
}