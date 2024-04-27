package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.commons.reflection.nonNullTypeArguments
import kotlin.reflect.KType

internal object PairBinaryAdapter : BinaryAdapter<Pair<*, *>> {
    
    override fun write(obj: Pair<*, *>, type: KType, writer: ByteWriter) {
        val arguments = type.arguments
        CBF.write(obj.first, arguments[0].type, writer)
        CBF.write(obj.second, arguments[1].type, writer)
    }
    
    override fun read(type: KType, reader: ByteReader): Pair<*, *> {
        val typeArguments = type.nonNullTypeArguments
        
        return Pair<Any?, Any?>(
            CBF.read(typeArguments[0], reader),
            CBF.read(typeArguments[1], reader)
        )
    }
    
    override fun copy(obj: Pair<*, *>, type: KType): Pair<*, *> {
        val (firstType, secondType) = type.nonNullTypeArguments
        val firstTypeBinaryAdapter = CBF.getBinaryAdapter<Any>(firstType)
        val secondTypeBinaryAdapter = CBF.getBinaryAdapter<Any>(secondType)
        
        return Pair(
            obj.first?.let { firstTypeBinaryAdapter.copy(it, firstType) },
            obj.second?.let { secondTypeBinaryAdapter.copy(it, secondType) }
        )
    }
    
}