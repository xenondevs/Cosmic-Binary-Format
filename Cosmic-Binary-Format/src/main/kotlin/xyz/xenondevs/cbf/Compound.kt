package xyz.xenondevs.cbf

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
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
    
    inline fun <reified T> getOrPut(key: String, defaultValue: () -> T): T {
        return get(key) ?: defaultValue().also { set(key, it) }
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
    
    fun copy(): Compound {
        return Compound(HashMap(binMap), HashMap(map))
    }
    
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("{")
        
        binMap.entries.forEach { (key, value) ->
            builder.append("\n\"$key\": (binary) ${value.contentToString()}")
        }
        
        map.entries.forEach { (key, value) ->
            builder.append("\n\"$key\": $value")
        }
        
        return builder.toString().replace("\n", "\n  ") + "\n}"
    }
    
    companion object {
        
        fun of(map: Map<String, Any?>): Compound =
            Compound(HashMap(), HashMap(map))
        
    }
    
    internal object CompoundBinaryAdapter : BinaryAdapter<Compound> {
        
        override fun write(obj: Compound, writer: ByteWriter) {
            writer.writeVarInt(obj.binMap.size + obj.map.size)
            
            obj.binMap.forEach { (key, binData) ->
                writer.writeString(key)
                writer.writeVarInt(binData.size)
                writer.writeBytes(binData)
            }
            
            obj.map.forEach { (key, data) ->
                writer.writeString(key)
                val binData = CBF.write(data)
                writer.writeVarInt(binData.size)
                writer.writeBytes(binData)
            }
        }
        
        override fun read(type: Type, reader: ByteReader): Compound {
            val mapSize = reader.readVarInt()
            val map = HashMap<String, ByteArray>(mapSize)
            
            repeat(mapSize) {
                val key = reader.readString()
                val length = reader.readVarInt()
                map[key] = reader.readBytes(length)
            }
            
            return Compound(map, HashMap())
        }
        
    }
    
}