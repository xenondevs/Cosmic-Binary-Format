package xyz.xenondevs.cbf.buffer

import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.FileChannel
import java.nio.channels.GatheringByteChannel
import java.nio.channels.ScatteringByteChannel
import java.nio.charset.Charset
import java.util.*

interface ByteBuffer {
    
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
     * Returns the string at the given index.
     */
    fun getString(index: Int): String
    
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
     * Sets the string at the given index.
     */
    fun setString(index: Int, value: String)
    
    /**
     * Sets the [UUID] at the given index.
     */
    fun setUUID(index: Int, value: UUID)
    
    /**
     * Reads the boolean at the current reader position.
     */
    fun readBoolean(): Boolean
    
    /**
     * Reads the byte at the current reader position.
     */
    fun readByte(): Byte
    
    /**
     * Reads the unsigned byte at the current reader position.
     */
    fun readUnsignedByte(): UByte
    
    /**
     * Reads the short at the current reader position.
     */
    fun readShort(): Short
    
    /**
     * Reads the short at the current reader position in little endian.
     */
    fun readShortLE(): Short
    
    /**
     * Reads the unsigned short at the current reader position.
     */
    fun readUnsignedShort(): UShort
    
    /**
     * Reads the unsigned short at the current reader position in little endian.
     */
    fun readUnsignedShortLE(): UShort
    
    /**
     * Reads the medium at the current reader position.
     */
    fun readMedium(): Int
    
    /**
     * Reads the medium at the current reader position in little endian.
     */
    fun readMediumLE(): Int
    
    /**
     * Reads the unsigned medium at the current reader position.
     */
    fun readUnsignedMedium(): UInt
    
    /**
     * Reads the unsigned medium at the current reader position in little endian.
     */
    fun readUnsignedMediumLE(): UInt
    
    /**
     * Reads the int at the current reader position.
     */
    fun readInt(): Int
    
    /**
     * Reads the int at the current reader position in little endian.
     */
    fun readIntLE(): Int
    
    /**
     * Reads the unsigned int at the current reader position.
     */
    fun readUnsignedInt(): UInt
    
    /**
     * Reads the unsigned int at the current reader position in little endian.
     */
    fun readUnsignedIntLE(): UInt
    
    /**
     * Reads the long at the current reader position.
     */
    fun readLong(): Long
    
    /**
     * Reads the long at the current reader position in little endian.
     */
    fun readLongLE(): Long
    
    /**
     * Reads the unsigned long at the current reader position.
     */
    fun readUnsignedLong(): ULong
    
    /**
     * Reads the unsigned long at the current reader position in little endian.
     */
    fun readUnsignedLongLE(): ULong
    
    /**
     * Reads the char at the current reader position.
     */
    fun readChar(): Char
    
    /**
     * Reads the float at the current reader position.
     */
    fun readFloat(): Float
    
    /**
     * Reads the float at the current reader position in little endian.
     */
    fun readFloatLE(): Float
    
    /**
     * Reads the double at the current reader position.
     */
    fun readDouble(): Double
    
    /**
     * Reads the double at the current reader position in little endian.
     */
    fun readDoubleLE(): Double
    
    /**
     * Reads the bytes at the current reader position.
     */
    fun readBytes(dst: ByteArray, dstIndex: Int, length: Int)
    
    /**
     * Reads the bytes at the current reader position.
     */
    fun readBytes(dst: ByteArray, length: Int)
    
    /**
     * Reads the bytes at the current reader position.
     */
    fun readBytes(dst: ByteArray)
    
    /**
     * Reads the bytes at the current reader position.
     */
    fun readBytes(length: Int): ByteArray
    
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
     * Reads the string at the current reader position.
     */
    fun readString(): String
    
    /**
     * Reads the [UUID] at the current reader position.
     */
    fun readUUID(): UUID
    
    /**
     * Skips the given amount of bytes.
     */
    fun skip(length: Int)
    
    /**
     * Writes the given boolean at current writer position
     */
    fun writeBoolean(value: Boolean)
    
    /**
     * Writes the given byte at current writer position
     */
    fun writeByte(value: Byte)
    
    /**
     * Writes the given unsigned byte at current writer position
     */
    fun writeUnsignedByte(value: UByte)
    
    /**
     * Writes the given short at current writer position
     */
    fun writeShort(value: Short)
    
    /**
     * Writes the given short at current writer position in little endian
     */
    fun writeShortLE(value: Short)
    
    /**
     * Writes the given unsigned short at current writer position
     */
    fun writeUnsignedShort(value: UShort)
    
    /**
     * Writes the given unsigned short at current writer position in little endian
     */
    fun writeUnsignedShortLE(value: UShort)
    
    /**
     * Writes the given medium at current writer position
     */
    fun writeMedium(value: Int)
    
    /**
     * Writes the given medium at current writer position in little endian
     */
    fun writeMediumLE(value: Int)
    
    /**
     * Writes the given unsigned medium at current writer position
     */
    fun writeUnsignedMedium(value: UInt)
    
    /**
     * Writes the given unsigned medium at current writer position in little endian
     */
    fun writeUnsignedMediumLE(value: UInt)
    
    /**
     * Writes the given int at current writer position
     */
    fun writeInt(value: Int)
    
    /**
     * Writes the given int at current writer position in little endian
     */
    fun writeIntLE(value: Int)
    
    /**
     * Writes the given unsigned int at current writer position
     */
    fun writeUnsignedInt(value: UInt)
    
    /**
     * Writes the given unsigned int at current writer position in little endian
     */
    fun writeUnsignedIntLE(value: UInt)
    
    /**
     * Writes the given long at current writer position
     */
    fun writeLong(value: Long)
    
    /**
     * Writes the given long at current writer position in little endian
     */
    fun writeLongLE(value: Long)
    
    /**
     * Writes the given unsigned long at current writer position
     */
    fun writeUnsignedLong(value: ULong)
    
    /**
     * Writes the given unsigned long at current writer position in little endian
     */
    fun writeUnsignedLongLE(value: ULong)
    
    /**
     * Writes the given char at current writer position
     */
    fun writeChar(value: Char)
    
    /**
     * Writes the given float at current writer position
     */
    fun writeFloat(value: Float)
    
    /**
     * Writes the given float at current writer position in little endian
     */
    fun writeFloatLE(value: Float)
    
    /**
     * Writes the given double at current writer position
     */
    fun writeDouble(value: Double)
    
    /**
     * Writes the given double at current writer position in little endian
     */
    fun writeDoubleLE(value: Double)
    
    /**
     * Writes the given bytes at current writer position
     */
    fun writeBytes(src: ByteArray, srcIndex: Int, length: Int)
    
    /**
     * Writes the given bytes at current writer position
     */
    fun writeBytes(src: ByteArray, length: Int)
    
    /**
     * Writes the given bytes at current writer position
     */
    fun writeBytes(src: ByteArray)
    
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
     * Writes the given string at current writer position
     */
    fun writeString(value: String)
    
    /**
     * Writes the given [UUID] at current writer position
     */
    fun writeUUID(value: UUID)
    
    /**
     * Writes the given amount of nul bytes at current writer position
     */
    fun writeZero(length: Int)
    
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