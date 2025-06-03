package xyz.xenondevs.cbf.serializer

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TripleBinarySerializerTest : BinarySerializerFactoryTest<Triple<*, *, *>>(TripleBinarySerializer) {
    
    @Test
    fun testTriple() {
        val triple = Triple("a", 1, 2.0)
        assertEquals(triple, reserializeValue(triple))
    }
    
    @Test
    fun testTripleCopyEquals() {
        val triple = Triple("a", 1, 2.0)
        assertEquals(triple, copyValue(triple))
    }
    
    @Test
    fun testTripleCopyNotSame() {
        val triple = Triple("a", 1, 2.0)
        assert(triple !== copyValue(triple))
    }
    
    @Test
    fun testTripleCopyDeep() {
        val nestedTriple = Triple("a", 1, 2.0)
        val triple = Triple(nestedTriple, nestedTriple, nestedTriple)
        val copy = copyValue(triple)
        assert(nestedTriple !== copy.first && nestedTriple !== copy.second && nestedTriple !== copy.third)
    }
    
}