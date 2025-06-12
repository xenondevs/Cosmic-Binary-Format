package xyz.xenondevs.cbf.serializer

import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class ListBinarySerializerTest: BinarySerializerFactoryTest<List<*>>(CollectionBinarySerializer) {
    
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
        assert(nestedList !== copy?.get(0))
    }
    
}