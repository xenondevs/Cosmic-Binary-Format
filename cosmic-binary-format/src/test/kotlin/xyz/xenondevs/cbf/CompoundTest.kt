package xyz.xenondevs.cbf

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import xyz.xenondevs.commons.provider.mapNonNull
import kotlin.test.assertEquals

class CompoundTest {
    
    @Test
    fun testSetGet() {
        val compound = Compound()
        
        compound["a"] = 1
        assertEquals(1, compound["a"])
        assertThrows<Exception> { compound.get<String>("a") } // should fail because of incorrect type
        
        assertDoesNotThrow { compound["a"] = "1" } // changing type should be allowed if there is no entry provider
        compound.entry<String>("a")
        assertThrows<Exception> { compound["a"] = 1 } // type change is no longer allowed
    }
    
    @Test
    fun testExistingEntry() {
        val compound = Compound()
        compound["a"] = "b"
        
        val provider = compound.entry<String>("a")
            .mapNonNull(String::uppercase, String::lowercase)
        
        assertEquals("B", provider.get())
        
        provider.set("C")
        assertEquals("C", provider.get())
        assertEquals("c", compound["a"])
        
        compound["a"] = "d"
        assertEquals("D", provider.get())
        
        provider.set(null)
        assertEquals(false, "a" in compound)
    }
    
    @Test
    fun testNonExistingEntry() {
        val compound = Compound()
        val provider = compound.entry<String>("a")
        assertEquals(null, provider.get())
        
        provider.set("b")
        assertEquals("b", compound["a"])
    }
    
    @Test
    fun testCompoundProviderEntry() {
        val outerCompound = Compound()
        val innerCompound = Compound()
        innerCompound["value1"] = "1"
        
        val compoundEntry = outerCompound.entry<Compound>("compound")
        val valueEntry1 = compoundEntry.entry<String>("value1")
        val valueEntry2 = compoundEntry.entry<String>("value2")
        
        assertEquals(null, valueEntry1.get())
        assertEquals(null, valueEntry2.get())
        
        outerCompound["compound"] = innerCompound
        
        assertEquals("1", valueEntry1.get())
        assertEquals(null, valueEntry2.get())
        
        innerCompound["value2"] = "1"
        
        assertEquals("1", valueEntry1.get())
        assertEquals("1", valueEntry2.get())
        
        outerCompound.remove("compound")
        
        assertEquals(null, valueEntry1.get())
        assertEquals(null, valueEntry2.get())
    }
    
    @Test
    fun testGetOrPut() {
        val compound = Compound()
        assertEquals(compound.get<String>("a"), null)
        
        compound["a"] = "a"
        assertEquals("a", compound.getOrPut("a") { "aa" })
        assertEquals("b", compound.getOrPut("b") { "b" })
        assertEquals("b", compound["b"])
    }
    
    @Test
    fun testPutAllNoIntersection() {
        val a = listOf("a")
        val b = listOf("b")
        
        val compoundA = Compound()
        compoundA["a"] = a
        
        val compoundB = Compound()
        compoundB["b"] = b
        
        compoundA.putAll(compoundB)
        
        assert(compoundA.get<List<String>>("a") === a)
        assert(compoundA.get<List<String>>("b") === b)
    }
    
    @Test
    fun testPutAllValidIntersection() {
        val compoundA = Compound()
        compoundA["a"] = 1
        compoundA["b"] = 2
        
        val compoundB = Compound()
        compoundB["b"] = "2"
        compoundB["c"] = 3
        
        compoundA.putAll(compoundB)
        
        assertEquals(1, compoundA["a"])
        assertEquals("2", compoundA["b"])
        assertEquals(3, compoundA["c"])
    }
    
    @Test
    fun testPutAllInvalidIntersection() {
        val compoundA = Compound()
        compoundA["a"] = 1
        compoundA["b"] = 2
        compoundA.entry<Int>("b") // ProviderCompoundEntry cannot change type
        
        val compoundB = Compound()
        compoundB["b"] = "2"
        compoundB["c"] = 3
        
        assertThrows<IllegalArgumentException> { compoundA.putAll(compoundB) }
    }
    
    @Test
    fun testContains() {
        val compound = Compound()
        compound["a"] = 1
        assertEquals(true, compound.contains("a"))
        compound.set<Any?>("a", null)
        assertEquals(false, compound.contains("a"))
        
        val entry = compound.entry<String>("b")
        assertEquals(false, compound.contains("b"))
        entry.set("b")
        assertEquals(true, compound.contains("b"))
        entry.set(null)
        assertEquals(false, compound.contains("b"))
    }
    
    @Test
    fun testRemove() {
        val compound = Compound()
        compound["a"] = 1
        assertEquals(1, compound["a"])
        compound.remove("a")
        assertEquals(null, compound.get<String>("a"))
    }
    
    @Test
    fun testRename() {
        val compound = Compound()
        
        compound["a"] = 1
        compound.rename("a", "b")
        assertEquals(1, compound["b"])
        assertEquals(null, compound.get<String>("a"))
        
        val entry = compound.entry<Int>("b")
        entry.set(1)
        compound.rename("b", "c")
        assertEquals(1, compound["c"])
        entry.set(2)
        assertEquals(2, compound["c"])
    }
    
    @Test
    fun testEmpty() {
        val compound = Compound()
        assertEquals(true, compound.isEmpty())
        assertEquals(false, compound.isNotEmpty())
        
        compound["a"] = 1
        assertEquals(false, compound.isEmpty())
        assertEquals(true, compound.isNotEmpty())
        
        compound.remove("a")
        assertEquals(true, compound.isEmpty())
        assertEquals(false, compound.isNotEmpty())
        
        val entry = compound.entry<Int>("b")
        assertEquals(true, compound.isEmpty())
        assertEquals(false, compound.isNotEmpty())
        
        entry.set(1)
        assertEquals(false, compound.isEmpty())
        assertEquals(true, compound.isNotEmpty())
        
        entry.set(null)
        assertEquals(true, compound.isEmpty())
        assertEquals(false, compound.isNotEmpty())
    }
    
    @Test
    fun testCopy() {
        val compound = Compound()
        val list = listOf("a", "b", "c")
        compound["list"] = list
        
        val copy = compound.copy()
        val listCopy = copy.get<List<String>>("list")
        
        assert(copy !== compound)
        assert(list !== listCopy)
        assertEquals(list, listCopy)
    }
    
    @Test
    fun testShallowCopy() {
        val compound = Compound()
        val list = listOf("a", "b", "c")
        compound["list"] = list
        
        val copy = compound.shallowCopy()
        val listCopy = copy.get<List<String>>("list")
        
        assert(copy !== compound)
        assert(list === listCopy)
    }
    
}