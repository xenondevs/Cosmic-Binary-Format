package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal object TripleBinaryAdapter : BinaryAdapter<Triple<*, *, *>> {
    
    override fun write(obj: Triple<*, *, *>, writer: ByteWriter) {
        CBF.write(obj.first, writer)
        CBF.write(obj.second, writer)
        CBF.write(obj.third, writer)
    }
    
    override fun read(type: Type, reader: ByteReader): Triple<*, *, *> {
        val typeArguments = (type as ParameterizedType).actualTypeArguments
        
        return Triple<Any?, Any?, Any?>(
            CBF.read(typeArguments[0], reader),
            CBF.read(typeArguments[1], reader),
            CBF.read(typeArguments[2], reader)
        )
    }
    
}