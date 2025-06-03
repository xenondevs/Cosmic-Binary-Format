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
        
        override fun copyNonNull(value: AtomicReference<String>): AtomicReference<String> {
            return AtomicReference(value.get())
        }
        
        override fun writeUnversioned(obj: AtomicReference<String>, writer: ByteWriter) {
            writer.writeString(obj.get())
        }
        
        override fun readUnversioned(reader: ByteReader): AtomicReference<String> {
            return AtomicReference(reader.readString())
        }
        
    }
    
    private object AtomicReferenceIntBinarySerializer : UnversionedBinarySerializer<AtomicReference<Int>>() {
        
        override fun copyNonNull(value: AtomicReference<Int>): AtomicReference<Int> {
            return AtomicReference(value.get())
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
        CBF.registerSerializer(AtomicReferenceStringBinarySerializer)
        CBF.registerSerializer(AtomicReferenceIntBinarySerializer)
        
        val stringValue = AtomicReference("A")
        assertEquals(stringValue.get(), CBF.read<AtomicReference<String>>(CBF.write(stringValue))!!.get())
        
        val intValue = AtomicReference(0)
        assertEquals(intValue.get(), CBF.read<AtomicReference<Int>>(CBF.write(intValue))!!.get())
        
        val doubleValue = AtomicReference(0.0)
        assertThrows<IllegalStateException> { CBF.write(doubleValue) }
    }
    
}