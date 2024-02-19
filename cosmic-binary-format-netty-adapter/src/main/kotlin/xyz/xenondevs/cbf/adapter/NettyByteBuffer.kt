package xyz.xenondevs.cbf.adapter

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import xyz.xenondevs.cbf.io.ByteBuffer
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.FileChannel
import java.nio.channels.GatheringByteChannel
import java.nio.channels.ScatteringByteChannel
import java.nio.charset.Charset
import java.util.*

/**
 * TODO: Does Netty change a bytebufs indices when reading/writing via getBytes/setBytes?
 */
class NettyByteBuffer(val nettyBuf: ByteBuf) : ByteBuffer {
    
    constructor(allocator: ByteBufAllocator) : this(allocator.buffer())
    
    /**
     * Returns the number of bytes this buffer can hold.
     */
    override fun capacity(): Int {
        return nettyBuf.capacity()
    }
    
    /**
     * Sets the capacity of this buffer to the given value.
     */
    override fun capacity(newCapacity: Int) {
        nettyBuf.capacity(newCapacity)
    }
    
    /**
     * Returns the maximum capacity of this buffer.
     */
    override fun maxCapacity(): Int {
        return nettyBuf.maxCapacity()
    }
    
    /**
     * Returns the current reader index of this buffer.
     */
    override fun readerIndex(): Int {
        return nettyBuf.readerIndex()
    }
    
    /**
     * Sets the current reader index of this buffer.
     */
    override fun readerIndex(readerIndex: Int) {
        nettyBuf.readerIndex(readerIndex)
    }
    
    /**
     * Returns the current writer index of this buffer.
     */
    override fun writerIndex(): Int {
        return nettyBuf.writerIndex()
    }
    
    /**
     * Sets the current writer index of this buffer.
     */
    override fun writerIndex(writerIndex: Int) {
        nettyBuf.writerIndex(writerIndex)
    }
    
    /**
     * Sets the current reader and writer index of this buffer.
     */
    override fun index(readerIndex: Int, writerIndex: Int) {
        nettyBuf.setIndex(readerIndex, writerIndex)
    }
    
    /**
     * Returns the number of readable bytes in this buffer.
     */
    override fun readableBytes(): Int {
        return nettyBuf.readableBytes()
    }
    
    /**
     * Returns the number of writable bytes in this buffer.
     */
    override fun writableBytes(): Int {
        return nettyBuf.writableBytes()
    }
    
    /**
     * Returns the maximum number of writable bytes in this buffer.
     */
    override fun maxWritableBytes(): Int {
        return nettyBuf.maxWritableBytes()
    }
    
    /**
     * Returns the number of remaining currently allocated bytes. If this value is surpassed, new bytes will be allocated.
     */
    override fun remainingAllocated(): Int {
        return nettyBuf.capacity() - nettyBuf.writableBytes()
    }
    
    /**
     * Returns true if this buffer is readable.
     */
    override fun isReadable(): Boolean {
        return nettyBuf.isReadable
    }
    
    /**
     * Returns true if this buffer is readable and has at least the given number of readable bytes.
     */
    override fun isReadable(bytes: Int): Boolean {
        return nettyBuf.isReadable(bytes)
    }
    
    /**
     * Returns true if this buffer is writable.
     */
    override fun isWritable(): Boolean {
        return nettyBuf.isWritable
    }
    
    /**
     * Returns true if this buffer is writable and has at least the given number of writable bytes.
     */
    override fun isWritable(bytes: Int): Boolean {
        return nettyBuf.isWritable(bytes)
    }
    
    /**
     * Clears this buffer's contents.
     */
    override fun clear() {
        nettyBuf.clear()
    }
    
    /**
     * Marks the current reader index which can later be reset via [resetReaderIndex].
     */
    override fun markReaderIndex() {
        nettyBuf.markReaderIndex()
    }
    
    /**
     * Marks the current writer index which can later be reset via [resetWriterIndex].
     */
    override fun markWriterIndex() {
        nettyBuf.markWriterIndex()
    }
    
    /**
     * Resets the reader index to the marked reader index.
     */
    override fun resetReaderIndex() {
        nettyBuf.resetReaderIndex()
    }
    
    /**
     * Resets the writer index to the marked writer index.
     */
    override fun resetWriterIndex() {
        nettyBuf.resetWriterIndex()
    }
    
    /**
     * Discards the readable bytes from index 0 to the current reader index.
     */
    override fun discardReadBytes() {
        nettyBuf.discardReadBytes()
    }
    
