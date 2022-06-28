package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.buffer.ByteBuffer
import java.lang.reflect.Type
import java.util.*

internal object UUIDBinaryAdapter : BinaryAdapter<UUID> {
    
    override fun write(obj: UUID, buf: ByteBuffer) {
        buf.writeUUID(obj)
    }
    
    override fun read(type: Type, buf: ByteBuffer): UUID {
        return buf.readUUID()
    }
    
}