package xyz.xenondevs.cbf.io

import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.FileChannel
import java.nio.channels.GatheringByteChannel
import java.nio.channels.ScatteringByteChannel
import java.nio.charset.Charset
import java.util.*

interface ByteBuffer : ByteReader, ByteWriter {
    
    /**
     * Returns the number of bytes this buffer can hold.
     */
    fun capacity(): Int
    
    /**
     * Sets the capacity of this buffer to the given value.
     */
    fun capacity(newCapacity: Int)
    
    /**
     * Returns the maximum capacity of this buffer.
     */
    fun maxCapacity(): Int
    
    /**
     * Returns the current reader index of this buffer.
     */
    fun readerIndex(): Int
    
    /**
     * Sets the current reader index of this buffer.
     */
    fun readerIndex(readerIndex: Int)
    
    /**
     * Returns the current writer index of this buffer.
     */
    fun writerIndex(): Int
    
    /**
     * Sets the current writer index of this buffer.
     */
    fun writerIndex(writerIndex: Int)
    
    /**
     * Sets the current reader and writer index of this buffer.
     */
    fun index(readerIndex: Int, writerIndex: Int)
    
    /**
     * Returns the number of readable bytes in this buffer.
     */
    fun readableBytes(): Int
    
    /**
     * Returns the number of writable bytes in this buffer.
     */
    
    fun writableBytes(): Int
    
    /**
     * Returns the maximum number of writable bytes in this buffer.
     */
    fun maxWritableBytes(): Int
    
    /**
     * Returns the number of remaining currently allocated bytes. If this value is surpassed, new bytes will be allocated.
     */
    fun remainingAllocated(): Int
    
    /**
     * Returns true if this buffer is readable.
     */
    
    fun isReadable(): Boolean
    
    /**
     * Returns true if this buffer is readable and has at least the given number of readable bytes.
     */
    fun isReadable(bytes: Int): Boolean
    
    /**
     * Returns true if this buffer is writable.
     */
    fun isWritable(): Boolean
    
    /**
     * Returns true if this buffer is writable and has at least the given number of writable bytes.
     */
    fun isWritable(bytes: Int): Boolean
    
    /**
     * Clears this buffer's contents.
     */
    fun clear()
    
    /**
     * Marks the current reader index which can later be reset via [resetReaderIndex].
     */
    fun markReaderIndex()
    
    /**
     * Marks the current writer index which can later be reset via [resetWriterIndex].
     */
    fun markWriterIndex()
    
    /**
     * Resets the reader index to the marked reader index.
     */
    fun resetReaderIndex()
    
    /**
     * Resets the writer index to the marked writer index.
     */
    fun resetWriterIndex()
    
    /**
     * Discards the readable bytes from index 0 to the current reader index.
     */
    fun discardReadBytes()
    
    /**
     * Ensures that this buffer has at least the given number of writable bytes by allocating more bytes if necessary.
     */
    fun ensureWritable(bytes: Int)
    
    /**
     * Returns the boolean at the given index.
     */
    fun getBoolean(index: Int): Boolean
    
    /**
     * Returns the byte at the given index.
     */
    fun getByte(index: Int): Byte
    
    /**
     * Returns the unsigned byte at the given index.
     */
    fun getUnsignedByte(index: Int): UByte
    
    /**
     * Returns the short at the given index.
     */
    fun getShort(index: Int): Short
    
    /**
     * Returns the short at the given index in little endian.
     */
    fun getShortLE(index: Int): Short
    
    /**
     * Returns the unsigned short at the given index.
     */
    fun getUnsignedShort(index: Int): UShort
    
    /**
     * Returns the unsigned short at the given index in little endian.
     */
    fun getUnsignedShortLE(index: Int): UShort
    
    /**
     * Returns the medium at the given index.
     */
    fun getMedium(index: Int): Int
    
    /**
     * Returns the medium at the given index in little endian.
     */
    fun getMediumLE(index: Int): Int
    
    /**
     * Returns the unsigned medium at the given index.
     */
    fun getUnsignedMedium(index: Int): UInt
    
