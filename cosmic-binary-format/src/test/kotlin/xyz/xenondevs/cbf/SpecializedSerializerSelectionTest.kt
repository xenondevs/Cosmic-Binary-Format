package xyz.xenondevs.cbf

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.cbf.serializer.UnversionedBinarySerializer
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertEquals

class SpecializedSerializerSelectionTest {
    
    private object AtomicReferenceStringBinarySerializer : UnversionedBinarySerializer<AtomicReference<String>>() {
        
        override fun copyNonNull(obj: AtomicReference<String>): AtomicReference<String> {
            return AtomicReference(obj.get())
        }
        
        override fun writeUnversioned(obj: AtomicReference<String>, writer: ByteWriter) {
            writer.writeString(obj.get())
        }
        
        override fun readUnversioned(reader: ByteReader): AtomicReference<String> {
            return AtomicReference(reader.readString())
        }
        
    }
    
    private object AtomicReferenceIntBinarySerializer : UnversionedBinarySerializer<AtomicReference<Int>>() {
        
        override fun copyNonNull(obj: AtomicReference<Int>): AtomicReference<Int> {
            return AtomicReference(obj.get())
        }
        
        override fun writeUnversioned(obj: AtomicReference<Int>, writer: ByteWriter) {
            writer.writeInt(obj.get())
        }
        
        override fun readUnversioned(reader: ByteReader): AtomicReference<Int> {
            return AtomicReference(reader.readInt())
        }
        
    }
    
    @Test
    fun testSpecializedAdapterSelection() {
        Cbf.registerSerializer(AtomicReferenceStringBinarySerializer)
        Cbf.registerSerializer(AtomicReferenceIntBinarySerializer)
        
        val stringValue = AtomicReference("A")
        assertEquals(stringValue.get(), Cbf.read<AtomicReference<String>>(Cbf.write(stringValue))!!.get())
        
        val intValue = AtomicReference(0)
        assertEquals(intValue.get(), Cbf.read<AtomicReference<Int>>(Cbf.write(intValue))!!.get())
        
        val doubleValue = AtomicReference(0.0)
        assertThrows<IllegalStateException> { Cbf.write(doubleValue) }
    }
    
}