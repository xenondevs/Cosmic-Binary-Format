package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal object MapBinaryAdapter : BinaryAdapter<Map<*, *>> {
    
    override fun write(obj: Map<*, *>, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach { (key, value) -> 
            CBF.write(key, writer)
            CBF.write(value, writer)
        }
    }
    
    override fun read(type: Type, reader: ByteReader): Map<*, *> {
        val size = reader.readVarInt()
        val typeArguments = (type as ParameterizedType).actualTypeArguments
        val keyType = typeArguments[0]
        val valueType = typeArguments[1]
        
        val map = CBF.createInstance<MutableMap<Any?, Any?>>(type) ?: HashMap()
        repeat(size) { map[CBF.read(keyType, reader)] = CBF.read(valueType, reader) }
        
        return map
    }
    
}