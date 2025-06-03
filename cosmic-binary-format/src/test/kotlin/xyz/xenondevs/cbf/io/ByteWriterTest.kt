package xyz.xenondevs.cbf.io

import java.util.*
import kotlin.test.Test

class ByteWriterTest {
    
    private fun assertContentEquals(expected: ByteArray, vararg actual: Byte) {
        kotlin.test.assertContentEquals(expected, actual)
    }
    
    private fun Int.toByteArray(): ByteArray {
        return byteArrayOf(
            (this ushr 24).toByte(),
            (this ushr 16).toByte(),
            (this ushr 8).toByte(),
            this.toByte()
        )
    }
    
    private fun Int.toByteArrayLE(): ByteArray {
        return byteArrayOf(
            this.toByte(),
            (this ushr 8).toByte(),
            (this ushr 16).toByte(),
            (this ushr 24).toByte()
        )
    }
    
    private fun Long.toByteArray(): ByteArray {
        return byteArrayOf(
            (this ushr 56).toByte(),
            (this ushr 48).toByte(),
            (this ushr 40).toByte(),
            (this ushr 32).toByte(),
            (this ushr 24).toByte(),
            (this ushr 16).toByte(),
            (this ushr 8).toByte(),
            this.toByte()
        )
    }
    
    private fun Long.toByteArrayLE(): ByteArray {
        return byteArrayOf(
            this.toByte(),
            (this ushr 8).toByte(),
            (this ushr 16).toByte(),
            (this ushr 24).toByte(),
            (this ushr 32).toByte(),
            (this ushr 40).toByte(),
            (this ushr 48).toByte(),
            (this ushr 56).toByte()
        )
    }
    
    @Test
    fun testWriteBoolean() {
        assertContentEquals(
            byteWriter { writeBoolean(false) },
            0
        )
        
        assertContentEquals(
            byteWriter { writeBoolean(true) },
            1
        )
    }
    
    @Test
    fun testWriteByte() {
        assertContentEquals(
            byteWriter { writeByte(Byte.MIN_VALUE) },
            -128
        )
        
        assertContentEquals(
            byteWriter { writeByte(0) },
            0
        )
        
        assertContentEquals(
            byteWriter { writeByte(Byte.MAX_VALUE) },
            127
        )
    }
    
