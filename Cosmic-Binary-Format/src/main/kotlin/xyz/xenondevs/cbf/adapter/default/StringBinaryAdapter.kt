package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.lang.reflect.Type

internal object StringBinaryAdapter : BinaryAdapter<String> {
    
    override fun write(obj: String, writer: ByteWriter) {
        writer.writeString(obj)
    }
    
    override fun read(type: Type, reader: ByteReader): String {
        return reader.readString()
    }
    
}

internal object StringArrayBinaryAdapter : BinaryAdapter<Array<String>> {
    
    override fun write(obj: Array<String>, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeString)
    }
    
    override fun read(type: Type, reader: ByteReader): Array<String> {
        return Array(reader.readVarInt()) { reader.readString() }
    }
    
}