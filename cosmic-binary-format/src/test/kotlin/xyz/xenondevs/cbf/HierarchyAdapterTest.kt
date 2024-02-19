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
        val serialized = CBF.write(enumMap)
        val deserialized = CBF.read<EnumMap<TimeUnit, String>>(serialized)
        
        assertNotNull(deserialized)
        assertContentEquals(enumMap, deserialized)
    }
    
    @Test
    fun testCopyOnWriteArrayList() {
        val list = CopyOnWriteArrayList<String>().apply { add("A") }
        val serialized = CBF.write(list)
        val deserialized = CBF.read<CopyOnWriteArrayList<String>>(serialized)
        
        assertNotNull(deserialized)
        assertContentEquals(list, deserialized)
    }
    
}