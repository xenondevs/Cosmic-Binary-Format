package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.internal.nonNullTypeArguments
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import kotlin.reflect.KType

internal object CollectionBinaryAdapter : BinaryAdapter<Collection<*>> {
    
    override fun write(obj: Collection<*>, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        val entryType = type.arguments[0].type
        obj.forEach { CBF.write(it, entryType, writer) }
    }
    
    override fun read(type: KType, reader: ByteReader): Collection<*> {
        val size = reader.readVarInt()
        val typeArguments = type.nonNullTypeArguments
        val collection = CBF.createInstance<MutableCollection<Any?>>(type) ?: ArrayList()
        repeat(size) { collection.add(CBF.read(typeArguments[0], reader)) }
        return collection
    }
    
}