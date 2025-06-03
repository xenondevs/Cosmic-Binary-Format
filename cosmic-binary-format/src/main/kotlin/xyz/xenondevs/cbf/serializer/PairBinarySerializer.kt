package xyz.xenondevs.cbf.serializer

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.UncheckedApi
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.commons.reflection.isSubtypeOf
import kotlin.reflect.KType

internal class PairBinarySerializer<A : Any, B : Any>(
    private val aSerializer: BinarySerializer<A>,
    private val bSerializer: BinarySerializer<B>
) : UnversionedBinarySerializer<Pair<A, B>>() {
    
    override fun writeUnversioned(obj: Pair<A, B>, writer: ByteWriter) {
        aSerializer.write(obj.first, writer)
        bSerializer.write(obj.second, writer)
    }
    
    override fun readUnversioned(reader: ByteReader): Pair<A, B> {
        return Pair(
            aSerializer.read(reader),
            bSerializer.read(reader)
        )
    }
    
    override fun copyNonNull(value: Pair<A, B>): Pair<A, B> {
        return Pair(
            aSerializer.copy(value.first),
            bSerializer.copy(value.second)
        )
    }
    
    companion object : BinarySerializerFactory {
        
        @OptIn(UncheckedApi::class)
        override fun create(type: KType): BinarySerializer<*>? {
            if (!type.isSubtypeOf<Pair<*, *>?>())
                return null
            
            val typeA = type.arguments.getOrNull(0)?.type
                ?: return null
            val typeB = type.arguments.getOrNull(1)?.type
                ?: return null
            
            return PairBinarySerializer(
                CBF.getSerializer(typeA),
                CBF.getSerializer(typeB)
            )
        }
        
    }
    
}