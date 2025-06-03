package xyz.xenondevs.cbf.serializer

import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.util.*

internal object UUIDBinarySerializer : UnversionedBinarySerializer<UUID>() {
    
    override fun writeUnversioned(obj: UUID, writer: ByteWriter) {
        writer.writeUUID(obj)
    }
    
    override fun readUnversioned(reader: ByteReader): UUID {
        return reader.readUUID()
    }
    
    override fun copyNonNull(obj: UUID): UUID {
        return obj
    }
    
}