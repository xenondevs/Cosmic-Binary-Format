package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.cbf.util.representedClass
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
internal object EnumBinaryAdapter : BinaryAdapter<Enum<*>> {
    
    override fun write(obj: Enum<*>, writer: ByteWriter) {
        writer.writeString(obj.name)
    }
    
    override fun read(type: Type, reader: ByteReader): Enum<*> {
        val clazz = type.representedClass as Class<Enum<*>>
        val name = reader.readString()
        return clazz.enumConstants.first { it.name == name }
    }
    
}