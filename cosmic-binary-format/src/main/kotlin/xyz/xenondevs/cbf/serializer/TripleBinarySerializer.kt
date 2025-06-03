package xyz.xenondevs.cbf.serializer

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.UncheckedApi
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.commons.reflection.isSubtypeOf
import kotlin.reflect.KType

internal class TripleBinarySerializer<A : Any, B : Any, C : Any>(
    private val aSerializer: BinarySerializer<A>,
    private val bSerializer: BinarySerializer<B>,
    private val cSerializer: BinarySerializer<C>
) : UnversionedBinarySerializer<Triple<A, B, C>>() {
    
    override fun writeUnversioned(obj: Triple<A, B, C>, writer: ByteWriter) {
        aSerializer.write(obj.first, writer)
        bSerializer.write(obj.second, writer)
        cSerializer.write(obj.third, writer)
    }
    
    override fun readUnversioned(reader: ByteReader): Triple<A, B, C> {
        return Triple(
            aSerializer.read(reader),
            bSerializer.read(reader),
            cSerializer.read(reader)
        )
    }
    
    override fun copyNonNull(value: Triple<A, B, C>): Triple<A, B, C> {
        return Triple(
            aSerializer.copy(value.first),
            bSerializer.copy(value.second),
            cSerializer.copy(value.third)
        )
    }
    
    companion object : BinarySerializerFactory {
        
        @OptIn(UncheckedApi::class)
        override fun create(type: KType): BinarySerializer<*>? {
            if (!type.isSubtypeOf<Triple<*, *, *>?>())
                return null
            
            val typeA = type.arguments.getOrNull(0)?.type
                ?: return null
            val typeB = type.arguments.getOrNull(1)?.type
                ?: return null
            val typeC = type.arguments.getOrNull(2)?.type
                ?: return null
            
            return TripleBinarySerializer(
                CBF.getSerializer(typeA),
                CBF.getSerializer(typeB),
                CBF.getSerializer(typeC)
            )
        }
        
    }
    
}