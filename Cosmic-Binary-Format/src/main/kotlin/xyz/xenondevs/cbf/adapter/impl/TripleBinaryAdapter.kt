package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.internal.nonNullTypeArguments
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import kotlin.reflect.KType

internal object TripleBinaryAdapter : BinaryAdapter<Triple<*, *, *>> {
    
    override fun write(obj: Triple<*, *, *>, type: KType, writer: ByteWriter) {
        val arguments = type.arguments
        CBF.write(obj.first, arguments[0].type, writer)
        CBF.write(obj.second, arguments[1].type, writer)
        CBF.write(obj.third, arguments[2].type, writer)
    }
    
    override fun read(type: KType, reader: ByteReader): Triple<*, *, *> {
        val typeArguments = type.nonNullTypeArguments
        
        return Triple<Any?, Any?, Any?>(
            CBF.read(typeArguments[0], reader),
            CBF.read(typeArguments[1], reader),
            CBF.read(typeArguments[2], reader)
        )
    }
    
    override fun copy(obj: Triple<*, *, *>, type: KType): Triple<*, *, *> {
        val (firstType, secondType, thirdType) = type.nonNullTypeArguments
        val firstTypeBinaryAdapter = CBF.getBinaryAdapter<Any>(firstType)
        val secondTypeBinaryAdapter = CBF.getBinaryAdapter<Any>(secondType)
        val thirdTypeBinaryAdapter = CBF.getBinaryAdapter<Any>(thirdType)
        
        return Triple(
            obj.first?.let { firstTypeBinaryAdapter.copy(it, firstType) },
            obj.second?.let { secondTypeBinaryAdapter.copy(it, secondType) },
            obj.third?.let { thirdTypeBinaryAdapter.copy(it, thirdType) }
        )
    }
    
}