package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.lang.reflect.Type
import java.util.*

internal object UUIDBinaryAdapter : BinaryAdapter<UUID> {
    
    override fun write(obj: UUID, writer: ByteWriter) {
        writer.writeUUID(obj)
    }
    
    override fun read(type: Type, reader: ByteReader): UUID {
        return reader.readUUID()
    }
    
}