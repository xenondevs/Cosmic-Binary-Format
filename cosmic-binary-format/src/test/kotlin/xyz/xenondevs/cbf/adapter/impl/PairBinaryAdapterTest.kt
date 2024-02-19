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
    
    @Test
    fun testPairCopyEquals() {
        val pair = "a" to 1
        assertEquals(pair, copyValue(pair))
    }
    
    @Test
    fun testPairCopyNotSame() {
        val pair = "a" to 1
        assert(pair !== copyValue(pair))
    }
    
    @Test
    fun testPairCopyDeep() {
        val nestedPair = "a" to 1
        val pair = "nested" to nestedPair
        val copy = copyValue(pair)
        assert(nestedPair !== copy.second)
    }
    
}