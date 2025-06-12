package xyz.xenondevs.cbf

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import xyz.xenondevs.cbf.serializer.BinarySerializer
import java.util.concurrent.atomic.AtomicReference

class PlatformTypesSerializerSelectionTest {
    
    private class Foo1
    private class Foo2
    private class Foo3
    
    @Test
    fun `test retrieving non-platform type serializer after registering as platform type`() {
        val serializer = platformTypeSerializer(AtomicReference(Foo1())) // BinarySerializer<Foo1!>
        Cbf.registerSerializer(serializer)
        
        assertDoesNotThrow { Cbf.getSerializer<Foo1>() }
    }
    
    @Test
    fun `test retrieving platform type serializer after registering as platform type`() {
        val ref = AtomicReference(Foo2())
        val serializer = platformTypeSerializer(ref) // BinarySerializer<Foo2!>
        Cbf.registerSerializer(serializer)
        
        assertDoesNotThrow { Cbf.getSerializerLike(ref) }
    }
    
    @Test
    fun `test retrieving platform type serializer after registering as non-platform type`() {
        val serializer = object : BinarySerializer<Foo3> {
            override fun read(reader: ByteReader) = throw UnsupportedOperationException()
            override fun write(obj: Foo3?, writer: ByteWriter) = throw UnsupportedOperationException()
            override fun copy(obj: Foo3?) = throw UnsupportedOperationException()
        }
        Cbf.registerSerializer(serializer)
        
        assertDoesNotThrow { Cbf.getSerializerLike(AtomicReference(Foo3())) }
    }
    
    private fun <T : Any> platformTypeSerializer(atomicRef: AtomicReference<T>): BinarySerializer<T> {
        return object : BinarySerializer<T> {
            override fun read(reader: ByteReader) = throw UnsupportedOperationException()
            override fun write(obj: T?, writer: ByteWriter) = throw UnsupportedOperationException()
            override fun copy(obj: T?) = throw UnsupportedOperationException()
        }
    }
    
    private inline fun <reified T : Any> Cbf.getSerializerLike(atomicRef: AtomicReference<T>): BinarySerializer<T> {
        return Cbf.getSerializer<T>()
    }
    
}