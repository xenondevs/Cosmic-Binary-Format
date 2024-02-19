package xyz.xenondevs.cbf.io

import java.io.DataOutput
import java.io.DataOutputStream
import java.io.OutputStream
import java.util.*

interface ByteWriter {
    
    /**
     * Writes a [Byte].
     */
    fun writeByte(value: Byte)
    
    /**
     * Writes [length] number of bytes from [src] starting at [srcIndex].
     */
    fun writeBytes(src: ByteArray, srcIndex: Int, length: Int)
    
    /**
     * Writes [length] number of bytes from [src].
     */
    fun writeBytes(src: ByteArray, length: Int) {
        writeBytes(src, 0, length)
    }
    
    /**
     * Writes all bytes from [src].
     */
    fun writeBytes(src: ByteArray) {
        writeBytes(src, 0, src.size)
    }
    
    /**
     * Writes a [Boolean].
     */
    fun writeBoolean(value: Boolean) {
        writeByte(if (value) 1 else 0)
    }
    
    /**
     * Writes a [UByte].
     */
    fun writeUnsignedByte(value: UByte) {
        writeByte(value.toByte())
    }
    
    /**
     * Writes a [Short].
     */
    fun writeShort(value: Short) {
        writeByte((value.toInt() shr 8).toByte())
        writeByte(value.toByte())
    }
    
    /**
     * Writes a [Short] in little endian.
     */
    fun writeShortLE(value: Short) {
        writeByte(value.toByte())
        writeByte((value.toInt() shr 8).toByte())
    }
    
    /**
     * Writes a [UShort].
     */
    fun writeUnsignedShort(value: UShort) {
        writeShort(value.toShort())
    }
    
    /**
     * Writes a [UShort] in little endian.
     */
    fun writeUnsignedShortLE(value: UShort) {
        writeShortLE(value.toShort())
    }
    
    /**
     * Writes a medium.
     */
    fun writeMedium(value: Int) {
        writeByte((value shr 16).toByte())
        writeByte((value shr 8).toByte())
        writeByte(value.toByte())
    }
    
    /**
     * Writes a medium in little endian.
     */
    fun writeMediumLE(value: Int) {
        writeByte(value.toByte())
        writeByte((value shr 8).toByte())
        writeByte((value shr 16).toByte())
    }
    
    /**
     * Writes an unsigned medium.
     */
    fun writeUnsignedMedium(value: UInt) {
        writeMedium(value.toInt())
    }
    
    /**
     * Writes an unsigned medium in little endian.
     */
    fun writeUnsignedMediumLE(value: UInt) {
        writeMediumLE(value.toInt())
    }
    
    /**
     * Writes an [Int].
     */
    fun writeInt(value: Int) {
        writeByte((value shr 24).toByte())
        writeByte((value shr 16).toByte())
        writeByte((value shr 8).toByte())
        writeByte(value.toByte())
    }
    
    /**
     * Writes an [Int] in little endian.
     */
    fun writeIntLE(value: Int) {
        writeByte(value.toByte())
        writeByte((value shr 8).toByte())
        writeByte((value shr 16).toByte())
        writeByte((value shr 24).toByte())
    }
    
    /**
     * Writes a [UInt].
     */
    fun writeUnsignedInt(value: UInt) {
        writeInt(value.toInt())
    }
    
    /**
     * Writes a [UInt] in little endian.
     */
    fun writeUnsignedIntLE(value: UInt) {
        writeIntLE(value.toInt())
    }
    
    /**
     * Writes a [Long].
     */
    fun writeLong(value: Long) {
        writeByte((value shr 56).toByte())
        writeByte((value shr 48).toByte())
        writeByte((value shr 40).toByte())
        writeByte((value shr 32).toByte())
        writeByte((value shr 24).toByte())
        writeByte((value shr 16).toByte())
        writeByte((value shr 8).toByte())
        writeByte(value.toByte())
    }
    
    /**
     * Writes a [Long] in little endian.
     */
    fun writeLongLE(value: Long) {
        writeByte(value.toByte())
        writeByte((value shr 8).toByte())
        writeByte((value shr 16).toByte())
        writeByte((value shr 24).toByte())
        writeByte((value shr 32).toByte())
        writeByte((value shr 40).toByte())
        writeByte((value shr 48).toByte())
        writeByte((value shr 56).toByte())
    }
    
    /**
     * Writes a [ULong].
     */
    fun writeUnsignedLong(value: ULong) {
        writeLong(value.toLong())
    }
    
