package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.commons.reflection.nonNullTypeArguments
import kotlin.reflect.KType

internal abstract class CollectionBinaryAdapter<T : Collection<*>> : BinaryAdapter<T> {
    
    override fun write(obj: T, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        val entryType = type.arguments[0].type
        obj.forEach { CBF.write(it, entryType, writer) }
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun read(type: KType, reader: ByteReader): T {
        val size = reader.readVarInt()
        val typeArguments = type.nonNullTypeArguments
        val collection = createCollection(type)
        repeat(size) { collection.add(CBF.read(typeArguments[0], reader)) }
        return collection as T
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun copy(obj: T, type: KType): T {
        val collection = createCollection(type)
        val collectionType = type.nonNullTypeArguments[0]
        val typeBinaryAdapter = CBF.getBinaryAdapter<Any>(collectionType)
        obj.forEach { collection.add(it?.let { typeBinaryAdapter.copy(it, collectionType) }) }
        return collection as T
    }
    
    abstract fun createCollection(type: KType): MutableCollection<Any?> 
    
}