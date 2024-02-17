package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.adapter.ComplexBinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.commons.reflection.classifierClass
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Suppress("UNCHECKED_CAST")
object EnumBinaryAdapter : ComplexBinaryAdapter<Enum<*>> {
    
    private val ordinalEnums = HashSet<KClass<out Enum<*>>>()
    
    /**
     * Configures which [Enum] types should be serialized by their ordinal value instead of their name.
     */
    fun addOrdinalEnums(vararg classes: KClass<out Enum<*>>) {
        ordinalEnums.addAll(classes)
    }
    
    override fun write(obj: Enum<*>, type: KType, writer: ByteWriter) {
        if (obj::class in ordinalEnums) {
            writer.writeUnsignedByte(2U)
            writer.writeVarInt(obj.ordinal)
        } else {
            writer.writeUnsignedByte(1U)
            writer.writeString(obj.name)
        }
    }
    
    override fun read(type: KType, id: UByte, reader: ByteReader): Enum<*> {
        val clazz = type.classifierClass as KClass<Enum<*>>
        return when (id) {
            2.toUByte() -> {
                val ordinal = reader.readVarInt()
                clazz.java.enumConstants[ordinal]
            }
            1.toUByte() -> {
                val name = reader.readString()
                clazz.java.enumConstants.first { it.name == name }
            }
            else -> throw IllegalArgumentException("Invalid id $id")
        }
    }
    
    override fun copy(obj: Enum<*>, type: KType): Enum<*> {
        return obj
    }
    
}