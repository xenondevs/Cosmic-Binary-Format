package xyz.xenondevs.cbf.io

import java.util.*
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ByteReaderTest {
    
    private fun byteReaderOf(vararg bytes: Byte): ByteReader =
        ByteReader.fromByteArray(byteArrayOf(*bytes))
    
    private fun byteReaderOfInts(vararg ints: Int): ByteReader =
        ByteReader.fromByteArray(ByteArray(ints.size * 4) {
            val int = ints[it / 4]
            val byte = 3 - (it % 4)
            (int ushr (byte * 8) and 0xFF).toByte()
        })
    
    private fun byteReaderOfIntsLE(vararg ints: Int): ByteReader =
        ByteReader.fromByteArray(ByteArray(ints.size * 4) {
            val int = ints[it / 4]
            val byte = it % 4
            (int ushr (byte * 8) and 0xFF).toByte()
        })
    
    private fun byteReaderOfLongs(vararg longs: Long): ByteReader =
        ByteReader.fromByteArray(ByteArray(longs.size * 8) {
            val long = longs[it / 8]
            val byte = 7 - (it % 8)
            (long ushr (byte * 8) and 0xFF).toByte()
        })
    
    private fun byteReaderOfLongsLE(vararg longs: Long): ByteReader =
        ByteReader.fromByteArray(ByteArray(longs.size * 8) {
            val long = longs[it / 8]
            val byte = it % 8
            (long ushr (byte * 8) and 0xFF).toByte()
        })
    
    @Test
    fun testReadBoolean() {
        val reader = byteReaderOf(0, 1)
        
        assertEquals(false, reader.readBoolean())
        assertEquals(true, reader.readBoolean())
    }
    
    @Test
    fun testReadByte() {
        val reader = byteReaderOf(Byte.MIN_VALUE, 0, Byte.MAX_VALUE)
        
        assertEquals(Byte.MIN_VALUE, reader.readByte())
        assertEquals(0, reader.readByte())
        assertEquals(Byte.MAX_VALUE, reader.readByte())
    }
    
    @Test
    fun testReadUnsignedByte() {
        val reader = byteReaderOf(0, -1)
        
        assertEquals(UByte.MIN_VALUE, reader.readUnsignedByte())
        assertEquals(UByte.MAX_VALUE, reader.readUnsignedByte())
    }
    
    @Test
    fun testReadShort() {
        val reader = byteReaderOf(
            -128, 0,
            0, 0,
            127, -1
        )
        
        assertEquals(Short.MIN_VALUE, reader.readShort())
        assertEquals(0, reader.readShort())
        assertEquals(Short.MAX_VALUE, reader.readShort())
    }
    
    @Test
    fun testReadShortLE() {
        val reader = byteReaderOf(
            0, -128,
            0, 0,
            -1, 127
        )
        
        assertEquals(Short.MIN_VALUE, reader.readShortLE())
        assertEquals(0, reader.readShortLE())
        assertEquals(Short.MAX_VALUE, reader.readShortLE())
    }
    
    @Test
    fun testReadUnsignedShort() {
        val reader = byteReaderOf(
            0, 0,
            0, 1,
            -1, -1
        )
        
        assertEquals(UShort.MIN_VALUE, reader.readUnsignedShort())
        assertEquals(1U, reader.readUnsignedShort())
        assertEquals(UShort.MAX_VALUE, reader.readUnsignedShort())
    }
    
    @Test
    fun testReadUnsignedShortLE() {
        val reader = byteReaderOf(
            0, 0,
            1, 0,
            -1, -1
        )
        
        assertEquals(UShort.MIN_VALUE, reader.readUnsignedShortLE())
        assertEquals(1U, reader.readUnsignedShortLE())
        assertEquals(UShort.MAX_VALUE, reader.readUnsignedShortLE())
    }
    
    @Test
    fun testReadMedium() {
        val reader = byteReaderOf(
            -128, 0, 0,
            0, 0, 0,
            127, -1, -1
        )
        
        assertEquals(-8388608, reader.readMedium())
        assertEquals(0, reader.readMedium())
        assertEquals(8388607, reader.readMedium())
    }
    
    @Test
    fun testReadMediumLE() {
        val reader = byteReaderOf(
            0, 0, -128,
            0, 0, 0,
            -1, -1, 127
        )
    
        assertEquals(-8388608, reader.readMediumLE())
        assertEquals(0, reader.readMediumLE())
        assertEquals(8388607, reader.readMediumLE())
    }
    
    @Test
    fun testReadInt() {
        val reader = byteReaderOf(
            -128, 0, 0, 0,
            0, 0, 0, 0,
            127, -1, -1, -1
        )
        
        assertEquals(Int.MIN_VALUE, reader.readInt())
        assertEquals(0, reader.readInt())
        assertEquals(Int.MAX_VALUE, reader.readInt())
    }
    
    @Test
    fun testReadIntLE() {
        val reader = byteReaderOf(
            0, 0, 0, -128,
            0, 0, 0, 0,
            -1, -1, -1, 127
        )
        
        assertEquals(Int.MIN_VALUE, reader.readIntLE())
        assertEquals(0, reader.readIntLE())
        assertEquals(Int.MAX_VALUE, reader.readIntLE())
    }
    
    @Test
    fun testReadUnsignedInt() {
        val reader = byteReaderOf(
            0, 0, 0, 0,
            0, 0, 0, 1,
            -1, -1, -1, -1
        )
        
        assertEquals(UInt.MIN_VALUE, reader.readUnsignedInt())
        assertEquals(1U, reader.readUnsignedInt())
        assertEquals(UInt.MAX_VALUE, reader.readUnsignedInt())
    }
    
    @Test
    fun testReadUnsignedIntLE() {
        val reader = byteReaderOf(
            0, 0, 0, 0,
            1, 0, 0, 0,
            -1, -1, -1, -1
        )
        
        assertEquals(UInt.MIN_VALUE, reader.readUnsignedIntLE())
        assertEquals(1U, reader.readUnsignedIntLE())
        assertEquals(UInt.MAX_VALUE, reader.readUnsignedIntLE())
    }
    
    @Test
    fun testReadLong() {
        val reader = byteReaderOf(
            -128, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            127, -1, -1, -1, -1, -1, -1, -1
        )
        
        assertEquals(Long.MIN_VALUE, reader.readLong())
        assertEquals(0L, reader.readLong())
        assertEquals(Long.MAX_VALUE, reader.readLong())
    }
    
    @Test
    fun testReadUnsignedLong() {
        val reader = byteReaderOf(
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 1,
            -1, -1, -1, -1, -1, -1, -1, -1
        )
        
        assertEquals(ULong.MIN_VALUE, reader.readUnsignedLong())
        assertEquals(1UL, reader.readUnsignedLong())
        assertEquals(ULong.MAX_VALUE, reader.readUnsignedLong())
    }
    
    @Test
    fun testReadLongLE() {
        val reader = byteReaderOf(
            0, 0, 0, 0, 0, 0, 0, -128,
            0, 0, 0, 0, 0, 0, 0, 0,
            -1, -1, -1, -1, -1, -1, -1, 127
        )
        
        assertEquals(Long.MIN_VALUE, reader.readLongLE())
        assertEquals(0L, reader.readLongLE())
        assertEquals(Long.MAX_VALUE, reader.readLongLE())
    }
    
    @Test
    fun testReadUnsignedLongLE() {
        val reader = byteReaderOf(
            0, 0, 0, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 0, 0,
            -1, -1, -1, -1, -1, -1, -1, -1
        )
        
        assertEquals(ULong.MIN_VALUE, reader.readUnsignedLongLE())
        assertEquals(1UL, reader.readUnsignedLongLE())
        assertEquals(ULong.MAX_VALUE, reader.readUnsignedLongLE())
    }
    
    @Test
    fun testReadFloat() {
        val reader = byteReaderOfInts(
            Float.MIN_VALUE.toBits(),
            0f.toBits(),
            1f.toBits(),
            Float.MAX_VALUE.toBits()
        )
        
        assertEquals(Float.MIN_VALUE, reader.readFloat())
        assertEquals(0f, reader.readFloat())
        assertEquals(1f, reader.readFloat())
        assertEquals(Float.MAX_VALUE, reader.readFloat())
    }
    
    @Test
    fun testReadFloatLE() {
        val reader = byteReaderOfIntsLE(
            Float.MIN_VALUE.toBits(),
            0f.toBits(),
            1f.toBits(),
            Float.MAX_VALUE.toBits()
        )
        
        assertEquals(Float.MIN_VALUE, reader.readFloatLE())
        assertEquals(0f, reader.readFloatLE())
        assertEquals(1f, reader.readFloatLE())
        assertEquals(Float.MAX_VALUE, reader.readFloatLE())
    }
    
    @Test
    fun testReadDouble() {
        val reader = byteReaderOfLongs(
            Double.MIN_VALUE.toBits(),
            0.0.toBits(),
            1.0.toBits(),
            Double.MAX_VALUE.toBits()
        )
        
        assertEquals(Double.MIN_VALUE, reader.readDouble())
        assertEquals(0.0, reader.readDouble())
        assertEquals(1.0, reader.readDouble())
        assertEquals(Double.MAX_VALUE, reader.readDouble())
    }
    
    @Test
    fun testReadDoubleLE() {
        val reader = byteReaderOfLongsLE(
            Double.MIN_VALUE.toBits(),
            0.0.toBits(),
            1.0.toBits(),
            Double.MAX_VALUE.toBits()
        )
        
        assertEquals(Double.MIN_VALUE, reader.readDoubleLE())
        assertEquals(0.0, reader.readDoubleLE())
        assertEquals(1.0, reader.readDoubleLE())
        assertEquals(Double.MAX_VALUE, reader.readDoubleLE())
    }
    
    @Test
    fun testReadVarInt() {
        val reader = byteReaderOf(
            -128, -128, -128, -128, 8,
            0,
            -1, -1, -1, -1, 7
        )
        
        assertEquals(Int.MIN_VALUE, reader.readVarInt())
        assertEquals(0, reader.readVarInt())
        assertEquals(Int.MAX_VALUE, reader.readVarInt())
    }
    
    @Test
    fun testReadUnsignedVarInt() {
        val reader = byteReaderOf(
            0,
            1,
            -1, -1, -1, -1, 15
        )
        
        assertEquals(UInt.MIN_VALUE, reader.readUnsignedVarInt())
        assertEquals(1U, reader.readUnsignedVarInt())
        assertEquals(UInt.MAX_VALUE, reader.readUnsignedVarInt())
    }
    
    @Test
    fun testReadVarLong() {
        val reader = byteReaderOf(
            -128, -128, -128, -128, -128, -128, -128, -128, -128, 1,
            0,
            -1, -1, -1, -1, -1, -1, -1, -1, 127
        )
        
        assertEquals(Long.MIN_VALUE, reader.readVarLong())
        assertEquals(0L, reader.readVarLong())
        assertEquals(Long.MAX_VALUE, reader.readVarLong())
    }
    
    @Test
    fun testReadUnsignedVarLong() {
        val reader = byteReaderOf(
            0,
            1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, 1
        )
        
        assertEquals(ULong.MIN_VALUE, reader.readUnsignedVarLong())
        assertEquals(1UL, reader.readUnsignedVarLong())
        assertEquals(ULong.MAX_VALUE, reader.readUnsignedVarLong())
    }
    
    @Test
    fun testReadBytes() {
        val byteArray = byteArrayOf(-128, 0, 127)
        val reader = ByteReader.fromByteArray(byteArray)
        
        assertContentEquals(byteArray, reader.readBytes(3))
    }
    
    @Test
    fun testReadString() {
        val reader = byteReaderOf(
            3,
            'a'.code.toByte(), 'b'.code.toByte(), 'c'.code.toByte()
        )
        
        assertEquals("abc", reader.readString())
    }
    
    @Test
    fun readUUID() {
        val reader = byteReaderOf(
            -128, 0, 0, 0, 0, 0, 0, 0, 127, -1, -1, -1, -1, -1, -1, -1,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            127, -1, -1, -1, -1, -1, -1, -1, -128, 0, 0, 0, 0, 0, 0, 0
        )
        
        assertEquals(UUID(Long.MIN_VALUE, Long.MAX_VALUE), reader.readUUID())
        assertEquals(UUID(0, 0), reader.readUUID())
        assertEquals(UUID(Long.MAX_VALUE, Long.MIN_VALUE), reader.readUUID())
    }
    
}

