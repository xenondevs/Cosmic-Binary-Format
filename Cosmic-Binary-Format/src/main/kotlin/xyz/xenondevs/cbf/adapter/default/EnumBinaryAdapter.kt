package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.buffer.ByteBuffer
import xyz.xenondevs.cbf.util.representedClass
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
internal object EnumBinaryAdapter : BinaryAdapter<Enum<*>> {
    
    override fun write(obj: Enum<*>, buf: ByteBuffer) {
        buf.writeString(obj.name)
    }
    
    override fun read(type: Type, buf: ByteBuffer): Enum<*> {
        val clazz = type.representedClass as Class<Enum<*>>
        val name = buf.readString()
        return clazz.enumConstants.first { it.name == name }
    }
    
}