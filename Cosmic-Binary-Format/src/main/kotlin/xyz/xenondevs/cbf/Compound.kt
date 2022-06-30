package xyz.xenondevs.cbf

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.buffer.ByteBuffer
import xyz.xenondevs.cbf.util.type
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
class Compound internal constructor(
    private val binMap: HashMap<String, ByteArray>,
    private val map: HashMap<String, Any?>
) {
    
    val keys: Set<String>
        get() = binMap.keys + map.keys
    
    constructor() : this(HashMap(), HashMap())
    
    operator fun set(key: String, value: Any?) {
        binMap -= key
        map[key] = value
    }
    
    fun <T> get(type: Type, key: String): T? {
        map[key]?.let { return it as? T }
        
        val bytes = binMap[key] ?: return null
        val value = CBF.read<T>(type, bytes)
        
        map[key] = value
        binMap -= key
        
        return value
    }
    
    inline operator fun <reified T> get(key: String): T? {
        return get(type<T>(), key)
    }
    
    operator fun contains(key: String): Boolean {
        return map.containsKey(key) || binMap.containsKey(key)
    }
    
    fun remove(key: String) {
        binMap.remove(key)
        map.remove(key)
    }
    
    operator fun minusAssign(key: String) {
        remove(key)
    }
    
    fun isEmpty(): Boolean = map.isNotEmpty()
    
    fun isNotEmpty(): Boolean = map.isNotEmpty()
    
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("{")
        (binMap.entries + map.entries).forEach { (key, value) ->
            builder.append("\n\"$key\": $value")
        }
        
        return builder.toString().replace("\n", "\n  ") + "\n}"
    }
    
    companion object {
        
        fun of(map: HashMap<String, Any?>): Compound =
            Compound(HashMap(), map.mapValuesTo(HashMap()) { CBF.write(it.value) })
        
    }
    
    internal object CompoundBinaryAdapter : BinaryAdapter<Compound> {
        
        override fun write(obj: Compound, buf: ByteBuffer) {
            buf.writeVarInt(obj.binMap.size + obj.map.size)
            
            obj.binMap.forEach { (key, binData) ->
                buf.writeString(key)
                buf.writeVarInt(binData.size)
                buf.writeBytes(binData)
            }
            
            obj.map.forEach { (key, data) ->
                buf.writeString(key)
                val binData = CBF.write(data)
                buf.writeVarInt(binData.size)
                buf.writeBytes(binData)
            }
        }
        
        override fun read(type: Type, buf: ByteBuffer): Compound {
            val mapSize = buf.readInt()
            val map = HashMap<String, ByteArray>(mapSize)
            
            repeat(mapSize) {
                val key = buf.readString()
                val arraySize = buf.readInt()
                val array = ByteArray(arraySize)
                buf.readBytes(array)
                map[key] = array
            }
            
            return Compound(map, HashMap())
        }
        
    }
    
}