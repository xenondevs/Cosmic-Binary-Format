package xyz.xenondevs.cbf.adapter.impl

import org.junit.jupiter.api.Test
import xyz.xenondevs.cbf.adapter.BinaryAdapterTest
import kotlin.test.assertContentEquals

class ListBinaryAdapterTest: BinaryAdapterTest<List<*>>(ListBinaryAdapter) {
    
    @Test
    fun testStringList() {
        val list = listOf("a", "b", "c")
        assertContentEquals(list, reserializeValue(list))
    }
    
    @Test
    fun testIntList() {
        val list = listOf(1, 2, 3)
        assertContentEquals(list, reserializeValue(list))
    }
    
    @Test
    fun testListCopyEquals() {
        val list = listOf("a", "b", "c")
        assertContentEquals(list, copyValue(list))
    }
    
    @Test
    fun testListCopyNotSame() {
        val list = listOf("a", "b", "c")
        assert(list !== copyValue(list))
    }
    
    @Test
    fun testListCopyDeep() {
        val nestedList = listOf("a", "b", "c")
        val list = listOf(nestedList)
        val copy = copyValue(list)
        assert(nestedList !== copy[0])
    }
    
}