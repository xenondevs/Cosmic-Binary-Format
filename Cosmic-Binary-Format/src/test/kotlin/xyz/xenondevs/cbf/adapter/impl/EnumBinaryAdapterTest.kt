package xyz.xenondevs.cbf.adapter.impl

import org.junit.jupiter.api.Test
import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.adapter.BinaryAdapterTest
import java.util.concurrent.TimeUnit
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class EnumBinaryAdapterTest : BinaryAdapterTest<Enum<*>>(EnumBinaryAdapter) {
    
    @Test
    fun testEnumStringBased() {
        val value = TimeUnit.DAYS
        val bin = CBF.write(value)
        assert(String(bin).contains(value.toString()))
        assertEquals(value, CBF.read(bin))
    }
    
    @Test
    fun testEnumOrdinalBased() {
        EnumBinaryAdapter.addOrdinalEnums(TimeUnit::class)
        val value = TimeUnit.DAYS
        val bin = CBF.write(value)
        
        assertContentEquals(bin, byteWriter { 
            writeUnsignedByte(2U)
            writeVarInt(value.ordinal)
        })
    
        assertEquals(value, CBF.read(bin))
    }
    
}