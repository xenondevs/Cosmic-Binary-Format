package xyz.xenondevs.cbf.adapter.impl

import org.junit.jupiter.api.Test
import xyz.xenondevs.cbf.adapter.BinaryAdapterTest
import kotlin.test.assertEquals

class MapBinaryAdapterTest : BinaryAdapterTest<Map<*, *>>(MapBinaryAdapter) {
    
    private fun <K, V> assertEquals(expected: Map<K, V>, actual: Map<K, V>) {
        assertEquals(expected.size, actual.size)
        expected.forEach { (key, value) -> assertEquals(value, actual[key]) }
    }
    
    @Test
    fun testMap() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)
        assertEquals(map, reserializeValue(map))
    }
    
}