    @Test
    fun testWriteUnsignedByte() {
        assertContentEquals(
            byteWriter { writeUnsignedByte(UByte.MIN_VALUE) },
            0
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedByte(1U) },
            1
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedByte(UByte.MAX_VALUE) },
            -1
        )
    }
    
    @Test
    fun testWriteShort() {
        assertContentEquals(
            byteWriter { writeShort(Short.MIN_VALUE) },
            -128, 0
        )
        
        assertContentEquals(
            byteWriter { writeShort(0) },
            0, 0
        )
        
        assertContentEquals(
            byteWriter { writeShort(Short.MAX_VALUE) },
            127, -1
        )
    }
    
    @Test
    fun testWriteShortLE() {
        assertContentEquals(
            byteWriter { writeShortLE(Short.MIN_VALUE) },
            0, -128
        )
        
        assertContentEquals(
            byteWriter { writeShortLE(0) },
            0, 0
        )
        
        assertContentEquals(
            byteWriter { writeShortLE(Short.MAX_VALUE) },
            -1, 127
        )
    }
    
    @Test
    fun testWriteUnsignedShort() {
        assertContentEquals(
            byteWriter { writeUnsignedShort(UShort.MIN_VALUE) },
            0, 0
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedShort(1U) },
            0, 1
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedShort(UShort.MAX_VALUE) },
            -1, -1
        )
    }
    
    @Test
    fun testWriteUnsignedShortLE() {
        assertContentEquals(
            byteWriter { writeUnsignedShortLE(UShort.MIN_VALUE) },
            0, 0
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedShortLE(1U) },
            1, 0
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedShortLE(UShort.MAX_VALUE) },
            -1, -1
        )
    }
    
    @Test
    fun testWriteMedium() {
        assertContentEquals(
            byteWriter { writeMedium(-8388608) },
            -128, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeMedium(0) },
            0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeMedium(8388607) },
            127, -1, -1
        )
    }
    
    @Test
    fun testWriteMediumLE() {
        assertContentEquals(
            byteWriter { writeMediumLE(-8388608) },
            0, 0, -128
        )
        
        assertContentEquals(
            byteWriter { writeMediumLE(0) },
            0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeMediumLE(8388607) },
            -1, -1, 127
        )
    }
    
    @Test
    fun testWriteInt() {
        assertContentEquals(
            byteWriter { writeInt(Int.MIN_VALUE) },
            -128, 0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeInt(0) },
            0, 0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeInt(Int.MAX_VALUE) },
            127, -1, -1, -1
        )
    }
    
    @Test
    fun testWriteIntLE() {
        assertContentEquals(
            byteWriter { writeIntLE(Int.MIN_VALUE) },
            0, 0, 0, -128
        )
        
        assertContentEquals(
            byteWriter { writeIntLE(0) },
            0, 0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeIntLE(Int.MAX_VALUE) },
            -1, -1, -1, 127
        )
    }
    
    @Test
    fun testWriteUnsignedInt() {
        assertContentEquals(
            byteWriter { writeUnsignedInt(UInt.MIN_VALUE) },
            0, 0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedInt(1U) },
            0, 0, 0, 1
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedInt(UInt.MAX_VALUE) },
            -1, -1, -1, -1
        )
    }
    
    @Test
    fun testWriteUnsignedIntLE() {
        assertContentEquals(
            byteWriter { writeUnsignedIntLE(UInt.MIN_VALUE) },
            0, 0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedIntLE(1U) },
            1, 0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedIntLE(UInt.MAX_VALUE) },
            -1, -1, -1, -1
        )
    }
    
    @Test
    fun testWriteLong() {
        assertContentEquals(
            byteWriter { writeLong(Long.MIN_VALUE) },
            -128, 0, 0, 0, 0, 0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeLong(0) },
            0, 0, 0, 0, 0, 0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeLong(Long.MAX_VALUE) },
            127, -1, -1, -1, -1, -1, -1, -1
        )
    }
    
    @Test
    fun testWriteLongLE() {
        kotlin.test.assertContentEquals(
            byteWriter { writeLongLE(Long.MIN_VALUE) },
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, -128)
        )
    
        kotlin.test.assertContentEquals(
            byteWriter { writeLongLE(0) },
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
        )
    
        kotlin.test.assertContentEquals(
            byteWriter { writeLongLE(Long.MAX_VALUE) },
            byteArrayOf(-1, -1, -1, -1, -1, -1, -1, 127)
        )
    }
    
    @Test
    fun testWriteUnsignedLong() {
        assertContentEquals(
            byteWriter { writeUnsignedLong(ULong.MIN_VALUE) },
            0, 0, 0, 0, 0, 0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedLong(1UL) },
            0, 0, 0, 0, 0, 0, 0, 1
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedLong(ULong.MAX_VALUE) },
            -1, -1, -1, -1, -1, -1, -1, -1
        )
    }
    
    @Test
    fun testWriteUnsignedLongLE() {
        assertContentEquals(
            byteWriter { writeUnsignedLongLE(ULong.MIN_VALUE) },
            0, 0, 0, 0, 0, 0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedLongLE(1UL) },
            1, 0, 0, 0, 0, 0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedLongLE(ULong.MAX_VALUE) },
            -1, -1, -1, -1, -1, -1, -1, -1
        )
    }
    
    @Test
    fun testWriteFloat() {
        kotlin.test.assertContentEquals(
            byteWriter { writeFloat(Float.MIN_VALUE) },
            Float.MIN_VALUE.toBits().toByteArray()
        )
        
        assertContentEquals(
            byteWriter { writeFloat(0f) },
            0, 0, 0, 0
        )
    
        kotlin.test.assertContentEquals(
            byteWriter { writeFloat(Float.MAX_VALUE) },
            Float.MAX_VALUE.toBits().toByteArray()
        )
    }
    
    @Test
    fun testWriteFloatLE() {
        kotlin.test.assertContentEquals(
            byteWriter { writeFloatLE(Float.MIN_VALUE) },
            Float.MIN_VALUE.toBits().toByteArrayLE()
        )
        
        assertContentEquals(
            byteWriter { writeFloatLE(0f) },
            0, 0, 0, 0
        )
    
        kotlin.test.assertContentEquals(
            byteWriter { writeFloatLE(Float.MAX_VALUE) },
            Float.MAX_VALUE.toBits().toByteArrayLE()
        )
    }
    
    @Test
    fun testWriteDouble() {
        kotlin.test.assertContentEquals(
            byteWriter { writeDouble(Double.MIN_VALUE) },
            Double.MIN_VALUE.toBits().toByteArray()
        )
        
        assertContentEquals(
            byteWriter { writeDouble(0.0) },
            0, 0, 0, 0, 0, 0, 0, 0
        )
    
        kotlin.test.assertContentEquals(
            byteWriter { writeDouble(Double.MAX_VALUE) },
            Double.MAX_VALUE.toBits().toByteArray()
        )
    }
    
    @Test
    fun testWriteDoubleLE() {
        kotlin.test.assertContentEquals(
            byteWriter { writeDoubleLE(Double.MIN_VALUE) },
            Double.MIN_VALUE.toBits().toByteArrayLE()
        )
        
        assertContentEquals(
            byteWriter { writeDoubleLE(0.0) },
            0, 0, 0, 0, 0, 0, 0, 0
        )
    
        kotlin.test.assertContentEquals(
            byteWriter { writeDoubleLE(Double.MAX_VALUE) },
            Double.MAX_VALUE.toBits().toByteArrayLE()
        )
    }
    
    @Test
    fun testWriteVarInt() {
        assertContentEquals(
            byteWriter { writeVarInt(Int.MIN_VALUE) },
            -128, -128, -128, -128, 8
        )
        
        assertContentEquals(
            byteWriter { writeVarInt(0) },
            0
        )
        
        assertContentEquals(
            byteWriter { writeVarInt(Int.MAX_VALUE) },
            -1, -1, -1, -1, 7
        )
    }
    
    @Test
    fun writeUnsignedVarInt() {
        assertContentEquals(
            byteWriter { writeUnsignedVarInt(0U) },
            0
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedVarInt(1U) },
            1
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedVarInt(UInt.MAX_VALUE) },
            -1, -1, -1, -1, 15
        )
    }
    
    @Test
    fun testWriteVarLong() {
        assertContentEquals(
            byteWriter { writeVarLong(Long.MIN_VALUE) },
            -128, -128, -128, -128, -128, -128, -128, -128, -128, 1
        )
        
        assertContentEquals(
            byteWriter { writeVarLong(0L) },
            0
        )
        
        assertContentEquals(
            byteWriter { writeVarLong(Long.MAX_VALUE) },
            -1, -1, -1, -1, -1, -1, -1, -1, 127
        )
    }
    
    @Test
    fun testWriteUnsignedVarLong() {
        assertContentEquals(
            byteWriter { writeUnsignedVarLong(0UL) },
            0
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedVarLong(1UL) },
            1
        )
        
        assertContentEquals(
            byteWriter { writeUnsignedVarLong(ULong.MAX_VALUE) },
            -1, -1, -1, -1, -1, -1, -1, -1, -1, 1
        )
    }
    
    
    
    @Test
    fun testWriteString() {
        assertContentEquals(
            byteWriter { writeString("abc") },
            3, 'a'.code.toByte(), 'b'.code.toByte(), 'c'.code.toByte()
        )
    }
    
    @Test
    fun testWriteUUID() {
        assertContentEquals(
            byteWriter { writeUUID(UUID(Long.MIN_VALUE, Long.MAX_VALUE)) },
            -128, 0, 0, 0, 0, 0, 0, 0,
            127, -1, -1, -1, -1, -1, -1, -1
        )
        
        assertContentEquals(
            byteWriter { writeUUID(UUID(0, 0)) },
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0
        )
        
        assertContentEquals(
            byteWriter { writeUUID(UUID(Long.MAX_VALUE, Long.MIN_VALUE)) },
            127, -1, -1, -1, -1, -1, -1, -1,
            -128, 0, 0, 0, 0, 0, 0, 0
        )
    }
    
}