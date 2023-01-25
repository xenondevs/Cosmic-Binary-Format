package xyz.xenondevs.cbf.adapter.impl

import org.junit.jupiter.api.Test
import xyz.xenondevs.cbf.adapter.BinaryAdapterTest
import kotlin.test.assertEquals

class PairBinaryAdapterTest : BinaryAdapterTest<Pair<*, *>>(PairBinaryAdapter) {
    
    @Test
    fun testPair() {
        val pair = "a" to 1
        assertEquals(pair, reserializeValue(pair))
    }
    
}