    /**
     * Ensures that this buffer has at least the given number of writable bytes by allocating more bytes if necessary.
     */
    override fun ensureWritable(bytes: Int) {
        nettyBuf.ensureWritable(bytes)
    }
    
    /**
     * Returns the boolean at the given index.
     */
    override fun getBoolean(index: Int): Boolean {
        return nettyBuf.getBoolean(index)
    }
    
    /**
     * Returns the byte at the given index.
     */
    override fun getByte(index: Int): Byte {
        return nettyBuf.getByte(index)
    }
    
    /**
     * Returns the unsigned byte at the given index.
     */
    override fun getUnsignedByte(index: Int): UByte {
        return nettyBuf.getUnsignedByte(index).toUByte()
    }
    
    /**
     * Returns the short at the given index.
     */
    override fun getShort(index: Int): Short {
        return nettyBuf.getShort(index)
    }
    
    /**
     * Returns the short at the given index in little endian.
     */
    override fun getShortLE(index: Int): Short {
        return nettyBuf.getShortLE(index)
    }
    
    /**
     * Returns the unsigned short at the given index.
     */
    override fun getUnsignedShort(index: Int): UShort {
        return nettyBuf.getUnsignedShort(index).toUShort()
    }
    
    /**
     * Returns the unsigned short at the given index in little endian.
     */
    override fun getUnsignedShortLE(index: Int): UShort {
        return nettyBuf.getUnsignedShortLE(index).toUShort()
    }
    
    /**
     * Returns the medium at the given index.
     */
    override fun getMedium(index: Int): Int {
        return nettyBuf.getMedium(index)
    }
    
    /**
     * Returns the medium at the given index in little endian.
     */
    override fun getMediumLE(index: Int): Int {
        return nettyBuf.getMediumLE(index)
    }
    
    /**
     * Returns the unsigned medium at the given index.
     */
    override fun getUnsignedMedium(index: Int): UInt {
        return nettyBuf.getUnsignedMedium(index).toUInt()
    }
    
    /**
     * Returns the unsigned medium at the given index in little endian.
     */
    override fun getUnsignedMediumLE(index: Int): UInt {
        return nettyBuf.getUnsignedMediumLE(index).toUInt()
    }
    
    /**
     * Returns the int at the given index.
     */
    override fun getInt(index: Int): Int {
        return nettyBuf.getInt(index)
    }
    
    /**
     * Returns the int at the given index in little endian.
     */
    override fun getIntLE(index: Int): Int {
        return nettyBuf.getIntLE(index)
    }
    
    /**
     * Returns the unsigned int at the given index.
     */
    override fun getUnsignedInt(index: Int): UInt {
        return nettyBuf.getUnsignedInt(index).toUInt()
    }
    
    /**
     * Returns the unsigned int at the given index in little endian.
     */
    override fun getUnsignedIntLE(index: Int): UInt {
        return nettyBuf.getUnsignedIntLE(index).toUInt()
    }
    
    /**
     * Returns the long at the given index.
     */
    override fun getLong(index: Int): Long {
        return nettyBuf.getLong(index)
    }
    
    /**
     * Returns the long at the given index in little endian.
     */
    override fun getLongLE(index: Int): Long {
        return nettyBuf.getLongLE(index)
    }
    
    /**
     * Returns the unsigned long at the given index.
     */
    override fun getUnsignedLong(index: Int): ULong {
        return nettyBuf.getLong(index).toULong()
    }
    
    /**
     * Returns the unsigned long at the given index in little endian.
     */
    override fun getUnsignedLongLE(index: Int): ULong {
        return nettyBuf.getLongLE(index).toULong()
    }
    
    /**
     * Returns the char at the given index.
     */
    override fun getChar(index: Int): Char {
        return nettyBuf.getChar(index)
    }
    
    /**
     * Returns the float at the given index.
     */
    override fun getFloat(index: Int): Float {
        return nettyBuf.getFloat(index)
    }
    
    /**
     * Returns the float at the given index in little endian.
     */
    override fun getFloatLE(index: Int): Float {
        return nettyBuf.getFloatLE(index)
    }
    
    /**
     * Returns the double at the given index.
     */
    override fun getDouble(index: Int): Double {
        return nettyBuf.getDouble(index)
    }
    
    /**
     * Returns the double at the given index in little endian.
     */
    override fun getDoubleLE(index: Int): Double {
        return nettyBuf.getDoubleLE(index)
    }
    
    /**
     * Returns the bytes at the given index.
     */
    override fun getBytes(index: Int, dst: ByteArray, dstIndex: Int, length: Int) {
        nettyBuf.getBytes(index, dst, dstIndex, length)
    }
    
