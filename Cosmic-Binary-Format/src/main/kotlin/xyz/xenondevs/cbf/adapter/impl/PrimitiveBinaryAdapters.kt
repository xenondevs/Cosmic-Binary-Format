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
    
}

internal object ByteArrayBinaryAdapter : BinaryAdapter<ByteArray> {
    
    override fun write(obj: ByteArray, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        writer.writeBytes(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): ByteArray {
        return ByteArray(reader.readVarInt()) { reader.readByte() }
    }
    
}

internal object ShortBinaryAdapter : BinaryAdapter<Short> {
    
    override fun write(obj: Short, type: KType, writer: ByteWriter) {
        writer.writeShort(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Short {
        return reader.readShort()
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
    
}

internal object IntBinaryAdapter : BinaryAdapter<Int> {
    
    override fun write(obj: Int, type: KType, writer: ByteWriter) {
        writer.writeVarInt(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Int {
        return reader.readVarInt()
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
    
}

internal object LongBinaryAdapter : BinaryAdapter<Long> {
    
    override fun write(obj: Long, type: KType, buf: ByteWriter) {
        buf.writeVarLong(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Long {
        return reader.readVarLong()
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
    
}

internal object FloatBinaryAdapter : BinaryAdapter<Float> {
    
    override fun write(obj: Float, type: KType, buf: ByteWriter) {
        buf.writeFloat(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Float {
        return reader.readFloat()
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
    
}

internal object DoubleBinaryAdapter : BinaryAdapter<Double> {
    
    override fun write(obj: Double, type: KType, buf: ByteWriter) {
        buf.writeDouble(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Double {
        return reader.readDouble()
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
    
}

internal object BooleanBinaryAdapter : BinaryAdapter<Boolean> {
    
    override fun write(obj: Boolean, type: KType, writer: ByteWriter) {
        writer.writeBoolean(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Boolean {
        return reader.readBoolean()
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
    
}

internal object CharBinaryAdapter : BinaryAdapter<Char> {
    
    override fun write(obj: Char, type: KType, writer: ByteWriter) {
        writer.writeChar(obj)
    }
    
    override fun read(type: KType, reader: ByteReader): Char {
        return reader.readChar()
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
    
}