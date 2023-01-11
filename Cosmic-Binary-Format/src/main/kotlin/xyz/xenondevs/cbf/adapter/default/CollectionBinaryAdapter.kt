package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal object CollectionBinaryAdapter : BinaryAdapter<Collection<*>> {
    
    override fun write(obj: Collection<*>, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach { CBF.write(it, writer) }
    }
    
    override fun read(type: Type, reader: ByteReader): Collection<*> {
        val size = reader.readVarInt()
        val valueType = (type as ParameterizedType).actualTypeArguments[0]
        val collection = CBF.createInstance<MutableCollection<Any?>>(type) ?: ArrayList()
        repeat(size) { collection.add(CBF.read(valueType, reader)) }
        return collection
    }
    
}