    /**
     * Returns the bytes at the given index.
     */
    override fun getBytes(index: Int, dst: ByteArray, length: Int) {
        nettyBuf.getBytes(index, dst, 0, length)
    }
    
    /**
     * Returns the bytes at the given index.
     */
    override fun getBytes(index: Int, dst: ByteArray) {
        nettyBuf.getBytes(index, dst)
    }
    
    /**
     * Returns the bytes at the given index.
     */
    override fun getBytes(index: Int, length: Int): ByteArray {
        return ByteArray(length).apply { nettyBuf.getBytes(index, this) }
    }
    
    /**
     * Returns the bytes at the given index.
     */
    override fun getBytes(index: Int, dst: ByteBuffer, dstIndex: Int, length: Int) {
        val bytes = getBytes(index, length)
        dst.setBytes(dstIndex, bytes)
    }
    
    /**
     * Returns the bytes at the given index.
     */
    override fun getBytes(index: Int, dst: ByteBuffer, length: Int) {
        val bytes = getBytes(index, length)
        dst.writeBytes(bytes)
    }
    
    /**
     * Returns the bytes at the given index.
     */
    override fun getBytes(index: Int, dst: ByteBuffer) {
        val bytes = getBytes(index, dst.remainingAllocated())
        dst.writeBytes(bytes)
    }
    
    /**
     * Writes the bytes at the given index to the given [OutputStream]
     */
    override fun getBytes(index: Int, dst: OutputStream, length: Int) {
        nettyBuf.getBytes(index, dst, length)
    }
    
    /**
     * Writes the bytes at the given index to the given [GatheringByteChannel]
     */
    override fun getBytes(index: Int, dst: GatheringByteChannel, length: Int) {
        nettyBuf.getBytes(index, dst, length)
    }
    
    /**
     * Writes the bytes at the given index to the given [FileChannel] at the given position.
     */
    override fun getBytes(index: Int, dst: FileChannel, position: Long, length: Int) {
        nettyBuf.getBytes(index, dst, position, length)
    }
    
    /**
     * Returns the [CharSequence] at the given index.
     */
    override fun getCharSequence(index: Int, length: Int, charset: Charset): CharSequence {
        return nettyBuf.getCharSequence(index, length, charset)
    }
    
    /**
     * Returns the [UUID] at the given index.
     */
    override fun getUUID(index: Int): UUID {
        return UUID(getLong(index), getLong(index + 8))
    }
    
    /**
     * Sets the boolean at the given index.
     */
    override fun setBoolean(index: Int, value: Boolean) {
        nettyBuf.setBoolean(index, value)
    }
    
    /**
     * Sets the byte at the given index.
     */
    override fun setByte(index: Int, value: Byte) {
        nettyBuf.setByte(index, value.toInt())
    }
    
    /**
     * Sets the unsigned byte at the given index.
     */
    override fun setUnsignedByte(index: Int, value: UByte) {
        nettyBuf.setByte(index, value.toInt())
    }
    
    /**
     * Sets the short at the given index.
     */
    override fun setShort(index: Int, value: Short) {
        nettyBuf.setShort(index, value.toInt())
    }
    
    /**
     * Sets the short at the given index in little endian.
     */
    override fun setShortLE(index: Int, value: Short) {
        nettyBuf.setShortLE(index, value.toInt())
    }
    
    /**
     * Sets the unsigned short at the given index.
     */
    override fun setUnsignedShort(index: Int, value: UShort) {
        nettyBuf.setShort(index, value.toInt())
    }
    
    /**
     * Sets the unsigned short at the given index in little endian.
     */
    override fun setUnsignedShortLE(index: Int, value: UShort) {
        nettyBuf.setShortLE(index, value.toInt())
    }
    
    /**
     * Sets the medium at the given index.
     */
    override fun setMedium(index: Int, value: Int) {
        nettyBuf.setMedium(index, value)
    }
    
    /**
     * Sets the medium at the given index in little endian.
     */
    override fun setMediumLE(index: Int, value: Int) {
        nettyBuf.setMediumLE(index, value)
    }
    
    /**
     * Sets the unsigned medium at the given index.
     */
    override fun setUnsignedMedium(index: Int, value: UInt) {
        nettyBuf.setMedium(index, value.toInt())
    }
    
    /**
     * Sets the unsigned medium at the given index in little endian.
     */
    override fun setUnsignedMediumLE(index: Int, value: UInt) {
        nettyBuf.setMediumLE(index, value.toInt())
    }
    
    /**
     * Sets the int at the given index.
     */
    override fun setInt(index: Int, value: Int) {
        nettyBuf.setInt(index, value)
    }
    
