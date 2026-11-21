package xyz.xenondevs.cbf.io

import xyz.xenondevs.cbf.MAX_PRE_ALLOCATE_COLLECTION_SIZE
import java.io.ByteArrayInputStream
import java.io.DataInput
import java.io.DataInputStream
import java.io.EOFException
import java.io.InputStream
import java.util.*

/**
 * An interface for reading bytes and converting them to various primitive data types.
 *
 * All reads throw an [EOFException] if the input ends before the requested amount of bytes
 * could be read, and an [IllegalArgumentException] if an argument is negative.
 */
interface ByteReader {
    
    /**
     * Reads a [Byte].
     * @throws EOFException if there are no bytes remaining.
     */
    fun readByte(): Byte
    
    /**
     * Reads [length] amount of bytes into the [dst] array at the [dstIndex] offset.
     * @throws EOFException if there are fewer than [length] bytes remaining.
     * @throws IllegalArgumentException if [length] is negative.
     */
    fun readBytes(dst: ByteArray, dstIndex: Int, length: Int)
    
    /**
     * Reads [length] amount of bytes into the [dst] array.
     * @throws EOFException if there are fewer than [length] bytes remaining.
     * @throws IllegalArgumentException if [length] is negative.
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
     *
     * @throws EOFException if there are fewer than [length] bytes remaining.
     * @throws IllegalArgumentException if [length] is negative.
     */
    fun readBytes(length: Int): ByteArray {
        if (length < 0)
            throw IllegalArgumentException("length must not be negative: $length")
        
        val remaining = remaining()
        if (remaining != null) {
            if (length > remaining)
                throw EOFException("Expected $length more bytes, but only $remaining remain")
            val bytes = ByteArray(length)
            readBytes(bytes, 0, length)
            return bytes
        }
        
        return readGrowingArray(length, ::ByteArray, ByteArray::copyOf) { array, offset, chunk ->
            readBytes(array, offset, chunk)
            chunk
        }
    }
    
    /**
     * Skips the given amount of bytes.
     * @throws EOFException if there are fewer than [length] bytes remaining.
     * @throws IllegalArgumentException if [length] is negative.
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
     * Reads a [String] as a varint byte length prefix followed by the UTF-8 encoded bytes.
     * @throws EOFException if there are fewer bytes remaining than the string length prefix declares.
     * @throws IllegalArgumentException if the string length prefix is negative.
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
    
    /**
     * The number of bytes that can be read before the input ends, or null if unknown.
     */
    fun remaining(): Int? = null
    
    /**
     * Creates an [InputStream] that reads from this [ByteReader].
     */
    fun asInputStream(): InputStream =
        ByteReaderToInputStreamWrapper(this)
    
    /**
     * Creates a [DataInput] that reads from this [ByteReader].
     */
    fun asDataInput(): DataInput =
        DataInputStream(asInputStream())
    
    companion object {
        
        /**
         * Creates a [ByteReader] from the given [bytes].
         */
        fun fromByteArray(bytes: ByteArray): ByteReader =
            ByteArrayByteReader(bytes)
        
        /**
         * Creates a [ByteReader] that reads from the given [InputStream].
         */
        fun fromStream(ins: InputStream): ByteReader =
            InputStreamToByteReaderWrapper(ins)
        
        /**
         * Creates a [ByteReader] that reads from the given [DataInput].
         */
        fun fromDataInput(inp: DataInput): ByteReader =
            DataInputToByteReaderWrapper(inp)
        
    }
    
}

/**
 * Checks that at least [count] * [bytesPerElement] bytes are remaining in this reader,
 * throwing an exception if they are not.
 * Does nothing if the number of [remaining][ByteReader.remaining] bytes is unknown.
 *
 * @throws IllegalArgumentException if [count] is negative.
 * @throws EOFException if fewer than [count] * [bytesPerElement] bytes are remaining.
 */
fun ByteReader.requireRemaining(count: Int, bytesPerElement: Int = 1) {
    if (count < 0)
        throw IllegalArgumentException("count must not be negative: $count")
    val remaining = remaining() ?: return
    val required = count.toLong() * bytesPerElement
    if (required > remaining)
        throw EOFException("Expected $required more bytes, but only $remaining remain")
}

/**
 * Reads [size] items into an array that grows with the data as it is read,
 * where [read] reads up to the given number of items and returns the number of items read.
 */
internal inline fun <A> readGrowingArray(
    size: Int,
    create: (capacity: Int) -> A,
    grow: (array: A, capacity: Int) -> A,
    read: (array: A, offset: Int, length: Int) -> Int
): A {
    var capacity = size.coerceAtMost(MAX_PRE_ALLOCATE_COLLECTION_SIZE)
    var array = create(capacity)
    var count = 0
    while (count < size) {
        if (count == capacity) {
            capacity = (capacity.toLong() * 2).coerceAtMost(size.toLong()).toInt()
            array = grow(array, capacity)
        }
        count += read(array, count, capacity - count)
    }
    return array
}

/**
 * Reads a varint size followed by that many elements, each of which occupies at least [minElementSize] bytes.
 * The array is allocated up front if the number of [remaining][ByteReader.remaining] bytes is known,
 * and grows with the data as it is read otherwise.
 *
 * @throws IllegalArgumentException if the size is negative.
 * @throws EOFException if the input ends before all elements could be read.
 */
internal inline fun <A> ByteReader.readSizedArray(
    minElementSize: Int,
    create: (size: Int) -> A,
    grow: (array: A, capacity: Int) -> A,
    readInto: (array: A, index: Int) -> Unit
): A {
    val size = readVarInt()
    requireRemaining(size, minElementSize)
    if (remaining() == null) {
        return readGrowingArray(size, create, grow) { array, index, _ ->
            readInto(array, index)
            1
        }
    }
    
    val array = create(size)
    for (index in 0..<size)
        readInto(array, index)
    return array
}

