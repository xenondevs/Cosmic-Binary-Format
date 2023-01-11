package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.lang.reflect.Type

internal object ByteBinaryAdapter : BinaryAdapter<Byte> {
    
    override fun write(obj: Byte, writer: ByteWriter) {
        writer.writeByte(obj)
    }
    
    override fun read(type: Type, reader: ByteReader): Byte {
        return reader.readByte()
    }
    
}

internal object ByteArrayBinaryAdapter : BinaryAdapter<ByteArray> {
    
    override fun write(obj: ByteArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        writer.writeBytes(obj)
    }
    
    override fun read(type: Type, reader: ByteReader): ByteArray {
        return ByteArray(reader.readVarInt()) { reader.readByte() }
    }
    
}

internal object ShortBinaryAdapter : BinaryAdapter<Short> {
    
    override fun write(obj: Short, writer: ByteWriter) {
        writer.writeShort(obj)
    }
    
    override fun read(type: Type, reader: ByteReader): Short {
        return reader.readShort()
    }
    
}

internal object ShortArrayBinaryAdapter : BinaryAdapter<ShortArray> {
    
    override fun write(obj: ShortArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach { writer.writeShort(it) }
    }
    
    override fun read(type: Type, reader: ByteReader): ShortArray {
        return ShortArray(reader.readVarInt()) { reader.readShort() }
    }
    
}

internal object IntBinaryAdapter : BinaryAdapter<Int> {
    
    override fun write(obj: Int, writer: ByteWriter) {
        writer.writeVarInt(obj)
    }
    
    override fun read(type: Type, reader: ByteReader): Int {
        return reader.readVarInt()
    }
    
}

internal object IntArrayBinaryAdapter : BinaryAdapter<IntArray> {
    
    override fun write(obj: IntArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeVarInt)
    }
    
    override fun read(type: Type, reader: ByteReader): IntArray {
        return IntArray(reader.readVarInt()) { reader.readVarInt() }
    }
    
}

internal object LongBinaryAdapter : BinaryAdapter<Long> {
    
    override fun write(obj: Long, buf: ByteWriter) {
        buf.writeVarLong(obj)
    }
    
    override fun read(type: Type, reader: ByteReader): Long {
        return reader.readVarLong()
    }
    
}

internal object LongArrayBinaryAdapter : BinaryAdapter<LongArray> {
    
    override fun write(obj: LongArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeVarLong)
    }
    
    override fun read(type: Type, reader: ByteReader): LongArray {
        return LongArray(reader.readVarInt()) { reader.readVarLong() }
    }
    
}

internal object FloatBinaryAdapter : BinaryAdapter<Float> {
    
    override fun write(obj: Float, buf: ByteWriter) {
        buf.writeFloat(obj)
    }
    
    override fun read(type: Type, reader: ByteReader): Float {
        return reader.readFloat()
    }
    
}

internal object FloatArrayBinaryAdapter : BinaryAdapter<FloatArray> {
    
    override fun write(obj: FloatArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeFloat)
    }
    
    override fun read(type: Type, reader: ByteReader): FloatArray {
        return FloatArray(reader.readVarInt()) { reader.readFloat() }
    }
    
}

internal object DoubleBinaryAdapter : BinaryAdapter<Double> {
    
    override fun write(obj: Double, buf: ByteWriter) {
        buf.writeDouble(obj)
    }
    
    override fun read(type: Type, reader: ByteReader): Double {
        return reader.readDouble()
    }
    
}

internal object DoubleArrayBinaryAdapter : BinaryAdapter<DoubleArray> {
    
    override fun write(obj: DoubleArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeDouble)
    }
    
    override fun read(type: Type, reader: ByteReader): DoubleArray {
        return DoubleArray(reader.readVarInt()) { reader.readDouble() }
    }
    
}

internal object BooleanBinaryAdapter : BinaryAdapter<Boolean> {
    
    override fun write(obj: Boolean, writer: ByteWriter) {
        writer.writeBoolean(obj)
    }
    
    override fun read(type: Type, reader: ByteReader): Boolean {
        return reader.readBoolean()
    }
    
}

internal object BooleanArrayBinaryAdapter : BinaryAdapter<BooleanArray> {
    
    override fun write(obj: BooleanArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach(writer::writeBoolean)
    }
    
    override fun read(type: Type, reader: ByteReader): BooleanArray {
        return BooleanArray(reader.readVarInt()) { reader.readBoolean() }
    }
    
}

internal object CharBinaryAdapter : BinaryAdapter<Char> {
    
    override fun write(obj: Char, writer: ByteWriter) {
        writer.writeChar(obj)
    }
    
    override fun read(type: Type, reader: ByteReader): Char {
        return reader.readChar()
    }
    
}

internal object CharArrayBinaryAdapter : BinaryAdapter<CharArray> {
    
    override fun write(obj: CharArray, writer: ByteWriter) {
        writer.writeVarInt(obj.size)
        obj.forEach { writer.writeChar(it) }
    }
    
    override fun read(type: Type, reader: ByteReader): CharArray {
        return CharArray(reader.readVarInt()) { reader.readChar() }
    }
    
}