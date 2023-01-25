package xyz.xenondevs.cbf.adapter.impl

import org.junit.jupiter.api.Test
import xyz.xenondevs.cbf.adapter.BinaryAdapterTest
import kotlin.test.assertEquals

class TripleBinaryAdapterTest : BinaryAdapterTest<Triple<*, *, *>>(TripleBinaryAdapter) {
    
    @Test
    fun testTriple() {
        val triple = Triple("a", 1, 2.0)
        assertEquals(triple, reserializeValue(triple))
    }
    
}