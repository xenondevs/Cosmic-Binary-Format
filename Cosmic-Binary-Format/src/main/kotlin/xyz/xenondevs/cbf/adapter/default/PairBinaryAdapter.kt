package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal object PairBinaryAdapter : BinaryAdapter<Pair<*, *>> {
    
    override fun write(obj: Pair<*, *>, writer: ByteWriter) {
        CBF.write(obj.first, writer)
        CBF.write(obj.second, writer)
    }
    
    override fun read(type: Type, reader: ByteReader): Pair<*, *> {
        val typeArguments = (type as ParameterizedType).actualTypeArguments
        
        return Pair<Any?, Any?>(
            CBF.read(typeArguments[0], reader),
            CBF.read(typeArguments[1], reader)
        )
    }
    
}