package xyz.xenondevs.cbf.provider

import org.junit.jupiter.api.Test
import xyz.xenondevs.cbf.Compound
import xyz.xenondevs.commons.provider.mutable.mapNonNull
import kotlin.test.assertEquals

class CompoundEntryProviderTest {
    
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
    fun testNonExistentEntry() {
        val compound = Compound()
        val provider = compound.entry<String>("a")
        assertEquals(null, provider.get())
        
        provider.set("b")
        assertEquals("b", compound["a"])
    }
    
}