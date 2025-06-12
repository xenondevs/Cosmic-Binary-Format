package xyz.xenondevs.cbf

import xyz.xenondevs.commons.collections.enumMapOf
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull

class HierarchyAdapterTest {
    
    @Test
    fun testEnumMap() {
        val enumMap = enumMapOf(TimeUnit.SECONDS to "s")
        val serialized = Cbf.write(enumMap)
        val deserialized = Cbf.read<EnumMap<TimeUnit, String>>(serialized)
        
        assertNotNull(deserialized)
        assertContentEquals(enumMap, deserialized)
    }
    
    @Test
    fun testCopyOnWriteArrayList() {
        val list = CopyOnWriteArrayList<String>().apply { add("A") }
        val serialized = Cbf.write(list)
        val deserialized = Cbf.read<CopyOnWriteArrayList<String>>(serialized)
        
        assertNotNull(deserialized)
        assertContentEquals(list, deserialized)
    }
    
}