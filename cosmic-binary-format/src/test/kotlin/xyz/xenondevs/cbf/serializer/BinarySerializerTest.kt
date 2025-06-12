package xyz.xenondevs.cbf.serializer

import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.reflect.typeOf

abstract class BinarySerializerTest<T : Any>(val serializer: BinarySerializer<T>) {
    
    fun serializeValue(value: T?): ByteArray {
        val out = ByteArrayOutputStream()
        val writer = ByteWriter.fromStream(out)
        serializer.write(value, writer)
        return out.toByteArray()
    }
    
    fun deserializeValue(bin: ByteArray): T? {
        val ins = ByteArrayInputStream(bin)
        val reader = ByteReader.fromStream(ins)
        return serializer.read(reader)
    }
    
    fun reserializeValue(value: T?): T? {
        return deserializeValue(serializeValue(value))
    }
    
    fun copyValue(value: T?): T? {
        return serializer.copy(value)
    }
    
    fun byteWriter(write: ByteWriter.() -> Unit): ByteArray {
        val out = ByteArrayOutputStream()
        val writer = ByteWriter.fromStream(out)
        writer.write()
        return out.toByteArray()
    }
    
}

abstract class BinarySerializerFactoryTest<T : Any>(val factory: BinarySerializerFactory) {
    
    inline fun <reified E : T> serializeValue(value: E?): ByteArray {
        val out = ByteArrayOutputStream()
        val writer = ByteWriter.fromStream(out)
        obtainSerializer<E>().write(value, writer)
        return out.toByteArray()
    }
    
    inline fun <reified E : T> deserializeValue(bin: ByteArray): E {
        val ins = ByteArrayInputStream(bin)
        val reader = ByteReader.fromStream(ins)
        return obtainSerializer<E>().read(reader) as E
    }
    
    inline fun <reified E : T> reserializeValue(value: E?): E? {
        return deserializeValue(serializeValue(value))
    }
    
    inline fun <reified E : T> copyValue(value: E?): E? {
        return obtainSerializer<E>().copy(value)
    }
    
    fun byteWriter(write: ByteWriter.() -> Unit): ByteArray {
        val out = ByteArrayOutputStream()
        val writer = ByteWriter.fromStream(out)
        writer.write()
        return out.toByteArray()
    }
    
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> obtainSerializer(): BinarySerializer<T> {
        return factory.create(typeOf<T>()) as BinarySerializer<T>
    }
    
}