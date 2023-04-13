package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.util.*
import kotlin.reflect.KType

internal object UUIDBinaryAdapter : BinaryAdapter<UUID> {
    
    override fun write(obj: UUID, type: KType, writer: ByteWriter) {
        writer.writeUUID(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): UUID {
        return reader.readUUID()
    }
    
    override fun copy(obj: UUID, type: KType): UUID {
        return obj
    }
    
}