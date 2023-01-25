package xyz.xenondevs.cbf.io

import java.io.ByteArrayInputStream
import java.io.DataInput
import java.io.InputStream
import java.util.*

interface ByteReader {
    
    /**
     * Reads a [Byte].
     */
    fun readByte(): Byte
    
    /**
     * Skips the given amount of bytes.
     */
    fun skip(length: Int)
    
    /**
     * Reads a [Boolean].
     */
    fun readBoolean(): Boolean {
        return readByte() != 0.toByte()
    }
    
    /**
     * Reads a [UByte].
     */
    fun readUnsignedByte(): UByte {
        return readByte().toUByte()
    }
    
    /**
     * Reads a [Short].
     */
    fun readShort(): Short {
        return (readByte().toInt() shl 8 or (readByte().toInt() and 255)).toShort()
    }
    
    /**
     * Reads a [Short] in little endian.
     */
    fun readShortLE(): Short {
        return (readByte().toInt() and 255 or (readByte().toInt() shl 8)).toShort()
    }
    
    /**
     * Reads a [UShort].
     */
    fun readUnsignedShort(): UShort {
        return readShort().toUShort()
    }
    
    /**
     * Read a [UShort] in little endian.
     */
    fun readUnsignedShortLE(): UShort {
        return readShortLE().toUShort()
    }
    
    /**
     * Reads a medium.
     */
    fun readMedium(): Int {
        return (
            readByte().toInt() shl 16
                or (readByte().toInt() and 255 shl 8)
                or (readByte().toInt() and 255)
            )
    }
    
    /**
     * Reads a medium in little endian.
     */
    fun readMediumLE(): Int {
        return (
            readByte().toInt() and 255
                or (readByte().toInt() and 255 shl 8)
                or (readByte().toInt() shl 16)
            )
    }
    
    /**
     * Reads an unsigned medium.
     */
    fun readUnsignedMedium(): UInt {
        return readMedium().toUInt()
    }
    
    /**
     * Reads an unsigned medium in little endian.
     */
    fun readUnsignedMediumLE(): UInt {
        return readMediumLE().toUInt()
    }
    
    /**
     * Reads an [Int].
     */
    fun readInt(): Int {
        return (
            readByte().toInt() shl 24
                or (readByte().toInt() and 255 shl 16)
                or (readByte().toInt() and 255 shl 8)
                or (readByte().toInt() and 255)
            )
    }
    
    /**
     * Reads an [Int] in little endian.
     */
    fun readIntLE(): Int {
        return (
            readByte().toInt() and 255
                or (readByte().toInt() and 255 shl 8)
                or (readByte().toInt() and 255 shl 16)
                or (readByte().toInt() shl 24)
            )
    }
    
    /**
     * Reads a [UInt].
     */
    fun readUnsignedInt(): UInt {
        return readInt().toUInt()
    }
    
    /**
     * Reads a [UInt] in little endian.
     */
    fun readUnsignedIntLE(): UInt {
        return readIntLE().toUInt()
    }
    
    /**
     * Reads a [Long].
     */
    fun readLong(): Long {
        return (
            readByte().toLong() shl 56
                or (readByte().toLong() and 255 shl 48)
                or (readByte().toLong() and 255 shl 40)
                or (readByte().toLong() and 255 shl 32)
                or (readByte().toLong() and 255 shl 24)
                or (readByte().toLong() and 255 shl 16)
                or (readByte().toLong() and 255 shl 8)
                or (readByte().toLong() and 255)
            )
    }
    
    /**
     * Reads a [Long] in little endian.
     */
    fun readLongLE(): Long {
        return (
            readByte().toLong() and 255
                or (readByte().toLong() and 255 shl 8)
                or (readByte().toLong() and 255 shl 16)
                or (readByte().toLong() and 255 shl 24)
                or (readByte().toLong() and 255 shl 32)
                or (readByte().toLong() and 255 shl 40)
                or (readByte().toLong() and 255 shl 48)
                or (readByte().toLong() shl 56)
            )
    }
    
