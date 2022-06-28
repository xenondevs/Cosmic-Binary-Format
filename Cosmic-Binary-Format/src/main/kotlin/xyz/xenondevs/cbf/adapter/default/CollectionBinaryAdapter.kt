package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.buffer.ByteBuffer
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal object CollectionBinaryAdapter : BinaryAdapter<Collection<*>> {
    
    override fun write(obj: Collection<*>, buf: ByteBuffer) {
        buf.writeInt(obj.size)
        obj.forEach { CBF.write(it, buf) }
    }
    
    override fun read(type: Type, buf: ByteBuffer): Collection<*> {
        val size = buf.readInt()
        val valueType = (type as ParameterizedType).actualTypeArguments[0]
        val collection = CBF.createInstance<MutableCollection<Any?>>(type) ?: ArrayList()
        repeat(size) { collection.add(CBF.read(valueType, buf)) }
        return collection
    }
    
}