    /**
     * Returns the unsigned medium at the given index in little endian.
     */
    fun getUnsignedMediumLE(index: Int): UInt
    
    /**
     * Returns the int at the given index.
     */
    fun getInt(index: Int): Int
    
    /**
     * Returns the int at the given index in little endian.
     */
    fun getIntLE(index: Int): Int
    
    /**
     * Returns the unsigned int at the given index.
     */
    fun getUnsignedInt(index: Int): UInt
    
    /**
     * Returns the unsigned int at the given index in little endian.
     */
    fun getUnsignedIntLE(index: Int): UInt
    
    /**
     * Returns the long at the given index.
     */
    fun getLong(index: Int): Long
    
    /**
     * Returns the long at the given index in little endian.
     */
    fun getLongLE(index: Int): Long
    
    /**
     * Returns the unsigned long at the given index.
     */
    fun getUnsignedLong(index: Int): ULong
    
    /**
     * Returns the unsigned long at the given index in little endian.
     */
    fun getUnsignedLongLE(index: Int): ULong
    
    /**
     * Returns the char at the given index.
     */
    fun getChar(index: Int): Char
    
    /**
     * Returns the float at the given index.
     */
    fun getFloat(index: Int): Float
    
    /**
     * Returns the float at the given index in little endian.
     */
    fun getFloatLE(index: Int): Float
    
    /**
     * Returns the double at the given index.
     */
    fun getDouble(index: Int): Double
    
    /**
     * Returns the double at the given index in little endian.
     */
    fun getDoubleLE(index: Int): Double
    
    /**
     * Returns the bytes at the given index.
     */
    fun getBytes(index: Int, dst: ByteArray, dstIndex: Int, length: Int)
    
    /**
     * Returns the bytes at the given index.
     */
    fun getBytes(index: Int, dst: ByteArray, length: Int)
    
    /**
     * Returns the bytes at the given index.
     */
    fun getBytes(index: Int, dst: ByteArray)
    
    /**
     * Returns the bytes at the given index.
     */
    fun getBytes(index: Int, length: Int): ByteArray
    
    /**
     * Returns the bytes at the given index.
     */
    fun getBytes(index: Int, dst: ByteBuffer, dstIndex: Int, length: Int)
    
    /**
     * Returns the bytes at the given index.
     */
    fun getBytes(index: Int, dst: ByteBuffer, length: Int)
    
    /**
     * Returns the bytes at the given index.
     */
    fun getBytes(index: Int, dst: ByteBuffer)
    
    /**
     * Writes the bytes at the given index to the given [OutputStream]
     */
    fun getBytes(index: Int, dst: OutputStream, length: Int)
    
    /**
     * Writes the bytes at the given index to the given [GatheringByteChannel]
     */
    fun getBytes(index: Int, dst: GatheringByteChannel, length: Int)
    
    /**
     * Writes the bytes at the given index to the given [FileChannel] at the given position.
     */
    fun getBytes(index: Int, dst: FileChannel, position: Long, length: Int)
    
    /**
     * Returns the [CharSequence] at the given index.
     */
    fun getCharSequence(index: Int, length: Int, charset: Charset): CharSequence
    
    /**
     * Returns the [UUID] at the given index.
     */
    fun getUUID(index: Int): UUID
    
    /**
     * Sets the boolean at the given index.
     */
    fun setBoolean(index: Int, value: Boolean)
    
    /**
     * Sets the byte at the given index.
     */
    fun setByte(index: Int, value: Byte)
    
    /**
     * Sets the unsigned byte at the given index.
     */
    fun setUnsignedByte(index: Int, value: UByte)
    
    /**
     * Sets the short at the given index.
     */
    fun setShort(index: Int, value: Short)
    
    /**
     * Sets the short at the given index in little endian.
     */
    fun setShortLE(index: Int, value: Short)
    
    /**
     * Sets the unsigned short at the given index.
     */
    fun setUnsignedShort(index: Int, value: UShort)
    
