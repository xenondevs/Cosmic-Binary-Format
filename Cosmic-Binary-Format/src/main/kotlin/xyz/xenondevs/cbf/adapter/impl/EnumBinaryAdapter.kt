package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.commons.reflection.classifierClass
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Suppress("UNCHECKED_CAST")
internal object EnumBinaryAdapter : BinaryAdapter<Enum<*>> {
    
    override fun write(obj: Enum<*>, type: KType, writer: ByteWriter) {
        writer.writeString(obj.name)
    }
    
    override fun read(type: KType, reader: ByteReader): Enum<*> {
        val clazz = type.classifierClass as KClass<Enum<*>>
        val name = reader.readString()
        return clazz.java.enumConstants.first { it.name == name }
    }
    
    override fun copy(obj: Enum<*>, type: KType): Enum<*> {
        return obj
    }
    
}