    /**
     * Sets the int at the given index in little endian.
     */
    override fun setIntLE(index: Int, value: Int) {
        nettyBuf.setIntLE(index, value)
    }
    
    /**
     * Sets the unsigned int at the given index.
     */
    override fun setUnsignedInt(index: Int, value: UInt) {
        nettyBuf.setInt(index, value.toInt())
    }
    
    /**
     * Sets the unsigned int at the given index in little endian.
     */
    override fun setUnsignedIntLE(index: Int, value: UInt) {
        nettyBuf.setIntLE(index, value.toInt())
    }
    
    /**
     * Sets the long at the given index.
     */
    override fun setLong(index: Int, value: Long) {
        nettyBuf.setLong(index, value)
    }
    
    /**
     * Sets the long at the given index in little endian.
     */
    override fun setLongLE(index: Int, value: Long) {
        nettyBuf.setLongLE(index, value)
    }
    
    /**
     * Sets the unsigned long at the given index.
     */
    override fun setUnsignedLong(index: Int, value: ULong) {
        nettyBuf.setLong(index, value.toLong())
    }
    
    /**
     * Sets the unsigned long at the given index in little endian.
     */
    override fun setUnsignedLongLE(index: Int, value: ULong) {
        nettyBuf.setLongLE(index, value.toLong())
    }
    
    /**
     * Sets the char at the given index.
     */
    override fun setChar(index: Int, value: Char) {
        nettyBuf.setChar(index, value.code)
    }
    
    /**
     * Sets the float at the given index.
     */
    override fun setFloat(index: Int, value: Float) {
        nettyBuf.setFloat(index, value)
    }
    
    /**
     * Sets the float at the given index in little endian.
     */
    override fun setFloatLE(index: Int, value: Float) {
        nettyBuf.setFloatLE(index, value)
    }
    
    /**
     * Sets the double at the given index.
     */
    override fun setDouble(index: Int, value: Double) {
        nettyBuf.setDouble(index, value)
    }
    
    /**
     * Sets the double at the given index in little endian.
     */
    override fun setDoubleLE(index: Int, value: Double) {
        nettyBuf.setDoubleLE(index, value)
    }
    
    /**
     * Sets the bytes at the given index.
     */
    override fun setBytes(index: Int, src: ByteArray, srcIndex: Int, length: Int) {
        nettyBuf.setBytes(index, src, srcIndex, length)
    }
    
    /**
     * Sets the bytes at the given index.
     */
    override fun setBytes(index: Int, src: ByteArray, length: Int) {
        nettyBuf.setBytes(index, src, 0, length)
    }
    
    /**
     * Sets the bytes at the given index.
     */
    override fun setBytes(index: Int, src: ByteArray) {
        nettyBuf.setBytes(index, src)
    }
    
    /**
     * Sets the bytes at the given index.
     */
    override fun setBytes(index: Int, src: ByteBuffer, srcIndex: Int, length: Int) {
        nettyBuf.setBytes(index, src.getBytes(srcIndex, length))
    }
    
    /**
     * Sets the bytes at the given index.
     */
    override fun setBytes(index: Int, src: ByteBuffer, length: Int) {
        nettyBuf.setBytes(index, src.getBytes(src.readerIndex(), length))
    }
    
    /**
     * Sets the bytes at the given index.
     */
    override fun setBytes(index: Int, src: ByteBuffer) {
        nettyBuf.setBytes(index, src.getBytes(0, src.readableBytes() + src.readerIndex()))
    }
    
    /**
     * Sets the bytes at the given index.
     */
    override fun setBytes(index: Int, input: InputStream, length: Int) {
        nettyBuf.setBytes(index, input, length)
    }
    
    /**
     * Sets the bytes at the given index.
     */
    override fun setBytes(index: Int, input: ScatteringByteChannel, length: Int) {
        nettyBuf.setBytes(index, input, length)
    }
    
    /**
     * Sets the bytes at the given index.
     */
    override fun setBytes(index: Int, input: FileChannel, position: Long, length: Int) {
        nettyBuf.setBytes(index, input, position, length)
    }
    
    /**
     * Fills the specified range with ``0x00`` bytes.
     */
    override fun setZero(index: Int, length: Int) {
        nettyBuf.setZero(index, length)
    }
    
    /**
     * Sets the char sequence at the given index.
     */
    override fun setCharSequence(index: Int, value: CharSequence, charset: Charset) {
        nettyBuf.setCharSequence(index, value, charset)
    }
    