    /**
     * Sets the unsigned short at the given index in little endian.
     */
    fun setUnsignedShortLE(index: Int, value: UShort)
    
    /**
     * Sets the medium at the given index.
     */
    fun setMedium(index: Int, value: Int)
    
    /**
     * Sets the medium at the given index in little endian.
     */
    fun setMediumLE(index: Int, value: Int)
    
    /**
     * Sets the unsigned medium at the given index.
     */
    
    fun setUnsignedMedium(index: Int, value: UInt)
    
    /**
     * Sets the unsigned medium at the given index in little endian.
     */
    fun setUnsignedMediumLE(index: Int, value: UInt)
    
    /**
     * Sets the int at the given index.
     */
    fun setInt(index: Int, value: Int)
    
    /**
     * Sets the int at the given index in little endian.
     */
    fun setIntLE(index: Int, value: Int)
    
    /**
     * Sets the unsigned int at the given index.
     */
    fun setUnsignedInt(index: Int, value: UInt)
    
    /**
     * Sets the unsigned int at the given index in little endian.
     */
    fun setUnsignedIntLE(index: Int, value: UInt)
    
    /**
     * Sets the long at the given index.
     */
    fun setLong(index: Int, value: Long)
    
    /**
     * Sets the long at the given index in little endian.
     */
    fun setLongLE(index: Int, value: Long)
    
    /**
     * Sets the unsigned long at the given index.
     */
    fun setUnsignedLong(index: Int, value: ULong)
    
    /**
     * Sets the unsigned long at the given index in little endian.
     */
    fun setUnsignedLongLE(index: Int, value: ULong)
    
    /**
     * Sets the char at the given index.
     */
    fun setChar(index: Int, value: Char)
    
    /**
     * Sets the float at the given index.
     */
    fun setFloat(index: Int, value: Float)
    
    /**
     * Sets the float at the given index in little endian.
     */
    fun setFloatLE(index: Int, value: Float)
    
    /**
     * Sets the double at the given index.
     */
    fun setDouble(index: Int, value: Double)
    
    /**
     * Sets the double at the given index in little endian.
     */
    fun setDoubleLE(index: Int, value: Double)
    
    /**
     * Sets the bytes at the given index.
     */
    fun setBytes(index: Int, src: ByteArray, srcIndex: Int, length: Int)
    
    /**
     * Sets the bytes at the given index.
     */
    fun setBytes(index: Int, src: ByteArray, length: Int)
    
    /**
     * Sets the bytes at the given index.
     */
    fun setBytes(index: Int, src: ByteArray)
    
    /**
     * Sets the bytes at the given index.
     */
    fun setBytes(index: Int, src: ByteBuffer, srcIndex: Int, length: Int)
    
    /**
     * Sets the bytes at the given index.
     */
    fun setBytes(index: Int, src: ByteBuffer, length: Int)
    
    /**
     * Sets the bytes at the given index.
     */
    fun setBytes(index: Int, src: ByteBuffer)
    
    /**
     * Sets the bytes at the given index.
     */
    fun setBytes(index: Int, input: InputStream, length: Int)
    
    /**
     * Sets the bytes at the given index.
     */
    fun setBytes(index: Int, input: ScatteringByteChannel, length: Int)
    
    /**
     * Sets the bytes at the given index.
     */
    fun setBytes(index: Int, input: FileChannel, position: Long, length: Int)
    
    /**
     * Fills the specified range with ``0x00`` bytes.
     */
    fun setZero(index: Int, length: Int)
    
    /**
     * Sets the char sequence at the given index.
     */
    fun setCharSequence(index: Int, value: CharSequence, charset: Charset)
    
    /**
     * Sets the [UUID] at the given index.
     */
    fun setUUID(index: Int, value: UUID)
    
    /**
     * Reads the bytes at the current reader position.
     */
    fun readBytes(dst: ByteBuffer, dstIndex: Int, length: Int)
    
    /**
     * Reads the bytes at the current reader position.
     */
    fun readBytes(dst: ByteBuffer, length: Int)
    