    /**
     * Writes a [ULong] in little endian.
     */
    fun writeUnsignedLongLE(value: ULong) {
        writeLongLE(value.toLong())
    }
    
    /**
     * Writes a [Float].
     */
    fun writeChar(value: Char) {
        writeShort(value.code.toShort())
    }
    
    /**
     * Writes a [Float] in little endian.
     */
    fun writeFloat(value: Float) {
        writeInt(value.toRawBits())
    }
    
    /**
     * Writes a [Float] in little endian.
     */
    fun writeFloatLE(value: Float) {
        writeIntLE(value.toRawBits())
    }
    
    /**
     * Writes a [Double].
     */
    fun writeDouble(value: Double) {
        writeLong(value.toRawBits())
    }
    
    /**
     * Writes a [Double] in little endian.
     */
    fun writeDoubleLE(value: Double) {
        writeLongLE(value.toRawBits())
    }
    
    /**
     * Writes an [Int] in a variable length format.
     */
    fun writeVarInt(value: Int) {
        var currentValue = value
        while ((currentValue and -128) != 0) {
            this.writeByte(((currentValue and 127) or 128).toByte())
            currentValue = currentValue ushr 7
        }
        
        this.writeByte(currentValue.toByte())
    }
    
    /**
     * Writes a [UInt] in a variable length format.
     */
    fun writeUnsignedVarInt(value: UInt) {
        writeVarInt(value.toInt())
    }
    
    /**
     * Writes a [Long] in a variable length format.
     */
    fun writeVarLong(value: Long) {
        var currentValue = value
        while ((currentValue and -128L) != 0.toLong()) {
            this.writeByte(((currentValue and 127) or 128).toByte())
            currentValue = currentValue ushr 7
        }
        
        this.writeByte(currentValue.toByte())
    }
    
    /**
     * Writes a [ULong] in a variable length format.
     */
    fun writeUnsignedVarLong(value: ULong) {
        writeVarLong(value.toLong())
    }
    
    /**
     * Writes a [String].
     */
    fun writeString(value: String) {
        writeVarInt(value.length)
        writeBytes(value.toByteArray())
    }
    
    /**
     * Writes a [UUID].
     */
    fun writeUUID(value: UUID) {
        writeLong(value.mostSignificantBits)
        writeLong(value.leastSignificantBits)
    }
    
    /**
     * Writes [length] amount of null bytes.
     */
    fun writeZero(length: Int) {
        repeat(length) { writeByte(0) }
    }
    
    /**
     * Creates an [OutputStream] that writes to this [ByteWriter].
     */
    fun asOutputStream(): OutputStream =
        ByteWriterToOutputStreamWrapper(this)
    
    /**
     * Creates a [DataOutput] that writes to this [ByteWriter].
     */
    fun asDataOutput(): DataOutput =
        DataOutputStream(asOutputStream())
    
    companion object {
        
        /**
         * Creates a [ByteWriter] that writes to the given [OutputStream].
         */
        fun fromStream(out: OutputStream): ByteWriter =
            OutputStreamToByteWriterWrapper(out)
        
        /**
         * Creates a [ByteWriter] that writes to the given [DataOutput].
         */
        fun fromDataOutput(out: DataOutput): ByteWriter =
            DataOutputToByteWriterWrapper(out)
        
    }
    
}

private class DataOutputToByteWriterWrapper(private val out: DataOutput) : ByteWriter {
    
    override fun writeByte(value: Byte) {
        out.writeByte(value.toInt())
    }
    
    override fun writeBytes(src: ByteArray, srcIndex: Int, length: Int) {
        out.write(src, srcIndex, length)
    }
    
}

private class OutputStreamToByteWriterWrapper(private val out: OutputStream) : ByteWriter {
    
    override fun writeByte(value: Byte) {
        out.write(value.toInt())
    }
    
    override fun writeBytes(src: ByteArray, srcIndex: Int, length: Int) {
        out.write(src, srcIndex, length)
    }
    
}

private class ByteWriterToOutputStreamWrapper(private val writer: ByteWriter) : OutputStream() {
    
    override fun write(b: Int) {
        writer.writeByte(b.toByte())
    }
    
    override fun write(b: ByteArray) {
        writer.writeBytes(b)
    }
    
    override fun write(b: ByteArray, off: Int, len: Int) {
        writer.writeBytes(b, off, len)
    }
    
}