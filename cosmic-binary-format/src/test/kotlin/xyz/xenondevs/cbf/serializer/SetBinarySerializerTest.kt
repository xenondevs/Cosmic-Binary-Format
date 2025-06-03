package xyz.xenondevs.cbf.serializer

import org.junit.jupiter.api.Test
import xyz.xenondevs.commons.collections.enumSetOf
import java.util.*
import kotlin.test.assertEquals

class SetBinarySerializerTest : BinarySerializerFactoryTest<Set<*>>(CollectionBinarySerializer) {
    
    private enum class TestEnum {
        A, B, C
    }
    
    @Test
    fun testStringSet() {
        val set = setOf("a", "b", "c")
        assertEquals(set, reserializeValue(set))
    }
    
    @Test
    fun testIntSet() {
        val set = setOf(1, 2, 3)
        assertEquals(set, reserializeValue(set))
    }
    
    @Test
    fun testSetCopyEquals() {
        val set = setOf("a", "b", "c")
        assertEquals(set, copyValue(set))
    }
    
    @Test
    fun testSetCopyNotSame() {
        val set = setOf("a", "b", "c")
        assert(set !== copyValue(set))
    }
    
    @Test
    fun testSetCopyDeep() {
        val nestedSet = setOf("a", "b", "c")
        val set = setOf(nestedSet)
        val copy = copyValue(set)
        assert(nestedSet !== copy.first())
    }
    
    @Test
    fun testCorrectSetImplementation() {
        val set = enumSetOf(TestEnum.A, TestEnum.B, TestEnum.C)
        assertEquals(set, reserializeValue(set))
        
        assert(deserializeValue<Set<TestEnum>>(serializeValue(set)) is EnumSet)
        assert(deserializeValue<MutableSet<TestEnum>>(serializeValue(set)) is EnumSet)
        
        assert(deserializeValue<Set<String>>(serializeValue(set)) is HashSet)
        assert(deserializeValue<MutableSet<String>>(serializeValue(set)) is HashSet)
    }
    
}