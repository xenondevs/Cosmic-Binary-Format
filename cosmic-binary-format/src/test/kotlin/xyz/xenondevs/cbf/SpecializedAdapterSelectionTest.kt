package xyz.xenondevs.cbf

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KType
import kotlin.test.assertEquals

class SpecializedAdapterSelectionTest {
    
    private object AtomicReferenceStringBinaryAdapter : BinaryAdapter<AtomicReference<String>> {
        
        override fun write(obj: AtomicReference<String>, type: KType, writer: ByteWriter) {
            writer.writeString(obj.get())
        }
        
        override fun read(type: KType, reader: ByteReader): AtomicReference<String> {
            return AtomicReference(reader.readString())
        }
        
        override fun copy(obj: AtomicReference<String>, type: KType): AtomicReference<String> {
            return AtomicReference(obj.get())
        }
        
    }
    
    private object AtomicReferenceIntBinaryAdapter : BinaryAdapter<AtomicReference<Int>> {
        
        override fun write(obj: AtomicReference<Int>, type: KType, writer: ByteWriter) {
            writer.writeInt(obj.get())
        }
        
        override fun read(type: KType, reader: ByteReader): AtomicReference<Int> {
            return AtomicReference(reader.readInt())
        }
        
        override fun copy(obj: AtomicReference<Int>, type: KType): AtomicReference<Int> {
            return AtomicReference(obj.get())
        }
        
    }
    
    @Test
    fun testSpecializedAdapterSelection() {
        CBF.registerBinaryAdapter(AtomicReferenceStringBinaryAdapter)
        CBF.registerBinaryAdapter(AtomicReferenceIntBinaryAdapter)
        
        val stringValue = AtomicReference("A")
        assertEquals(stringValue.get(), CBF.read<AtomicReference<String>>(CBF.write(stringValue))!!.get())
        
        val intValue = AtomicReference(0)
        assertEquals(intValue.get(), CBF.read<AtomicReference<Int>>(CBF.write(intValue))!!.get())
        
        val doubleValue = AtomicReference(0.0)
        assertThrows<IllegalStateException> { CBF.write(doubleValue) }
    }
    
}