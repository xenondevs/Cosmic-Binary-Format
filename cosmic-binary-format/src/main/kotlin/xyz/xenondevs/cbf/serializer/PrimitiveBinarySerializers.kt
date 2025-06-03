package xyz.xenondevs.cbf.serializer

import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter

internal object ByteBinarySerializer : UnversionedBinarySerializer<Byte>() {
    
    override fun writeUnversioned(obj: Byte, writer: ByteWriter) {
        writer.writeByte(obj)
    }
    
    override fun readUnversioned(reader: ByteReader): Byte {
        return reader.readByte()
    }
    
    override fun copyNonNull(obj: Byte): Byte {
        return obj
    }
    
}

internal object ByteArrayBinarySerializer : UnversionedBinarySerializer<ByteArray>() {
    
    override fun writeUnversioned(obj: ByteArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        writer.writeBytes(obj)
    }
    
    override fun readUnversioned(reader: ByteReader): ByteArray {
        return ByteArray(reader.readVarInt()) { reader.readByte() }
    }
    
    override fun copyNonNull(obj: ByteArray): ByteArray {
        return obj.clone()
    }
    
}

internal object ShortBinarySerializer : UnversionedBinarySerializer<Short>() {
    
    override fun writeUnversioned(obj: Short, writer: ByteWriter) {
        writer.writeShort(obj)
    }
    
    override fun readUnversioned(reader: ByteReader): Short {
        return reader.readShort()
    }
    
    override fun copyNonNull(obj: Short): Short {
        return obj
    }
    
}

internal object ShortArrayBinarySerializer : UnversionedBinarySerializer<ShortArray>() {
    
    override fun writeUnversioned(obj: ShortArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach { writer.writeShort(it) }
    }
    
    override fun readUnversioned(reader: ByteReader): ShortArray {
        return ShortArray(reader.readVarInt()) { reader.readShort() }
    }
    
    override fun copyNonNull(obj: ShortArray): ShortArray {
        return obj.clone()
    }
    
}

internal object IntBinarySerializer : UnversionedBinarySerializer<Int>() {
    
    override fun writeUnversioned(obj: Int, writer: ByteWriter) {
        writer.writeVarInt(obj)
    }
    
    override fun readUnversioned(reader: ByteReader): Int {
        return reader.readVarInt()
    }
    
    override fun copyNonNull(obj: Int): Int {
        return obj
    }
    
}

internal object IntArrayBinarySerializer : UnversionedBinarySerializer<IntArray>() {
    
    override fun writeUnversioned(obj: IntArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeVarInt)
    }
    
    override fun readUnversioned(reader: ByteReader): IntArray {
        return IntArray(reader.readVarInt()) { reader.readVarInt() }
    }
    
    override fun copyNonNull(obj: IntArray): IntArray {
        return obj.clone()
    }
    
}

internal object LongBinarySerializer : UnversionedBinarySerializer<Long>() {
    
    override fun writeUnversioned(obj: Long, buf: ByteWriter) {
        buf.writeVarLong(obj)
    }
    
    override fun readUnversioned(reader: ByteReader): Long {
        return reader.readVarLong()
    }
    
    override fun copyNonNull(obj: Long): Long {
        return obj
    }
    
}

internal object LongArrayBinarySerializer : UnversionedBinarySerializer<LongArray>() {
    
    override fun writeUnversioned(obj: LongArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeVarLong)
    }
    
    override fun readUnversioned(reader: ByteReader): LongArray {
        return LongArray(reader.readVarInt()) { reader.readVarLong() }
    }
    
    override fun copyNonNull(obj: LongArray): LongArray {
        return obj.clone()
    }
    
}

internal object FloatBinarySerializer : UnversionedBinarySerializer<Float>() {
    
    override fun writeUnversioned(obj: Float, writer: ByteWriter) {
        writer.writeFloat(obj)
    }
    
    override fun readUnversioned(reader: ByteReader): Float {
        return reader.readFloat()
    }
    
    override fun copyNonNull(obj: Float): Float {
        return obj
    }
    
}

internal object FloatArrayBinarySerializer : UnversionedBinarySerializer<FloatArray>() {
    
    override fun writeUnversioned(obj: FloatArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeFloat)
    }
    
    override fun readUnversioned(reader: ByteReader): FloatArray {
        return FloatArray(reader.readVarInt()) { reader.readFloat() }
    }
    
    override fun copyNonNull(obj: FloatArray): FloatArray {
        return obj.clone()
    }
    
}

internal object DoubleBinarySerializer : UnversionedBinarySerializer<Double>() {
    
    override fun writeUnversioned(obj: Double, buf: ByteWriter) {
        buf.writeDouble(obj)
    }
    
    override fun readUnversioned(reader: ByteReader): Double {
        return reader.readDouble()
    }
    
    override fun copyNonNull(obj: Double): Double {
        return obj
    }
    
}

internal object DoubleArrayBinarySerializer : UnversionedBinarySerializer<DoubleArray>() {
    
    override fun writeUnversioned(obj: DoubleArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeDouble)
    }
    
    override fun readUnversioned(reader: ByteReader): DoubleArray {
        return DoubleArray(reader.readVarInt()) { reader.readDouble() }
    }
    
    override fun copyNonNull(obj: DoubleArray): DoubleArray {
        return obj.clone()
    }
    
}

internal object BooleanBinarySerializer : UnversionedBinarySerializer<Boolean>() {
    
    override fun writeUnversioned(obj: Boolean, writer: ByteWriter) {
        writer.writeBoolean(obj)
    }
    
    override fun readUnversioned(reader: ByteReader): Boolean {
        return reader.readBoolean()
    }
    
    override fun copyNonNull(obj: Boolean): Boolean {
        return obj
    }
    
}

internal object BooleanArrayBinarySerializer : UnversionedBinarySerializer<BooleanArray>() {
    
    override fun writeUnversioned(obj: BooleanArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeBoolean)
    }
    
    override fun readUnversioned(reader: ByteReader): BooleanArray {
        return BooleanArray(reader.readVarInt()) { reader.readBoolean() }
    }
    
    override fun copyNonNull(obj: BooleanArray): BooleanArray {
        return obj.clone()
    }
    
}

internal object CharBinarySerializer : UnversionedBinarySerializer<Char>() {
    
    override fun writeUnversioned(obj: Char, writer: ByteWriter) {
        writer.writeChar(obj)
    }
    
    override fun readUnversioned(reader: ByteReader): Char {
        return reader.readChar()
    }
    
    override fun copyNonNull(obj: Char): Char {
        return obj
    }
    
}

internal object CharArrayBinarySerializer : UnversionedBinarySerializer<CharArray>() {
    
    override fun writeUnversioned(obj: CharArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach { writer.writeChar(it) }
    }
    
    override fun readUnversioned(reader: ByteReader): CharArray {
        return CharArray(reader.readVarInt()) { reader.readChar() }
    }
    
    override fun copyNonNull(obj: CharArray): CharArray {
        return obj.clone()
    }
    
}