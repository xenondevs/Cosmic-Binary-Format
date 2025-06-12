package xyz.xenondevs.cbf

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import xyz.xenondevs.cbf.Compound.CompoundBinarySerializer
import xyz.xenondevs.cbf.serializer.read
import xyz.xenondevs.cbf.serializer.write
import xyz.xenondevs.commons.provider.MutableProvider
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
        
        outerCompound["compound"] = null
        
        assertEquals(null, valueEntry1.get())
        assertEquals(null, valueEntry2.get())
    }
    
    @Test
    fun testCompoundEntryNonNullDefaultValue() {
        val compound = Compound()
        
        val entry: MutableProvider<MutableList<Int>> = compound.entry("a", ::ArrayList)
        
        assertEquals(emptyList(), entry.get())
        
        entry.get().add(1)
        
        assertEquals(listOf(1), entry.get())
        
        compound["a"] = mutableListOf(2)
        
        assertEquals(listOf(2), entry.get())
        
        assertThrows<IllegalArgumentException> { compound["a"] = null } // null with Nothing? type
        assertThrows<IllegalArgumentException> { compound.set<MutableList<Int>?>("a", null) } // null with MutableList<Int>? type
        assertThrows<IllegalArgumentException> { compound["a"] = mutableListOf<String>() } // empty list of String instead of Int
    }
    
    @Test
    fun testCompoundEntryNullableDefaultValue() {
        var defaultEnabled = false
        val compound = Compound()
        val defaultValue: () -> MutableList<Int>? = { if (defaultEnabled) ArrayList() else null }
        val entryA: MutableProvider<MutableList<Int>?> = compound.entry("a", defaultValue)
        val entryB: MutableProvider<MutableList<Int>?> = compound.entry("b", defaultValue)
        
        assertEquals(null, entryA.get())
        
        defaultEnabled = true // captured in lambda above! note https://youtrack.jetbrains.com/issue/KTIJ-34162
        
        assertEquals(null, entryA.get()) // once resolved, defaultValue lambda should not be called again
        assertEquals(mutableListOf(), entryB.get())
        
        compound["a"] = mutableListOf<Int>()
        assertEquals(mutableListOf(), entryA.get())
        
        compound["a"] = null
        assertEquals(null, entryA.get()) // like above, defaultValue lambda should not be called again
    }
    
    @Test
    fun testCompoundEntryWithTwoDefaultValues() {
        val compound = Compound()
        
        val a1 by compound.entry("a") { 1 }
        val a2 by compound.entry("a") { 2 }
        
        // the initial default value is used, latter ones are ignored
        assertEquals(1, a1)
        assertEquals(1, a2)
    
        val b1 by compound.entry("b") { 1 }
        val b2 by compound.entry("b") { 2 }
        
        // order of resolution doesn't matter, instead order of entry calls matters
        assertEquals(1, b2)
        assertEquals(1, b1)
    }
    
    @Test
    fun testCompoundProviderEntryNonNullDefaultValue() {
        val outerCompound = Compound()
        val innerCompound = Compound()
        innerCompound["value1"] = 1
        
        val compoundEntry = outerCompound.entry<Compound>("compound", ::Compound)
        val value1 by compoundEntry.entry<Int>("value1") { -1 }
        val value2 by compoundEntry.entry<Int>("value2") { -1 }
        
        assertEquals(-1, value1)
        assertEquals(-1, value2)
        
        outerCompound["compound"] = innerCompound
        
        assertEquals(1, value1)
        assertEquals(-1, value2)
        
        innerCompound["value2"] = 1
        
        assertEquals(1, value1)
        assertEquals(1, value2)
        
        outerCompound["compound"] = Compound()
        
        assertEquals(-1, value1)
        assertEquals(-1, value2)
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
    fun testPutAllIgnoresEmptyProviderEntries() {
        val compoundA = Compound()
        compoundA["a"] = 1
        compoundA["b"] = 2
        compoundA.entry<Int>("c")
        
        val compoundB = Compound()
        compoundB["a"] = 3
        compoundB.entry<Int>("b")
        compoundB["c"] = 4
        
        compoundA.putAll(compoundB)
        
        assertEquals(3, compoundA["a"])
        assertEquals(2, compoundA["b"])
        assertEquals(4, compoundA["c"])
    }
    
    @Test
    fun testPutAllWithUnknownTypes() {
        val compoundA = Compound()
        compoundA["a"] = 1
        compoundA["b"] = 2
        compoundA["c"] = 3
        
        val compoundA1 = CompoundBinarySerializer.read(CompoundBinarySerializer.write(compoundA))!!
        
        val compoundB = Compound()
        compoundB["a"] = -1
        compoundB.entry("b") { -1 }
        
        compoundB.putAll(compoundA1)
        
        assertEquals(1, compoundB["a"])
        assertEquals(2, compoundB["b"])
        assertEquals(3, compoundB["c"])
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
        compound["a"] = null
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
        
        compound["a"] = null
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
    
    @Test
    fun `test get and modify mutable element on direct entry`() {
        val compound = Compound()
        val list = arrayListOf("a")
        compound["list"] = list
        
        compound.get<ArrayList<String>>("list")!! += "b"
        assertEquals(listOf("a", "b"), compound.get<List<String>>("list"))
        
        val reserializedCompound = Cbf.read<Compound>(Cbf.write(compound))!!
        reserializedCompound.get<ArrayList<String>>("list")!! += "c"
        assertEquals(listOf("a", "b", "c"), reserializedCompound.get<List<String>>("list"))
    }
    
    @Test
    fun `test that observed entry provider does not get removed when value is removed (set to null)`() {
        val compound = Compound()
        val entry: MutableProvider<Int?> = compound.entry("a")
        val entryValue by entry
        
        assertEquals(null, entryValue)
        
        entry.set(1)
        assertEquals(1, entryValue)
        
        compound["a"] = null
        assertEquals(null, entryValue)
        
        compound["a"] = 2
        assertEquals(2, entryValue)
    }
    
}