    /**
     * Reads the bytes at the current reader position.
     */
    fun readBytes(dst: ByteBuffer)
    
    /**
     * Reads the bytes at the current reader position.
     */
    fun readBytes(dst: OutputStream, length: Int)
    
    /**
     * Reads the bytes at the current reader position.
     */
    fun readBytes(dst: GatheringByteChannel, length: Int)
    
    /**
     * Reads the bytes at the current reader position.
     */
    fun readBytes(dst: FileChannel, position: Long, length: Int)
    
    /**
     * Reads the [CharSequence] at the current reader position.
     */
    fun readCharSequence(length: Int, charset: Charset): CharSequence
    
    /**
     * Writes the given bytes at current writer position
     */
    fun writeBytes(src: ByteBuffer, srcIndex: Int, length: Int)
    
    /**
     * Writes the given bytes at current writer position
     */
    fun writeBytes(src: ByteBuffer, length: Int)
    
    /**
     * Writes the given bytes at current writer position
     */
    fun writeBytes(src: ByteBuffer)
    
    /**
     * Writes the given bytes at current writer position
     */
    fun writeBytes(src: InputStream, length: Int)
    
    /**
     * Writes the given bytes at current writer position
     */
    fun writeBytes(src: ScatteringByteChannel, length: Int)
    
    /**
     * Writes the given bytes at current writer position
     */
    fun writeBytes(src: FileChannel, position: Long, length: Int)
    
    /**
     * Writes the given [CharSequence] at current writer position
     */
    fun writeCharSequence(value: CharSequence, charset: Charset)
    
    /**
     * Gets the index of the first occurrence of the given byte.
     */
    fun indexOf(fromIndex: Int, toIndex: Int, value: Byte): Int
    
    /**
     * Gets the number of bytes between the current readerPosition and the first occurrence of the given byte.
     */
    fun bytesBefore(value: Byte): Int
    
    /**
     * Gets the number of bytes between the current readerPosition and the first occurrence of the given byte.
     */
    fun bytesBefore(length: Int, value: Byte): Int
    
    /**
     * Gets the number of bytes between the specified index and the first occurrence of the given byte.
     */
    fun bytesBefore(fromIndex: Int, toIndex: Int, value: Byte): Int
    
    /**
     * Runs an expression for each byte.
     */
    fun forEachByte(action: (Byte) -> Unit)
    
    /**
     * Returns a copy of the current buffer.
     */
    fun copy(): ByteBuffer
    
    /**
     * Returns a copy of the current buffer in the given range.
     */
    fun copy(fromIndex: Int, toIndex: Int): ByteBuffer
    
    /**
     * Returns a slice of this buffer's readable bytes. They both reference the same array.
     */
    fun slice(): ByteBuffer
    
    /**
     * Returns a retained slice of this buffer's readable bytes. They both reference the same array.
     */
    fun retainedSlice(): ByteBuffer
    
    /**
     * Returns a slice of this buffer's readable bytes. They both reference the same array.
     */
    fun slice(index: Int, length: Int): ByteBuffer
    
    /**
     * Returns a retained slice of this buffer's readable bytes. They both reference the same array.
     */
    fun retainedSlice(index: Int, length: Int): ByteBuffer
    
    /**
     * Returns a duplicate of this buffer. They both reference the same array.
     */
    fun duplicate(): ByteBuffer
    
    /**
     * Returns a retained duplicate of this buffer. They both reference the same array.
     */
    fun retainedDuplicate(): ByteBuffer
    
    /**
     * Returns true if this buffer has an array.
     */
    fun hasArray(): Boolean
    
    /**
     * Returns the internal array of this buffer (including unused but allocated bytes).
     */
    fun array(): ByteArray
    
    /**
     * Returns the array offset of this buffer.
     */
    fun arrayOffset(): Int
    
    /**
     * Turn this buffer into a byte array.
     */
    fun toByteArray(): ByteArray
    
    /**
     * hashCode() of this buffer.
     */
    override fun hashCode(): Int
    
}