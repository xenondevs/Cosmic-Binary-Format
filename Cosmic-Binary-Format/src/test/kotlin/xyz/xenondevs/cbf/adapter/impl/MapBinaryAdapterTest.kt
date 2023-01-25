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
    
}