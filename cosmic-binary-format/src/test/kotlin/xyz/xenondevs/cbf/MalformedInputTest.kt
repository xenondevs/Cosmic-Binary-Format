package xyz.xenondevs.cbf

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.byteWriter
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.EOFException
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

enum class Remaining { KNOWN, UNKNOWN_STREAM, UNKNOWN_DATA_INPUT }

fun byteReader(remaining: Remaining, bin: ByteArray): ByteReader =
    when (remaining) {
        Remaining.KNOWN -> ByteReader.fromByteArray(bin)
        Remaining.UNKNOWN_STREAM -> ByteReader.fromStream(DataInputStream(ByteArrayInputStream(bin)))
        Remaining.UNKNOWN_DATA_INPUT -> ByteReader.fromDataInput(DataInputStream(ByteArrayInputStream(bin)))
    }

/**
 * Tests that lengths, counts and sizes read from malformed input are rejected.
 */
class MalformedInputTest {
    
    private fun declaredEmptyArray(size: Int = 1 shl 30) = byteWriter {
        writeUnsignedByte(1U)
        writeVarInt(size)
    }
    
    @ParameterizedTest
    @EnumSource(Remaining::class)
    fun `readBytes rejects lengths larger than the remaining input`(remaining: Remaining) {
        val reader = byteReader(remaining, byteWriter { writeVarInt(Int.MAX_VALUE) })
        assertThrows<EOFException> { reader.readBytes(reader.readVarInt()) }
    }
    
    @ParameterizedTest
    @EnumSource(Remaining::class)
    fun `readString rejects lengths larger than the remaining input`(remaining: Remaining) {
        val bin = byteWriter { writeVarInt(Int.MAX_VALUE) }
        assertThrows<EOFException> { byteReader(remaining, bin).readString() }
    }
    
    @ParameterizedTest
    @EnumSource(Remaining::class)
    fun `array serializers reject sizes larger than the remaining input`(remaining: Remaining) {
        assertThrows<EOFException> { Cbf.read<ByteArray>(byteReader(remaining, declaredEmptyArray())) }
        assertThrows<EOFException> { Cbf.read<ShortArray>(byteReader(remaining, declaredEmptyArray())) }
        assertThrows<EOFException> { Cbf.read<IntArray>(byteReader(remaining, declaredEmptyArray())) }
        assertThrows<EOFException> { Cbf.read<LongArray>(byteReader(remaining, declaredEmptyArray())) }
        assertThrows<EOFException> { Cbf.read<FloatArray>(byteReader(remaining, declaredEmptyArray())) }
        assertThrows<EOFException> { Cbf.read<DoubleArray>(byteReader(remaining, declaredEmptyArray())) }
        assertThrows<EOFException> { Cbf.read<BooleanArray>(byteReader(remaining, declaredEmptyArray())) }
        assertThrows<EOFException> { Cbf.read<CharArray>(byteReader(remaining, declaredEmptyArray())) }
        assertThrows<EOFException> { Cbf.read<Array<String>>(byteReader(remaining, declaredEmptyArray())) }
    }
    
    @ParameterizedTest
    @EnumSource(Remaining::class)
    fun `array serializers reject negative sizes`(remaining: Remaining) {
        assertThrows<IllegalArgumentException> { Cbf.read<ByteArray>(byteReader(remaining, declaredEmptyArray(-1))) }
        assertThrows<IllegalArgumentException> { Cbf.read<ShortArray>(byteReader(remaining, declaredEmptyArray(-1))) }
        assertThrows<IllegalArgumentException> { Cbf.read<IntArray>(byteReader(remaining, declaredEmptyArray(-1))) }
        assertThrows<IllegalArgumentException> { Cbf.read<LongArray>(byteReader(remaining, declaredEmptyArray(-1))) }
        assertThrows<IllegalArgumentException> { Cbf.read<FloatArray>(byteReader(remaining, declaredEmptyArray(-1))) }
        assertThrows<IllegalArgumentException> { Cbf.read<DoubleArray>(byteReader(remaining, declaredEmptyArray(-1))) }
        assertThrows<IllegalArgumentException> { Cbf.read<BooleanArray>(byteReader(remaining, declaredEmptyArray(-1))) }
        assertThrows<IllegalArgumentException> { Cbf.read<CharArray>(byteReader(remaining, declaredEmptyArray(-1))) }
        assertThrows<IllegalArgumentException> { Cbf.read<Array<String>>(byteReader(remaining, declaredEmptyArray(-1))) }
    }
    
    @ParameterizedTest
    @EnumSource(Remaining::class)
    fun `compound rejects forged entry lengths`(remaining: Remaining) {
        val bin = byteWriter {
            writeUnsignedByte(2U)
            writeVarInt(1)
            writeString("a")
            writeVarInt(Int.MAX_VALUE)
        }
        assertThrows<EOFException> { Cbf.read<Compound>(byteReader(remaining, bin)) }
    }
    
    @ParameterizedTest
    @EnumSource(Remaining::class)
    fun `compound rejects forged map sizes`(remaining: Remaining) {
        val bin = byteWriter {
            writeUnsignedByte(2U)
            writeVarInt(1 shl 30)
        }
        assertThrows<EOFException> { Cbf.read<Compound>(byteReader(remaining, bin)) }
    }
    
    @ParameterizedTest
    @EnumSource(Remaining::class)
    fun `compound rejects negative map sizes`(remaining: Remaining) {
        val bin = byteWriter {
            writeUnsignedByte(2U)
            writeVarInt(-1)
        }
        assertThrows<IllegalArgumentException> { Cbf.read<Compound>(byteReader(remaining, bin)) }
    }
    
    @ParameterizedTest
    @EnumSource(Remaining::class)
    fun `collections reject sizes larger than the remaining input`(remaining: Remaining) {
        val bin = byteWriter {
            writeUnsignedByte(1U)
            writeVarInt(1 shl 30)
        }
        
        assertThrows<EOFException> { Cbf.read<MutableList<Int>>(byteReader(remaining, bin)) }
        assertThrows<EOFException> { Cbf.read<MutableMap<String, Int>>(byteReader(remaining, bin)) }
    }
    
    @ParameterizedTest
    @EnumSource(Remaining::class)
    fun `collections reject negative sizes`(remaining: Remaining) {
        val bin = byteWriter {
            writeUnsignedByte(1U)
            writeVarInt(-1)
        }
        
        // these collection creators ignore the size, so the negative size must be rejected by the serializer
        assertThrows<IllegalArgumentException> { Cbf.read<LinkedList<Int>>(byteReader(remaining, bin)) }
        assertThrows<IllegalArgumentException> { Cbf.read<TreeMap<String, Int>>(byteReader(remaining, bin)) }
    }
    
    @ParameterizedTest
    @EnumSource(Remaining::class)
    fun `truncated values are rejected`(remaining: Remaining) {
        val bin = byteWriter {
            writeUnsignedByte(1U)
            writeZero(8)
        }
        assertThrows<EOFException> { Cbf.read<UUID>(byteReader(remaining, bin)) }
    }
    
    @Test
    fun `strict reads reject leftover bytes`() {
        val bin = byteWriter {
            writeUnsignedByte(1U)
            writeVarInt(2)
            writeZero(1)
        }
        
        assertThrows<IllegalArgumentException> { Cbf.read<Int>(bin) }
        assertEquals(2, Cbf.read<Int>(bin, strict = false))
    }
    
}
