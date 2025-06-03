package xyz.xenondevs.cbf.serializer

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import xyz.xenondevs.cbf.CBF
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

enum class TestEnum1 {
    FIRST, SECOND, THIRD
}

enum class TestEnum2 {
    FIRST, SECOND, THIRD
}

class EnumBinarySerializerTest : BinarySerializerFactoryTest<Enum<*>>(EnumBinarySerializer) {
    
    @MethodSource("testEnum1Values")
    @ParameterizedTest
    fun testEnumStringBased(value: TestEnum1) {
        val bin = CBF.write(value)
        assert(String(bin).contains(value.toString()))
        assertEquals(value, CBF.read(bin))
    }
    
    @MethodSource("testEnum2Values")
    @ParameterizedTest
    fun testEnumOrdinalBased(value: TestEnum2) {
        val bin = CBF.write(value)
        
        assertContentEquals(bin, byteWriter { 
            writeUnsignedByte(2U)
            writeVarInt(value.ordinal)
        })
    
        assertEquals(value, CBF.read(bin))
    }
    
    companion object {
        
        @BeforeAll
        @JvmStatic
        fun setup() {
            CBF.addOrdinalEnums(TestEnum2::class)
        }
        
        @JvmStatic
        fun testEnum1Values() = TestEnum1.entries
        
        @JvmStatic
        fun testEnum2Values() = TestEnum2.entries
        
    }
    
}