/**
 * A [ByteReader] that reads from a [ByteArray], tracking the exact number of remaining bytes.
 */
private class ByteArrayByteReader(private val bytes: ByteArray, private var index: Int = 0) : ByteReader {
    
    override fun readByte(): Byte {
        if (index >= bytes.size)
            throw EOFException("End of input")
        return bytes[index++]
    }
    
    override fun readBytes(dst: ByteArray, dstIndex: Int, length: Int) {
        if (length < 0)
            throw IllegalArgumentException("length must not be negative: $length")
        val remaining = bytes.size - index
        if (length > remaining)
            throw EOFException("Expected $length more bytes, but only $remaining remain")
        bytes.copyInto(dst, dstIndex, index, index + length)
        index += length
    }
    
    override fun skip(length: Int) {
        if (length < 0)
            throw IllegalArgumentException("length must not be negative: $length")
        val remaining = bytes.size - index
        if (length > remaining)
            throw EOFException("Expected $length more bytes, but only $remaining remain")
        index += length
    }
    
    override fun remaining(): Int = bytes.size - index
    
}

private class InputStreamToByteReaderWrapper(private val inp: InputStream) : ByteReader {
    
    override fun readByte(): Byte {
        val byte = inp.read()
        if (byte == -1)
            throw EOFException("End of input")
        return byte.toByte()
    }
    
    override fun readBytes(dst: ByteArray, dstIndex: Int, length: Int) {
        if (length < 0)
            throw IllegalArgumentException("length must not be negative: $length")
        
        var offset = dstIndex
        var remaining = length
        while (remaining > 0) {
            val count = inp.read(dst, offset, remaining)
            if (count == -1)
                throw EOFException("Expected $length more bytes, but the input ended after ${length - remaining}")
            offset += count
            remaining -= count
        }
    }
    
    override fun skip(length: Int) {
        if (length < 0)
            throw IllegalArgumentException("length must not be negative: $length")
        
        var remaining = length
        while (remaining > 0) {
            val skipped = inp.skip(remaining.toLong())
            if (skipped > 0) {
                remaining -= skipped.toInt()
            } else {
                // skip() made no progress; check whether the input has ended
                if (inp.read() == -1)
                    throw EOFException("Expected $length more bytes, but the input ended after ${length - remaining}")
                remaining--
            }
        }
    }
    
    // ByteArrayInputStream is the only standard input stream whose available() method
    // reports the exact number of remaining bytes
    override fun remaining(): Int? =
        if (inp is ByteArrayInputStream) inp.available() else null
    
    override fun asInputStream(): InputStream {
        return inp
    }
    
}

private class DataInputToByteReaderWrapper(private val inp: DataInput) : ByteReader {
    
    override fun readByte(): Byte {
        return inp.readByte()
    }
    
    override fun readBytes(dst: ByteArray, dstIndex: Int, length: Int) {
        if (length < 0)
            throw IllegalArgumentException("length must not be negative: $length")
        inp.readFully(dst, dstIndex, length)
    }
    
    override fun skip(length: Int) {
        if (length < 0)
            throw IllegalArgumentException("length must not be negative: $length")
        
        var remaining = length
        while (remaining > 0) {
            val skipped = inp.skipBytes(remaining)
            if (skipped > 0) {
                remaining -= skipped
            } else {
                // skipBytes() made no progress, which does not necessarily mean that the input has ended
                inp.readByte() // throws EOFException if the input has ended
                remaining--
            }
        }
    }
    
    override fun asDataInput(): DataInput {
        return inp
    }
    
}

private class ByteReaderToInputStreamWrapper(private val reader: ByteReader) : InputStream() {
    
    override fun read(): Int {
        return try {
            reader.readByte().toInt() and 0xFF
        } catch (_: EOFException) {
            -1
        }
    }
    
    override fun available(): Int {
        return reader.remaining() ?: 0
    }
    
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (off < 0 || len < 0 || len > b.size - off)
            throw IndexOutOfBoundsException("off=$off, len=$len, array size=${b.size}")
        if (len == 0)
            return 0
        
        val remaining = reader.remaining()
        if (remaining != null) {
            if (remaining == 0)
                return -1
            val count = minOf(len, remaining)
            reader.readBytes(b, off, count)
            return count
        }
        
        var count = 0
        try {
            while (count < len) {
                b[off + count] = reader.readByte()
                count++
            }
        } catch (_: EOFException) {
        }
        return if (count == 0) -1 else count
    }
    
    override fun readNBytes(len: Int): ByteArray {
        if (len < 0)
            throw IllegalArgumentException("len must not be negative: $len")
        
        val remaining = reader.remaining()
        if (remaining != null)
            return reader.readBytes(minOf(len, remaining))
        
        var bytes = ByteArray(len.coerceAtMost(MAX_PRE_ALLOCATE_COLLECTION_SIZE))
        var count = 0
        try {
            while (count < len) {
                if (count == bytes.size)
                    bytes = bytes.copyOf((bytes.size.toLong() * 2).coerceAtMost(len.toLong()).toInt())
                bytes[count++] = reader.readByte()
            }
        } catch (_: EOFException) {
        }
        return if (count == bytes.size) bytes else bytes.copyOf(count)
    }
    
    override fun skip(n: Long): Long {
        if (n <= 0)
            return 0
        val length = n.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        
        val remaining = reader.remaining()
        if (remaining != null) {
            val count = minOf(length, remaining)
            reader.skip(count)
            return count.toLong()
        }
        
        var count = 0L
        try {
            while (count < length) {
                reader.readByte()
                count++
            }
        } catch (_: EOFException) {
        }
        return count
    }
    
}
