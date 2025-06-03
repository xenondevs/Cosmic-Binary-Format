package xyz.xenondevs.cbf.serializer

import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import kotlin.uuid.Uuid

internal object KotlinUuidBinarySerializer : UnversionedBinarySerializer<Uuid>() {
    
    override fun writeUnversioned(obj: Uuid, writer: ByteWriter) {
        obj.toLongs { mostSignificantBits, leastSignificantBits -> 
            writer.writeLong(mostSignificantBits)
            writer.writeLong(leastSignificantBits)
        }
    }
    
    override fun readUnversioned(reader: ByteReader): Uuid {
        return Uuid.fromLongs(reader.readLong(), reader.readLong())
    }
    
    override fun copyNonNull(obj: Uuid): Uuid = obj
    
}