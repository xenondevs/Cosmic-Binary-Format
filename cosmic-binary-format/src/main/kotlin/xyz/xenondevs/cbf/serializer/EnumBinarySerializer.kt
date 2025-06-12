package xyz.xenondevs.cbf.serializer

import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.commons.reflection.isSubtypeOf
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Suppress("UNCHECKED_CAST")
private fun <E : Enum<E>> EnumBinarySerializer(
    clazz: KClass<*>,
    serializeByOrdinal: Boolean,
): EnumBinarySerializer<*> {
    clazz as KClass<E>
    return EnumBinarySerializer(serializeByOrdinal, clazz.java.enumConstants)
}

internal class EnumBinarySerializer<E : Enum<E>>(
    private val serializeByOrdinal: Boolean,
    private val enumConstants: Array<E>
) : BinarySerializer<E> {
    
    override fun write(obj: E?, writer: ByteWriter) {
        if (obj == null) {
            writer.writeUnsignedByte(0U)
        } else if (serializeByOrdinal) {
            writer.writeUnsignedByte(2U)
            writer.writeVarInt(obj.ordinal)
        } else {
            writer.writeUnsignedByte(1U)
            writer.writeString(obj.name)
        }
    }
    
    override fun read(reader: ByteReader): E? {
        return when (val id = reader.readUnsignedByte()) {
            2.toUByte() -> {
                val ordinal = reader.readVarInt()
                enumConstants[ordinal]
            }
            
            1.toUByte() -> {
                val name = reader.readString()
                enumConstants.first { it.name == name }
            }
            
            0.toUByte() -> null
            else -> throw IllegalArgumentException("Invalid id $id")
        }
    }
    
    override fun copy(obj: E?): E? = obj
    
    companion object : BinarySerializerFactory {
        
        val ordinalEnums = HashSet<KClass<out Enum<*>>>()
        
        override fun create(type: KType): BinarySerializer<*>? {
            if (!type.isSubtypeOf<Enum<*>?>())
                return null
            
            val clazz = type.classifier as KClass<*>
            return EnumBinarySerializer(
                clazz,
                clazz in ordinalEnums
            )
        }
        
    }
    
}