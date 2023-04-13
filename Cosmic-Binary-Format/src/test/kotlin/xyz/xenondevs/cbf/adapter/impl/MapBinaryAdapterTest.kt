package xyz.xenondevs.cbf.adapter.impl

import org.junit.jupiter.api.Test
import xyz.xenondevs.cbf.adapter.BinaryAdapterTest
import xyz.xenondevs.cbf.assertContentEquals

class MapBinaryAdapterTest : BinaryAdapterTest<Map<*, *>>(MapBinaryAdapter) {
    
    @Test
    fun testMap() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)
        assertContentEquals(map, reserializeValue(map))
    }
    
    @Test
    fun testMapCopyEquals() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)
        assertContentEquals(map, copyValue(map))
    }
    
    @Test
    fun testMapCopyNotSame() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)
        assert(map !== copyValue(map))
    }
    
    @Test
    fun testMapCopyDeep() {
        val nestedMap = mapOf("a" to 1, "b" to 2, "c" to 3)
        val map = mapOf("nested" to nestedMap)
        val copy = copyValue(map)
        assert(nestedMap !== copy["nested"])
    }
    
}