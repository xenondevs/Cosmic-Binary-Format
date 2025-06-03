package xyz.xenondevs.cbf.serializer

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VersionedBinarySerializerTest {
    
    private object OldIntSerializer : UnversionedBinarySerializer<Int>() {
        
        override fun readUnversioned(reader: ByteReader): Int {
            return reader.readIntLE()
        }
        
        override fun writeUnversioned(obj: Int, writer: ByteWriter) {
            return writer.writeIntLE(obj)
        }
        
        override fun copyNonNull(obj: Int): Int = obj
        
    }
    
    private object NewIntSerializer : VersionedBinarySerializer<Int>(2U) {
        
        override fun readVersioned(version: UByte, reader: ByteReader): Int {
            return when (version) {
                1.toUByte() -> reader.readIntLE()
                2.toUByte() -> reader.readInt()
                else -> throw IllegalArgumentException("Unsupported version: $version")
            }
        }
        
        override fun writeVersioned(obj: Int, writer: ByteWriter) {
            writer.writeInt(obj)
        }
        
        override fun copyNonNull(obj: Int): Int = obj
        
    }
    
    @ValueSource(ints = [Int.MIN_VALUE, -1, 0, 1, 1337, Int.MAX_VALUE])
    @ParameterizedTest
    fun testVersionedBinarySerializer(value: Int) {
        assertEquals(value, OldIntSerializer.read(OldIntSerializer.write(value)))
        assertEquals(value, NewIntSerializer.read(OldIntSerializer.write(value)))
        assertEquals(value, NewIntSerializer.read(NewIntSerializer.write(value)))
        assertNull(NewIntSerializer.read(OldIntSerializer.write(null)))
    }
    
}