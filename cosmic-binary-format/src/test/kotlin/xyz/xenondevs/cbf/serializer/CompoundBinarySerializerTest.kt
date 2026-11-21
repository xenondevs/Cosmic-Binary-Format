package xyz.xenondevs.cbf.serializer

import org.junit.jupiter.api.Test
import xyz.xenondevs.cbf.Cbf
import xyz.xenondevs.cbf.Compound
import kotlin.test.assertEquals

class CompoundBinarySerializerTest : BinarySerializerTest<Compound>(Compound.CompoundBinarySerializer) {
    
    @Test
    fun `test direct values`() {
        val compound = Compound()
        compound["a"] = "a"
        compound["b"] = 2
        
        val reserializedCompound = reserializeValue(compound)!!
        
        assertEquals("a", reserializedCompound.get<String>("a"))
        assertEquals(2, reserializedCompound.get<Int>("b"))
    }
    
    @Test
    fun `test provider values`() {
        val compound = Compound()
        compound.entry<String>("a").set("a")
        compound.entry<Int>("b").set(2)
        
        val reserializedCompound = reserializeValue(compound)!!
        
        assertEquals("a", reserializedCompound.get<String>("a"))
        assertEquals(2, reserializedCompound.get<Int>("b"))
    }
    
    @Test
    fun `test mixed values`() {
        val compound = Compound()
        compound["a"] = "a"
        compound.entry<Int>("b").set(2)
        compound["c"] = "c"
        compound.entry<String>("c").set("c1")
        compound.entry<Int>("d").set(4)
        compound["d"] = 5
        
        val reserializedCompound = reserializeValue(compound)!!
        
        assertEquals("a", reserializedCompound.get<String>("a"))
        assertEquals(2, reserializedCompound.get<Int>("b"))
        assertEquals("c1", reserializedCompound.get<String>("c"))
        assertEquals(5, reserializedCompound.get<Int>("d"))
    }
    
    @Test
    fun `test compound provider entry`() {
        val compound = Compound()
        val innerEntry = compound.entry<Compound>("compound", ::Compound)
        
        innerEntry.get()["a"] = "a"
        
        val reserializedCompound = reserializeValue(compound)!!
        assertEquals("a", reserializedCompound.get<Compound>("compound")!!.get<String>("a"))
        
        innerEntry.get()["a"] = "b"
        
        val reserializedCompound2 = reserializeValue(compound)!!
        assertEquals("b", reserializedCompound2.get<Compound>("compound")!!.get<String>("a"))
    }
    
    @Test
    fun `test conversion from format v1`() {
        val compound = deserializeValue(
            byteWriter {
                writeUnsignedByte(1U) // version
                writeVarInt(2) // element count
                
                writeString("element1")
                val bytes1 = Cbf.write<Int>(null)
                writeVarInt(bytes1.size)
                writeBytes(bytes1)
                
                writeString("element2")
                val bytes2 = Cbf.write<Int>(1234)
                writeVarInt(bytes2.size)
                writeBytes(bytes2)
            }
        )!!
        
        assertEquals(null, compound.get<Int>("element1"))
        assertEquals(1234, compound.get<Int>("element2"))
    }
    
}