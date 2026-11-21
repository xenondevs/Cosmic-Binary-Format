package xyz.xenondevs.cbf

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

/**
 * Tests that large lengths and sizes are read correctly for known and unknown remaining counts.
 */
class LargeReadTest {
    
    @ParameterizedTest
    @EnumSource(Remaining::class)
    fun `readBytes reads large lengths`(remaining: Remaining) {
        val data = ByteArray(100_000) { it.toByte() }
        assertContentEquals(data, byteReader(remaining, data).readBytes(100_000))
    }
    
    @ParameterizedTest
    @EnumSource(Remaining::class)
    fun `readString reads large strings`(remaining: Remaining) {
        val str = "a".repeat(100_000)
        assertEquals(str, Cbf.read<String>(byteReader(remaining, Cbf.write(str))))
    }
    
    @ParameterizedTest
    @EnumSource(Remaining::class)
    fun `array serializers read large arrays`(remaining: Remaining) {
        val size = 50_000
        
        val bytes = ByteArray(size) { it.toByte() }
        assertContentEquals(bytes, Cbf.read<ByteArray>(byteReader(remaining, Cbf.write(bytes))))
        
        val shorts = ShortArray(size) { it.toShort() }
        assertContentEquals(shorts, Cbf.read<ShortArray>(byteReader(remaining, Cbf.write(shorts))))
        
        val ints = IntArray(size) { it }
        assertContentEquals(ints, Cbf.read<IntArray>(byteReader(remaining, Cbf.write(ints))))
        
        val longs = LongArray(size) { it.toLong() }
        assertContentEquals(longs, Cbf.read<LongArray>(byteReader(remaining, Cbf.write(longs))))
        
        val floats = FloatArray(size) { it.toFloat() }
        assertContentEquals(floats, Cbf.read<FloatArray>(byteReader(remaining, Cbf.write(floats))))
        
        val doubles = DoubleArray(size) { it.toDouble() }
        assertContentEquals(doubles, Cbf.read<DoubleArray>(byteReader(remaining, Cbf.write(doubles))))
        
        val booleans = BooleanArray(size) { it % 2 == 0 }
        assertContentEquals(booleans, Cbf.read<BooleanArray>(byteReader(remaining, Cbf.write(booleans))))
        
        val chars = CharArray(size) { it.toChar() }
        assertContentEquals(chars, Cbf.read<CharArray>(byteReader(remaining, Cbf.write(chars))))
        
        val strings = Array(size) { "string$it" }
        assertContentEquals(strings, Cbf.read<Array<String>>(byteReader(remaining, Cbf.write(strings))))
    }
    
}
