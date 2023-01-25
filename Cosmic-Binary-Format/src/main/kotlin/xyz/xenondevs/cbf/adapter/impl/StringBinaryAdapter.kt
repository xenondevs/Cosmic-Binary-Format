package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import kotlin.reflect.KType

internal object StringBinaryAdapter : BinaryAdapter<String> {
    
    override fun write(obj: String, type: KType, writer: ByteWriter) {
        writer.writeString(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): String {
        return reader.readString()
    }
    
}

internal object StringArrayBinaryAdapter : BinaryAdapter<Array<String>> {
    
    override fun write(obj: Array<String>, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeString)
    }
    
    override fun read(type: KType, reader: ByteReader): Array<String> {
        return Array(reader.readVarInt()) { reader.readString() }
    }
    
}