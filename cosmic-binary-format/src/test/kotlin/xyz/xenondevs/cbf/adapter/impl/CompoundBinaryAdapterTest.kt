package xyz.xenondevs.cbf.adapter.impl

import org.junit.jupiter.api.Test
import xyz.xenondevs.cbf.Compound
import xyz.xenondevs.cbf.adapter.BinaryAdapterTest
import xyz.xenondevs.cbf.entry
import xyz.xenondevs.commons.provider.defaultsToLazily
import xyz.xenondevs.commons.provider.observed
import xyz.xenondevs.commons.provider.orElseNew
import kotlin.test.assertEquals

class CompoundBinaryAdapterTest : BinaryAdapterTest<Compound>(Compound.CompoundBinaryAdapter) {
    
    @Test
    fun `test direct values`() {
        val compound = Compound()
        compound["a"] = "a"
        compound["b"] = 2
        
        val reserializedCompound = reserializeValue(compound)
        
        assertEquals("a", reserializedCompound.get<String>("a"))
        assertEquals(2, reserializedCompound.get<Int>("b"))
    }
    
    @Test
    fun `test provider values`() {
        val compound = Compound()
        compound.entry<String>("a").set("a")
        compound.entry<Int>("b").set(2)
        
        val reserializedCompound = reserializeValue(compound)
        
        assertEquals("a", reserializedCompound.get<String>("a"))
        assertEquals(2, reserializedCompound.get<Int>("b"))
    }
    
    @Test
    fun `test collection provider values`() {
        val compound = Compound()
        val mapEntry = compound.entry<MutableMap<String, Int>>("map")
            .orElseNew {
                HashMap<String, Int>().apply {
                    put("a", 0)
                    put("b", 2)
                }
            }
            .observed()
        
        mapEntry.get().put("a", 1)
        
        val reserializedCompound = reserializeValue(compound)
        
        val map = reserializedCompound.get<Map<String, Int>>("map")!!
        assertEquals(1, map["a"])
        assertEquals(2, map["b"])
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
        
        val reserializedCompound = reserializeValue(compound)
        
        assertEquals("a", reserializedCompound.get<String>("a"))
        assertEquals(2, reserializedCompound.get<Int>("b"))
        assertEquals("c1", reserializedCompound.get<String>("c"))
        assertEquals(5, reserializedCompound.get<Int>("d"))
    }
    
    @Test
    fun `test compound provider entry`() {
        val compound = Compound()
        val innerEntry = compound.entry<Compound>("compound")
            .defaultsToLazily(::Compound)
        
        innerEntry.get()["a"] = "a"
        
        val reserializedCompound = reserializeValue(compound)
        assertEquals("a", reserializedCompound.get<Compound>("compound")!!.get<String>("a"))
        
        innerEntry.get()["a"] = "b"
        
        val reserializedCompound2 = reserializeValue(compound)
        assertEquals("b", reserializedCompound2.get<Compound>("compound")!!.get<String>("a"))
    }
    
    @Test
    fun `test compound provider entry that has observed list provider entry`() {
        val compound = Compound()
        
        val provider = compound.entry<Compound>("compound")
            .defaultsToLazily(::Compound)
            .entry<MutableList<String>>("list")
            .orElseNew(::ArrayList)
            .observed()
        
        provider.get() += "A"
        assertEquals(listOf("A"), reserializeValue(compound).get<Compound>("compound")!!.get<List<String>>("list"))
        
        provider.get() += "B"
        assertEquals(listOf("A", "B"), reserializeValue(compound).get<Compound>("compound")!!.get<List<String>>("list"))
    }
    
}