package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.buffer.ByteBuffer
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal object PairBinaryAdapter : BinaryAdapter<Pair<*, *>> {
    
    override fun write(obj: Pair<*, *>, buf: ByteBuffer) {
        CBF.write(obj.first, buf)
        CBF.write(obj.second, buf)
    }
    
    override fun read(type: Type, buf: ByteBuffer): Pair<*, *> {
        val typeArguments = (type as ParameterizedType).actualTypeArguments
        
        return Pair<Any?, Any?>(
            CBF.read(typeArguments[0], buf),
            CBF.read(typeArguments[1], buf)
        )
    }
    
}