    /**
     * Sets the [UUID] at the given index.
     */
    override fun setUUID(index: Int, value: UUID) {
        setLong(index, value.mostSignificantBits)
        setLong(index + 8, value.leastSignificantBits)
    }
    
    /**
     * Reads the boolean at the current reader position.
     */
    override fun readBoolean(): Boolean {
        return nettyBuf.readBoolean()
    }
    
    /**
     * Reads the byte at the current reader position.
     */
    override fun readByte(): Byte {
        return nettyBuf.readByte()
    }
    
    /**
     * Reads the unsigned byte at the current reader position.
     */
    override fun readUnsignedByte(): UByte {
        return nettyBuf.readUnsignedByte().toUByte()
    }
    
    /**
     * Reads the short at the current reader position.
     */
    override fun readShort(): Short {
        return nettyBuf.readShort()
    }
    
    /**
     * Reads the short at the current reader position in little endian.
     */
    override fun readShortLE(): Short {
        return nettyBuf.readShortLE()
    }
    
    /**
     * Reads the unsigned short at the current reader position.
     */
    override fun readUnsignedShort(): UShort {
        return nettyBuf.readUnsignedShort().toUShort()
    }
    
    /**
     * Reads the unsigned short at the current reader position in little endian.
     */
    override fun readUnsignedShortLE(): UShort {
        return nettyBuf.readUnsignedShortLE().toUShort()
    }
    
    /**
     * Reads the medium at the current reader position.
     */
    override fun readMedium(): Int {
        return nettyBuf.readMedium()
    }
    
    /**
     * Reads the medium at the current reader position in little endian.
     */
    override fun readMediumLE(): Int {
        return nettyBuf.readMediumLE()
    }
    
    /**
     * Reads the unsigned medium at the current reader position.
     */
    override fun readUnsignedMedium(): UInt {
        return nettyBuf.readUnsignedMedium().toUInt()
    }
    
    /**
     * Reads the unsigned medium at the current reader position in little endian.
     */
    override fun readUnsignedMediumLE(): UInt {
        return nettyBuf.readUnsignedMediumLE().toUInt()
    }
    
    /**
     * Reads the int at the current reader position.
     */
    override fun readInt(): Int {
        return nettyBuf.readInt()
    }
    
    /**
     * Reads the int at the current reader position in little endian.
     */
    override fun readIntLE(): Int {
        return nettyBuf.readIntLE()
    }
    
    /**
     * Reads the unsigned int at the current reader position.
     */
    override fun readUnsignedInt(): UInt {
        return nettyBuf.readUnsignedInt().toUInt()
    }
    
    /**
     * Reads the unsigned int at the current reader position in little endian.
     */
    override fun readUnsignedIntLE(): UInt {
        return nettyBuf.readUnsignedIntLE().toUInt()
    }
    
    /**
     * Reads the long at the current reader position.
     */
    override fun readLong(): Long {
        return nettyBuf.readLong()
    }
    
    /**
     * Reads the long at the current reader position in little endian.
     */
    override fun readLongLE(): Long {
        return nettyBuf.readLongLE()
    }
    
    /**
     * Reads the unsigned long at the current reader position.
     */
    override fun readUnsignedLong(): ULong {
        return nettyBuf.readLong().toULong()
    }
    
    /**
     * Reads the unsigned long at the current reader position in little endian.
     */
    override fun readUnsignedLongLE(): ULong {
        return nettyBuf.readLongLE().toULong()
    }
    
    /**
     * Reads the char at the current reader position.
     */
    override fun readChar(): Char {
        return nettyBuf.readChar()
    }
    
    /**
     * Reads the float at the current reader position.
     */
    override fun readFloat(): Float {
        return nettyBuf.readFloat()
    }
    
    /**
     * Reads the float at the current reader position in little endian.
     */
    override fun readFloatLE(): Float {
        return nettyBuf.readFloatLE()
    }
    
    /**
     * Reads the double at the current reader position.
     */
    override fun readDouble(): Double {
        return nettyBuf.readDouble()
    }
    
    /**
     * Reads the double at the current reader position in little endian.
     */
    override fun readDoubleLE(): Double {
        return nettyBuf.readDoubleLE()
    }
    
