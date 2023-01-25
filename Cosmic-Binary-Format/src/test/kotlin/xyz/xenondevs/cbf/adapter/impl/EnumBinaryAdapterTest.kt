package xyz.xenondevs.cbf.adapter.impl

import org.junit.jupiter.api.Test
import xyz.xenondevs.cbf.adapter.BinaryAdapterTest
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class EnumBinaryAdapterTest : BinaryAdapterTest<Enum<*>>(EnumBinaryAdapter) {
    
    @Test
    fun testEnum() {
        val value = TimeUnit.DAYS
        assertEquals(value, reserializeValue(value))
    }
    
}