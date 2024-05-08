package xyz.xenondevs.cbf.provider

import org.junit.jupiter.api.Test
import xyz.xenondevs.cbf.Compound
import xyz.xenondevs.commons.provider.immutable.provider
import xyz.xenondevs.commons.provider.mutable.mapNonNull
import xyz.xenondevs.commons.provider.mutable.mutableProvider
import kotlin.test.assertEquals

class CompoundProviderEntryProviderTest {
    
    @Test
    fun testExistingEntry() {
        val compound = Compound()
        compound["a"] = "b"
        val compoundProvider = provider(compound)
        
        val provider = compoundProvider.entry<String>("a")
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
        val compoundProvider = provider(compound)
        val provider = compoundProvider.entry<String>("a")
        assertEquals(null, provider.get())
        
        provider.set("b")
        assertEquals("b", compound["a"])
    }
    
    @Test
    fun testCompoundSwap() {
        val compound1 = Compound()
        val compound2 = Compound()
        
        compound1["a"] = "b"
        compound2["a"] = "c"
        
        val compoundProvider = mutableProvider(compound1)
        val provider = compoundProvider.entry<String>("a")
        
        assertEquals("b", provider.get())
        
        compoundProvider.set(compound2)
        assertEquals("c", provider.get())
        
        compound1.remove("a")
        assertEquals("c", provider.get())
        
        compound2.remove("a")
        assertEquals(null, provider.get())
        
        compoundProvider.set(compound1)
        assertEquals(null, provider.get())
        
        compound1["a"] = "d"
        assertEquals("d", provider.get())
    }
    
}