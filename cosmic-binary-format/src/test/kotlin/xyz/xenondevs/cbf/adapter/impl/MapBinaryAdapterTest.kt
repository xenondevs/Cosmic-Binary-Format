package xyz.xenondevs.cbf.adapter.impl

import org.junit.jupiter.api.Test
import xyz.xenondevs.cbf.adapter.BinaryAdapterTest
import xyz.xenondevs.cbf.assertContentEquals
import xyz.xenondevs.commons.collections.enumMapOf
import java.util.EnumMap

private enum class TestEnum {
    A, B, C
}

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
    
    @Test
    fun testCorrectMapImplementation() {
        val map = enumMapOf(TestEnum.A to "a", TestEnum.B to "b", TestEnum.C to "c")
        assertContentEquals(map, reserializeValue(map))
        
        assert(deserializeValue<Map<TestEnum, String>>(serializeValue(map)) is EnumMap)
        assert(deserializeValue<MutableMap<TestEnum, String>>(serializeValue(map)) is EnumMap)
        
        assert(deserializeValue<Map<String, String>>(serializeValue(map)) is HashMap)
        assert(deserializeValue<MutableMap<String, String>>(serializeValue(map)) is HashMap)
    }
    
}