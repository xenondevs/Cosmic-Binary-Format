package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.adapter.BinaryAdapterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class CollectionBinaryAdapterTest : BinaryAdapterTest<Collection<*>>(CollectionBinaryAdapter) {
    
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
    
}