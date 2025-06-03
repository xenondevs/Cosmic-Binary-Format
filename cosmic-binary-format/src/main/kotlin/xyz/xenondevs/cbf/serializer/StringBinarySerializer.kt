package xyz.xenondevs.cbf.serializer

import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter

internal object StringBinarySerializer : UnversionedBinarySerializer<String>() {
    
    override fun writeUnversioned(obj: String, writer: ByteWriter) {
        writer.writeString(obj)
    }
    
    override fun readUnversioned(reader: ByteReader): String {
        return reader.readString()
    }
    
    override fun copyNonNull(obj: String): String {
        return obj
    }
    
}

internal object StringArrayBinarySerializer : UnversionedBinarySerializer<Array<String>>() {
    
    override fun writeUnversioned(obj: Array<String>, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeString)
    }
    
    override fun readUnversioned(reader: ByteReader): Array<String> {
        return Array(reader.readVarInt()) { reader.readString() }
    }
    
    override fun copyNonNull(obj: Array<String>): Array<String> {
        return obj.clone()
    }
    
}