    /**
     * Reads a [ULong].
     */
    fun readUnsignedLong(): ULong {
        return readLong().toULong()
    }
    
    /**
     * Reads a [ULong] in little endian.
     */
    fun readUnsignedLongLE(): ULong {
        return readLongLE().toULong()
    }
    
    /**
     * Reads a [Char].
     */
    fun readChar(): Char {
        return readShort().toInt().toChar()
    }
    
    /**
     * Reads a [Float].
     */
    fun readFloat(): Float {
        return Float.fromBits(readInt())
    }
    
    /**
     * Reads a [Float] in little endian.
     */
    fun readFloatLE(): Float {
        return Float.fromBits(readIntLE())
    }
    
    /**
     * Reads a [Double].
     */
    fun readDouble(): Double {
        return Double.fromBits(readLong())
    }
    
    /**
     * Reads a [Double] in little endian.
     */
    fun readDoubleLE(): Double {
        return Double.fromBits(readLongLE())
    }
    
    /**
     * Reads an [Int] in a variable length format.
     */
    fun readVarInt(): Int {
        var value = 0
        var currentByte: Byte
        var byteIdx = 0
        
        do {
            currentByte = readByte()
            value = value or ((currentByte.toInt() and 127) shl byteIdx++ * 7)
            check(byteIdx < 6) { "VarInt is too big" }
        } while (currentByte.countLeadingZeroBits() == 0)
        
        return value
    }
    
    /**
     * Reads a [UInt] in a variable length format.
     */
    fun readUnsignedVarInt(): UInt {
        return readVarInt().toUInt()
    }
    
    /**
     * Reads a [Long] in a variable length format.
     */
    fun readVarLong(): Long {
        var value = 0L
        var currentByte: Byte
        var byteIdx = 0
        
        do {
            currentByte = readByte()
            value = value or ((currentByte.toLong() and 127) shl byteIdx++ * 7)
            check(byteIdx < 11) { "VarLong is too big" }
        } while (currentByte.countLeadingZeroBits() == 0)
        
        return value
    }
    
    /**
     * Reads a [ULong] in a variable length format.
     */
    fun readUnsignedVarLong(): ULong {
        return readVarLong().toULong()
    }
    
    /**
     * Reads [length] amount of bytes into the [dst] array at the [dstIndex] offset.
     */
    fun readBytes(dst: ByteArray, dstIndex: Int, length: Int) {
        check(dstIndex + length <= dst.size) { "Destination buffer is too small" }
        
        for (i in 0 until length) {
            dst[dstIndex + i] = readByte()
        }
    }
    
    /**
     * Reads [length] amount of bytes into the [dst] array.
     */
    fun readBytes(dst: ByteArray, length: Int) {
        readBytes(dst, 0, length)
    }
    
    /**
     * Reads bytes into the [dst] array.
     */
    fun readBytes(dst: ByteArray) {
        readBytes(dst, 0, dst.size)
    }
    
    /**
     * Reads [length] amount of bytes into a new [ByteArray].
     */
    fun readBytes(length: Int): ByteArray {
        val bytes = ByteArray(length)
        readBytes(bytes, 0, length)
        return bytes
    }
    
    /**
     * Reads a [String].
     */
    fun readString(): String {
        return String(readBytes(readVarInt()))
    }
    
    /**
     * Reads a [UUID].
     */
    fun readUUID(): UUID {
        return UUID(readLong(), readLong())
    }
    
    companion object {
        
        fun fromByteArray(bytes: ByteArray) = fromStream(ByteArrayInputStream(bytes))
        
        fun fromStream(ins: InputStream) = object : ByteReader {
            
            override fun readByte(): Byte {
                return ins.read().toByte()
            }
            
            override fun skip(length: Int) {
                ins.skip(length.toLong())
            }
            
        }
        
        fun fromDataInput(inp: DataInput) = object : ByteReader {
            
            override fun readByte(): Byte {
                return inp.readByte()
            }
            
            override fun skip(length: Int) {
                inp.skipBytes(length)
            }
            
        }
        
    }
    
}