    /**
     * Reads the int in a variable length format at current writer position.
     */
    override fun readVarInt(): Int {
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
     * Reads the  long in a variable length format at current writer position.
     */
    override fun readVarLong(): Long {
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
     * Reads the bytes at the current reader position.
     */
    override fun readBytes(dst: ByteArray, dstIndex: Int, length: Int) {
        nettyBuf.readBytes(dst, dstIndex, length)
    }
    
    /**
     * Reads the bytes at the current reader position.
     */
    override fun readBytes(dst: ByteArray, length: Int) {
        nettyBuf.readBytes(dst, 0, length)
    }
    
    /**
     * Reads the bytes at the current reader position.
     */
    override fun readBytes(dst: ByteArray) {
        nettyBuf.readBytes(dst)
    }
    
    /**
     * Reads the bytes at the current reader position.
     */
    override fun readBytes(length: Int): ByteArray {
        return ByteArray(length).apply { nettyBuf.readBytes(this) }
    }
    
    /**
     * Reads the bytes at the current reader position.
     */
    override fun readBytes(dst: ByteBuffer, dstIndex: Int, length: Int) {
        val bytes = readBytes(length)
        dst.setBytes(dstIndex, bytes)
    }
    
    /**
     * Reads the bytes at the current reader position.
     */
    override fun readBytes(dst: ByteBuffer, length: Int) {
        val bytes = readBytes(length)
        dst.setBytes(0, bytes)
    }
    
    /**
     * Reads the bytes at the current reader position.
     */
    override fun readBytes(dst: ByteBuffer) {
        val bytes = readBytes(dst.remainingAllocated())
        dst.setBytes(0, bytes)
    }
    
    /**
     * Reads the bytes at the current reader position.
     */
    override fun readBytes(dst: OutputStream, length: Int) {
        nettyBuf.readBytes(dst, length)
    }
    
    /**
     * Reads the bytes at the current reader position.
     */
    override fun readBytes(dst: GatheringByteChannel, length: Int) {
        nettyBuf.readBytes(dst, length)
    }
    
    /**
     * Reads the bytes at the current reader position.
     */
    override fun readBytes(dst: FileChannel, position: Long, length: Int) {
        nettyBuf.readBytes(dst, position, length)
    }
    
    /**
     * Reads the [CharSequence] at the current reader position.
     */
    override fun readCharSequence(length: Int, charset: Charset): CharSequence {
        return nettyBuf.readCharSequence(length, charset)
    }
    
    /**
     * Reads the string at the current reader position.
     */
    override fun readString(): String {
        return String(readBytes(readVarInt()))
    }
    
    /**
     * Reads the [UUID] at the current reader position.
     */
    override fun readUUID(): UUID {
        return UUID(readLong(), readLong())
    }
    
    /**
     * Skips the given amount of bytes.
     */
    override fun skip(length: Int) {
        nettyBuf.skipBytes(length)
    }
    
    /**
     * Writes the given boolean at current writer position
     */
    override fun writeBoolean(value: Boolean) {
        nettyBuf.writeBoolean(value)
    }
    
    /**
     * Writes the given byte at current writer position
     */
    override fun writeByte(value: Byte) {
        nettyBuf.writeByte(value.toInt())
    }
    
    /**
     * Writes the given unsigned byte at current writer position
     */
    override fun writeUnsignedByte(value: UByte) {
        nettyBuf.writeByte(value.toInt())
    }
    
    /**
     * Writes the given short at current writer position
     */
    override fun writeShort(value: Short) {
        nettyBuf.writeShort(value.toInt())
    }
    
    /**
     * Writes the given short at current writer position in little endian
     */
    override fun writeShortLE(value: Short) {
        nettyBuf.writeShortLE(value.toInt())
    }
    
    /**
     * Writes the given unsigned short at current writer position
     */
    override fun writeUnsignedShort(value: UShort) {
        nettyBuf.writeShort(value.toInt())
    }
    
    /**
     * Writes the given unsigned short at current writer position in little endian
     */
    override fun writeUnsignedShortLE(value: UShort) {
        nettyBuf.writeShortLE(value.toInt())
    }
    
    /**
     * Writes the given medium at current writer position
     */
    override fun writeMedium(value: Int) {
        nettyBuf.writeMedium(value)
    }
    
    /**
     * Writes the given medium at current writer position in little endian
     */
    override fun writeMediumLE(value: Int) {
        nettyBuf.writeMediumLE(value)
    }
    
    /**
     * Writes the given unsigned medium at current writer position
     */
    override fun writeUnsignedMedium(value: UInt) {
        nettyBuf.writeMedium(value.toInt())
    }
    
    /**
     * Writes the given unsigned medium at current writer position in little endian
     */
    override fun writeUnsignedMediumLE(value: UInt) {
        nettyBuf.writeMediumLE(value.toInt())
    }
    
    /**
     * Writes the given int at current writer position
     */
    override fun writeInt(value: Int) {
        nettyBuf.writeInt(value)
    }
    
    /**
     * Writes the given int at current writer position in little endian
     */
    override fun writeIntLE(value: Int) {
        nettyBuf.writeIntLE(value)
    }
    
    /**
     * Writes the given unsigned int at current writer position
     */
    override fun writeUnsignedInt(value: UInt) {
        nettyBuf.writeInt(value.toInt())
    }
    
    /**
     * Writes the given unsigned int at current writer position in little endian
     */
    override fun writeUnsignedIntLE(value: UInt) {
        nettyBuf.writeIntLE(value.toInt())
    }
    
    /**
     * Writes the given long at current writer position
     */
    override fun writeLong(value: Long) {
        nettyBuf.writeLong(value)
    }
    
    /**
     * Writes the given long at current writer position in little endian
     */
    override fun writeLongLE(value: Long) {
        nettyBuf.writeLongLE(value)
    }
    
    /**
     * Writes the given unsigned long at current writer position
     */
    override fun writeUnsignedLong(value: ULong) {
        nettyBuf.writeLong(value.toLong())
    }
    
    /**
     * Writes the given unsigned long at current writer position in little endian
     */
    override fun writeUnsignedLongLE(value: ULong) {
        nettyBuf.writeLongLE(value.toLong())
    }
    
    /**
     * Writes the given char at current writer position
     */
    override fun writeChar(value: Char) {
        nettyBuf.writeChar(value.code)
    }
    
    /**
     * Writes the given float at current writer position
     */
    override fun writeFloat(value: Float) {
        nettyBuf.writeFloat(value)
    }
    
    /**
     * Writes the given float at current writer position in little endian
     */
    override fun writeFloatLE(value: Float) {
        nettyBuf.writeFloatLE(value)
    }
    
    /**
     * Writes the given double at current writer position
     */
    override fun writeDouble(value: Double) {
        nettyBuf.writeDouble(value)
    }
    
    /**
     * Writes the given double at current writer position in little endian
     */
    override fun writeDoubleLE(value: Double) {
        nettyBuf.writeDoubleLE(value)
    }
    
    /**
     * Writes the given int in a variable length format at current writer position.
     */
    override fun writeVarInt(value: Int) {
        var currentValue = value
        while ((currentValue and -128) != 0) {
            this.writeByte(((currentValue and 127) or 128).toByte())
            currentValue = currentValue ushr 7
        }
        
        this.writeByte(currentValue.toByte())
    }
    
    /**
     * Writes the given long in a variable length format at current writer position.
     */
    override fun writeVarLong(value: Long) {
        var currentValue = value
        while ((currentValue and -128L) != 0.toLong()) {
            this.writeByte(((currentValue and 127) or 128).toByte())
            currentValue = currentValue ushr 7
        }
        
        this.writeByte(currentValue.toByte())
    }
    
    /**
     * Writes the given bytes at current writer position
     */
    override fun writeBytes(src: ByteArray, srcIndex: Int, length: Int) {
        nettyBuf.writeBytes(src, srcIndex, length)
    }
    
    /**
     * Writes the given bytes at current writer position
     */
    override fun writeBytes(src: ByteArray, length: Int) {
        nettyBuf.writeBytes(src, 0, length)
    }
    
    /**
     * Writes the given bytes at current writer position
     */
    override fun writeBytes(src: ByteArray) {
        nettyBuf.writeBytes(src)
    }
    
    /**
     * Writes the given bytes at current writer position
     */
    override fun writeBytes(src: ByteBuffer, srcIndex: Int, length: Int) {
        nettyBuf.writeBytes(src.getBytes(srcIndex, length))
    }
    
    /**
     * Writes the given bytes at current writer position
     */
    override fun writeBytes(src: ByteBuffer, length: Int) {
        nettyBuf.writeBytes(src.getBytes(0, length))
    }
    
    /**
     * Writes the given bytes at current writer position
     */
    override fun writeBytes(src: ByteBuffer) {
        nettyBuf.writeBytes(src.getBytes(0, src.readableBytes() + src.readerIndex()))
    }
    
    /**
     * Writes the given bytes at current writer position
     */
    override fun writeBytes(src: InputStream, length: Int) {
        nettyBuf.writeBytes(src, length)
    }
    
    /**
     * Writes the given bytes at current writer position
     */
    override fun writeBytes(src: ScatteringByteChannel, length: Int) {
        nettyBuf.writeBytes(src, length)
    }
    
    /**
     * Writes the given bytes at current writer position
     */
    override fun writeBytes(src: FileChannel, position: Long, length: Int) {
        nettyBuf.writeBytes(src, position, length)
    }
    
    /**
     * Writes the given [CharSequence] at current writer position
     */
    override fun writeCharSequence(value: CharSequence, charset: Charset) {
        nettyBuf.writeCharSequence(value, charset)
    }
    
    /**
     * Writes the given string at current writer position
     */
    override fun writeString(value: String) {
        val bytes = value.encodeToByteArray()
        writeVarInt(bytes.size)
        writeBytes(bytes)
    }
    
    /**
     * Writes the given [UUID] at current writer position
     */
    override fun writeUUID(value: UUID) {
        writeLong(value.mostSignificantBits)
        writeLong(value.leastSignificantBits)
    }
    
    /**
     * Writes the given amount of nul bytes at current writer position
     */
    override fun writeZero(length: Int) {
        nettyBuf.writeZero(length)
    }
    
    /**
     * Gets the index of the first occurrence of the given byte.
     */
    override fun indexOf(fromIndex: Int, toIndex: Int, value: Byte): Int {
        return nettyBuf.indexOf(fromIndex, toIndex, value)
    }
    
    /**
     * Gets the number of bytes between the current readerPosition and the first occurrence of the given byte.
     */
    override fun bytesBefore(value: Byte): Int {
        return nettyBuf.bytesBefore(value)
    }
    
    /**
     * Gets the number of bytes between the current readerPosition and the first occurrence of the given byte.
     */
    override fun bytesBefore(length: Int, value: Byte): Int {
        return nettyBuf.bytesBefore(length, value)
    }
    
    /**
     * Gets the number of bytes between the specified index and the first occurrence of the given byte.
     */
    override fun bytesBefore(fromIndex: Int, toIndex: Int, value: Byte): Int {
        return nettyBuf.bytesBefore(fromIndex, toIndex, value)
    }
    
    /**
     * Runs an expression for each byte.
     */
    override fun forEachByte(action: (Byte) -> Unit) {
        nettyBuf.forEachByte { action(it); true }
    }
    
    /**
     * Returns a copy of the current buffer.
     */
    override fun copy(): ByteBuffer {
        return NettyByteBuffer(nettyBuf.copy())
    }
    
    /**
     * Returns a copy of the current buffer in the given range.
     */
    override fun copy(fromIndex: Int, toIndex: Int): ByteBuffer {
        return NettyByteBuffer(nettyBuf.copy(fromIndex, toIndex))
    }
    
    /**
     * Returns a slice of this buffer's readable bytes. They both reference the same array.
     */
    override fun slice(): ByteBuffer {
        return NettyByteBuffer(nettyBuf.slice())
    }
    
    /**
     * Returns a slice of this buffer's readable bytes. They both reference the same array.
     */
    override fun slice(index: Int, length: Int): ByteBuffer {
        return NettyByteBuffer(nettyBuf.slice(index, length))
    }
    
    /**
     * Returns a retained slice of this buffer's readable bytes. They both reference the same array.
     */
    override fun retainedSlice(): ByteBuffer {
        return NettyByteBuffer(nettyBuf.retainedSlice())
    }
    
    /**
     * Returns a retained slice of this buffer's readable bytes. They both reference the same array.
     */
    override fun retainedSlice(index: Int, length: Int): ByteBuffer {
        return NettyByteBuffer(nettyBuf.retainedSlice(index, length))
    }
    
    /**
     * Returns a duplicate of this buffer. They both reference the same array.
     */
    override fun duplicate(): ByteBuffer {
        return NettyByteBuffer(nettyBuf.duplicate())
    }
    
    /**
     * Returns a retained duplicate of this buffer. They both reference the same array.
     */
    override fun retainedDuplicate(): ByteBuffer {
        return NettyByteBuffer(nettyBuf.retainedDuplicate())
    }
    
    /**
     * Returns true if this buffer has an array.
     */
    override fun hasArray(): Boolean {
        return nettyBuf.hasArray()
    }
    
    /**
     * Returns the internal array of this buffer (including unused but allocated bytes).
     */
    override fun array(): ByteArray {
        return nettyBuf.array()
    }
    
    /**
     * Returns the array offset of this buffer.
     */
    override fun arrayOffset(): Int {
        return nettyBuf.arrayOffset()
    }
    
    /**
     * Turn this buffer into a byte array.
     */
    override fun toByteArray(): ByteArray {
        markReaderIndex()
        readerIndex(0)
        val bytes = ByteArray(readableBytes())
        readBytes(bytes)
        resetReaderIndex()
        return bytes
    }
    
    /**
     * hashCode() of this buffer.
     */
    override fun hashCode(): Int {
        return nettyBuf.hashCode()
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NettyByteBuffer) return false
        
        return nettyBuf == other.nettyBuf
    }
    
    
}