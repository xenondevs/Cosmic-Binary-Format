package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.buffer.ByteBuffer
import java.lang.reflect.Type

internal object StringBinaryAdapter : BinaryAdapter<String> {
    
    override fun write(obj: String, buf: ByteBuffer) {
        buf.writeString(obj)
    }
    
    override fun read(type: Type, buf: ByteBuffer): String {
        return buf.readString()
    }
    
}

internal object StringArrayBinaryAdapter : BinaryAdapter<Array<String>> {
    
    override fun write(obj: Array<String>, buf: ByteBuffer) {
        buf.writeVarInt(obj.size)
        obj.forEach(buf::writeString)
    }
    
    override fun read(type: Type, buf: ByteBuffer): Array<String> {
        return Array(buf.readVarInt()) { buf.readString() }
    }
    
}