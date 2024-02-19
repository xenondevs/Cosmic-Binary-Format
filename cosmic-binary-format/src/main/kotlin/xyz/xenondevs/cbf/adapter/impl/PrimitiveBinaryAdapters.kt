package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import kotlin.reflect.KType

internal object ByteBinaryAdapter : BinaryAdapter<Byte> {
    
    override fun write(obj: Byte, type: KType, writer: ByteWriter) {
        writer.writeByte(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Byte {
        return reader.readByte()
    }
    
    override fun copy(obj: Byte, type: KType): Byte {
        return obj
    }
    
}

internal object ByteArrayBinaryAdapter : BinaryAdapter<ByteArray> {
    
    override fun write(obj: ByteArray, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        writer.writeBytes(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): ByteArray {
        return ByteArray(reader.readVarInt()) { reader.readByte() }
    }
    
    override fun copy(obj: ByteArray, type: KType): ByteArray {
        return obj.clone()
    }
    
}

internal object ShortBinaryAdapter : BinaryAdapter<Short> {
    
    override fun write(obj: Short, type: KType, writer: ByteWriter) {
        writer.writeShort(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Short {
        return reader.readShort()
    }
    
    override fun copy(obj: Short, type: KType): Short {
        return obj
    }
    
}

internal object ShortArrayBinaryAdapter : BinaryAdapter<ShortArray> {
    
    override fun write(obj: ShortArray, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach { writer.writeShort(it) }
    }
    
    override fun read(type: KType, reader: ByteReader): ShortArray {
        return ShortArray(reader.readVarInt()) { reader.readShort() }
    }
    
    override fun copy(obj: ShortArray, type: KType): ShortArray {
        return obj.clone()
    }
    
}

internal object IntBinaryAdapter : BinaryAdapter<Int> {
    
    override fun write(obj: Int, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Int {
        return reader.readVarInt()
    }
    
    override fun copy(obj: Int, type: KType): Int {
        return obj
    }
    
}

internal object IntArrayBinaryAdapter : BinaryAdapter<IntArray> {
    
    override fun write(obj: IntArray, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeVarInt)
    }
    
    override fun read(type: KType, reader: ByteReader): IntArray {
        return IntArray(reader.readVarInt()) { reader.readVarInt() }
    }
    
    override fun copy(obj: IntArray, type: KType): IntArray {
        return obj.clone()
    }
    
}

internal object LongBinaryAdapter : BinaryAdapter<Long> {
    
    override fun write(obj: Long, type: KType, buf: ByteWriter) {
        buf.writeVarLong(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Long {
        return reader.readVarLong()
    }
    
    override fun copy(obj: Long, type: KType): Long {
        return obj
    }
    
}

internal object LongArrayBinaryAdapter : BinaryAdapter<LongArray> {
    
    override fun write(obj: LongArray, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeVarLong)
    }
    
    override fun read(type: KType, reader: ByteReader): LongArray {
        return LongArray(reader.readVarInt()) { reader.readVarLong() }
    }
    
    override fun copy(obj: LongArray, type: KType): LongArray {
        return obj.clone()
    }
    
}

internal object FloatBinaryAdapter : BinaryAdapter<Float> {
    
    override fun write(obj: Float, type: KType, writer: ByteWriter) {
        writer.writeFloat(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Float {
        return reader.readFloat()
    }
    
    override fun copy(obj: Float, type: KType): Float {
        return obj
    }
    
}

internal object FloatArrayBinaryAdapter : BinaryAdapter<FloatArray> {
    
    override fun write(obj: FloatArray, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeFloat)
    }
    
    override fun read(type: KType, reader: ByteReader): FloatArray {
        return FloatArray(reader.readVarInt()) { reader.readFloat() }
    }
    
    override fun copy(obj: FloatArray, type: KType): FloatArray {
        return obj.clone()
    }
    
}

internal object DoubleBinaryAdapter : BinaryAdapter<Double> {
    
    override fun write(obj: Double, type: KType, buf: ByteWriter) {
        buf.writeDouble(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Double {
        return reader.readDouble()
    }
    
    override fun copy(obj: Double, type: KType): Double {
        return obj
    }
    
}

internal object DoubleArrayBinaryAdapter : BinaryAdapter<DoubleArray> {
    
    override fun write(obj: DoubleArray, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeDouble)
    }
    
    override fun read(type: KType, reader: ByteReader): DoubleArray {
        return DoubleArray(reader.readVarInt()) { reader.readDouble() }
    }
    
    override fun copy(obj: DoubleArray, type: KType): DoubleArray {
        return obj.clone()
    }
    
}

internal object BooleanBinaryAdapter : BinaryAdapter<Boolean> {
    
    override fun write(obj: Boolean, type: KType, writer: ByteWriter) {
        writer.writeBoolean(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Boolean {
        return reader.readBoolean()
    }
    
    override fun copy(obj: Boolean, type: KType): Boolean {
        return obj
    }
    
}

internal object BooleanArrayBinaryAdapter : BinaryAdapter<BooleanArray> {
    
    override fun write(obj: BooleanArray, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeBoolean)
    }
    
    override fun read(type: KType, reader: ByteReader): BooleanArray {
        return BooleanArray(reader.readVarInt()) { reader.readBoolean() }
    }
    
    override fun copy(obj: BooleanArray, type: KType): BooleanArray {
        return obj.clone()
    }
    
}

internal object CharBinaryAdapter : BinaryAdapter<Char> {
    
    override fun write(obj: Char, type: KType, writer: ByteWriter) {
        writer.writeChar(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Char {
        return reader.readChar()
    }
    
    override fun copy(obj: Char, type: KType): Char {
        return obj
    }
    
}

internal object CharArrayBinaryAdapter : BinaryAdapter<CharArray> {
    
    override fun write(obj: CharArray, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach { writer.writeChar(it) }
    }
    
    override fun read(type: KType, reader: ByteReader): CharArray {
        return CharArray(reader.readVarInt()) { reader.readChar() }
    }
    
    override fun copy(obj: CharArray, type: KType): CharArray {
        return obj.clone()
    }
    
}