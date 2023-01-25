package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.internal.nonNullTypeArguments
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
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
    
}