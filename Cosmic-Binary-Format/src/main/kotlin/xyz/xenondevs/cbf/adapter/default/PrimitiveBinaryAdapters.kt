package xyz.xenondevs.cbf.adapter.default

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.buffer.ByteBuffer
import java.lang.reflect.Type

internal object ByteBinaryAdapter : BinaryAdapter<Byte> {
    
    override fun write(obj: Byte, buf: ByteBuffer) {
        buf.writeByte(obj)
    }
    
    override fun read(type: Type, buf: ByteBuffer): Byte {
        return buf.readByte()
    }
    
}

internal object ByteArrayBinaryAdapter : BinaryAdapter<ByteArray> {
    
    override fun write(obj: ByteArray, buf: ByteBuffer) {
        buf.writeInt(obj.size)
        obj.forEach { buf.writeByte(it) }
    }
    
    override fun read(type: Type, buf: ByteBuffer): ByteArray {
        return ByteArray(buf.readInt()) { buf.readByte() }
    }
    
}

internal object ShortBinaryAdapter : BinaryAdapter<Short> {
    
    override fun write(obj: Short, buf: ByteBuffer) {
        buf.writeShort(obj)
    }
    
    override fun read(type: Type, buf: ByteBuffer): Short {
        return buf.readShort()
    }
    
}

internal object ShortArrayBinaryAdapter : BinaryAdapter<ShortArray> {
    
    override fun write(obj: ShortArray, buf: ByteBuffer) {
        buf.writeInt(obj.size)
        obj.forEach { buf.writeShort(it) }
    }
    
    override fun read(type: Type, buf: ByteBuffer): ShortArray {
        return ShortArray(buf.readInt()) { buf.readShort() }
    }
    
}

internal object IntBinaryAdapter : BinaryAdapter<Int> {
    
    override fun write(obj: Int, buf: ByteBuffer) {
        buf.writeInt(obj)
    }
    
    override fun read(type: Type, buf: ByteBuffer): Int {
        return buf.readInt()
    }
    
}

internal object IntArrayBinaryAdapter : BinaryAdapter<IntArray> {
    
    override fun write(obj: IntArray, buf: ByteBuffer) {
        buf.writeInt(obj.size)
        obj.forEach(buf::writeInt)
    }
    
    override fun read(type: Type, buf: ByteBuffer): IntArray {
        return IntArray(buf.readInt()) { buf.readInt() }
    }
    
}

internal object LongBinaryAdapter : BinaryAdapter<Long> {
    
    override fun write(obj: Long, buf: ByteBuffer) {
        buf.writeLong(obj)
    }
    
    override fun read(type: Type, buf: ByteBuffer): Long {
        return buf.readLong()
    }
    
}

internal object LongArrayBinaryAdapter : BinaryAdapter<LongArray> {
    
    override fun write(obj: LongArray, buf: ByteBuffer) {
        buf.writeInt(obj.size)
        obj.forEach(buf::writeLong)
    }
    
    override fun read(type: Type, buf: ByteBuffer): LongArray {
        return LongArray(buf.readInt()) { buf.readLong() }
    }
    
}

internal object FloatBinaryAdapter : BinaryAdapter<Float> {
    
    override fun write(obj: Float, buf: ByteBuffer) {
        buf.writeFloat(obj)
    }
    
    override fun read(type: Type, buf: ByteBuffer): Float {
        return buf.readFloat()
    }
    
}

internal object FloatArrayBinaryAdapter : BinaryAdapter<FloatArray> {
    
    override fun write(obj: FloatArray, buf: ByteBuffer) {
        buf.writeInt(obj.size)
        obj.forEach(buf::writeFloat)
    }
    
    override fun read(type: Type, buf: ByteBuffer): FloatArray {
        return FloatArray(buf.readInt()) { buf.readFloat() }
    }
    
}

internal object DoubleBinaryAdapter : BinaryAdapter<Double> {
    
    override fun write(obj: Double, buf: ByteBuffer) {
        buf.writeDouble(obj)
    }
    
    override fun read(type: Type, buf: ByteBuffer): Double {
        return buf.readDouble()
    }
    
}

internal object DoubleArrayBinaryAdapter : BinaryAdapter<DoubleArray> {
    
    override fun write(obj: DoubleArray, buf: ByteBuffer) {
        buf.writeInt(obj.size)
        obj.forEach(buf::writeDouble)
    }
    
    override fun read(type: Type, buf: ByteBuffer): DoubleArray {
        return DoubleArray(buf.readInt()) { buf.readDouble() }
    }
    
}

internal object BooleanBinaryAdapter : BinaryAdapter<Boolean> {
    
    override fun write(obj: Boolean, buf: ByteBuffer) {
        buf.writeBoolean(obj)
    }
    
    override fun read(type: Type, buf: ByteBuffer): Boolean {
        return buf.readBoolean()
    }
    
}

internal object BooleanArrayBinaryAdapter : BinaryAdapter<BooleanArray> {
    
    override fun write(obj: BooleanArray, buf: ByteBuffer) {
        buf.writeInt(obj.size)
        obj.forEach(buf::writeBoolean)
    }
    
    override fun read(type: Type, buf: ByteBuffer): BooleanArray {
        return BooleanArray(buf.readInt()) { buf.readBoolean() }
    }
    
}

internal object CharBinaryAdapter : BinaryAdapter<Char> {
    
    override fun write(obj: Char, buf: ByteBuffer) {
        buf.writeChar(obj)
    }
    
    override fun read(type: Type, buf: ByteBuffer): Char {
        return buf.readChar()
    }
    
}

internal object CharArrayBinaryAdapter : BinaryAdapter<CharArray> {
    
    override fun write(obj: CharArray, buf: ByteBuffer) {
        buf.writeInt(obj.size)
        obj.forEach { buf.writeChar(it) }
    }
    
    override fun read(type: Type, buf: ByteBuffer): CharArray {
        return CharArray(buf.readInt()) { buf.readChar() }
    }
    
}