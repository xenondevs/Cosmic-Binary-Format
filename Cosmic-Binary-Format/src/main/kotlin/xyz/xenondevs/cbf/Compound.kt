package xyz.xenondevs.cbf

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.util.HexFormat
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
class Compound internal constructor(
    private val binMap: HashMap<String, ByteArray>,
    private val map: HashMap<String, Any?> = HashMap(),
    private val types: HashMap<String, KType> = HashMap()
) {
    
    val keys: Set<String>
        get() = binMap.keys + map.keys
    
    constructor() : this(HashMap(), HashMap(), HashMap())
    
    inline operator fun <reified T> set(key: String, value: T) {
        set(typeOf<T>(), key, value)
    }
    
    fun set(type: KType, key: String, value: Any?) {
        binMap -= key
        map[key] = value
        types[key] = type
    }
    
    fun <T : Any> get(type: KType, key: String): T? {
        map[key]?.let { return it as T }
        
        val bytes = binMap[key] ?: return null
        val value = CBF.read<T>(type, bytes)
        
        types[key] = type
        map[key] = value
        binMap -= key
        
        return value
    }
    
    inline operator fun <reified T : Any> get(key: String): T? {
        return get(typeOf<T>(), key)
    }
    
    inline fun <reified T : Any> getOrPut(key: String, defaultValue: () -> T): T {
        return get(key) ?: defaultValue().also { set(key, it) }
    }
    
    fun putAll(other: Compound) {
        binMap.putAll(other.binMap)
        map.putAll(other.map)
        types.putAll(other.types)
    }
    
    operator fun contains(key: String): Boolean {
        return map.containsKey(key) || binMap.containsKey(key)
    }
    
    fun remove(key: String) {
        binMap.remove(key)
        map.remove(key)
        types.remove(key)
    }
    
    operator fun minusAssign(key: String) {
        remove(key)
    }
    
    fun isEmpty(): Boolean = map.isNotEmpty()
    
    fun isNotEmpty(): Boolean = map.isNotEmpty()
    
    fun copy(): Compound {
        val copy = Compound(HashMap(binMap))
        
        for((key, value) in map) {
            val valueType = types[key]!!
            copy.map[key] = value?.let { CBF.copy(it, valueType.withNullability(false)) }
            copy.types[key] = valueType
        }
        
        return copy
    }
    
    fun shallowCopy(): Compound {
        return Compound(HashMap(binMap), HashMap(map), HashMap(types))
    }
    
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("{")
        
        binMap.entries.forEach { (key, value) ->
            val hexStr = HexFormat.of().formatHex(value)
            builder.append("\n\"$key\": (binary) $hexStr")
        }
        
        map.entries.forEach { (key, value) ->
            builder.append("\n\"$key\": $value")
        }
        
        return builder.toString().replace("\n", "\n  ") + "\n}"
    }
    
    companion object {
        
        fun of(map: Map<String, Any?>): Compound =
            Compound(HashMap(), HashMap(map), HashMap())
        
    }
    
    internal object CompoundBinaryAdapter : BinaryAdapter<Compound> {
        
        override fun write(obj: Compound, type: KType, writer: ByteWriter) {
            writer.writeVarInt(obj.binMap.size + obj.map.size)
            
            obj.binMap.forEach { (key, binData) ->
                writer.writeString(key)
                writer.writeVarInt(binData.size)
                writer.writeBytes(binData)
            }
            
            obj.map.forEach { (key, value) ->
                writer.writeString(key)
                val binData = CBF.write(value, obj.types[key])
                writer.writeVarInt(binData.size)
                writer.writeBytes(binData)
            }
        }
        
        override fun read(type: KType, reader: ByteReader): Compound {
            val mapSize = reader.readVarInt()
            val binMap = HashMap<String, ByteArray>(mapSize)
            
            repeat(mapSize) {
                val key = reader.readString()
                val length = reader.readVarInt()
                binMap[key] = reader.readBytes(length)
            }
            
            return Compound(binMap)
        }
        
        override fun copy(obj: Compound, type: KType): Compound {
            return obj.copy()
        }
        
    }
    
}