package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.buffer.ByteBuffer
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal object TripleBinaryAdapter : BinaryAdapter<Triple<*, *, *>> {
    
    override fun write(obj: Triple<*, *, *>, buf: ByteBuffer) {
        CBF.write(obj.first, buf)
        CBF.write(obj.second, buf)
        CBF.write(obj.third, buf)
    }
    
    override fun read(type: Type, buf: ByteBuffer): Triple<*, *, *> {
        val typeArguments = (type as ParameterizedType).actualTypeArguments
        
        return Triple<Any?, Any?, Any?>(
            CBF.read(typeArguments[0], buf),
            CBF.read(typeArguments[1], buf),
            CBF.read(typeArguments[2], buf)
        )
    }
    
}