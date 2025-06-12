package xyz.xenondevs.cbf.serializer

import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.reflect.KType

/**
 * Reads a value of type [T] from [bytes].
 * 
 * @throws IllegalArgumentException if [strict] is true and the byte array contains more data than was read
 */
fun <T : Any> BinarySerializer<T>.read(bytes: ByteArray, strict: Boolean = true): T? {
    val inp = ByteArrayInputStream(bytes)
    val reader = ByteReader.fromStream(inp)
    val value = read(reader)
    if (strict && inp.available() > 0)
        throw IllegalArgumentException("Byte array contains more data than expected")
    return value
}

/**
 * Writes the given [obj] of type [T] to a [ByteArray].
 */
fun <T : Any> BinarySerializer<T>.write(obj: T?): ByteArray {
    val out = ByteArrayOutputStream()
    val writer = ByteWriter.fromStream(out)
    write(obj, writer)
    return out.toByteArray()
}

/**
 * A serializer for [T] to a binary format.
 */
interface BinarySerializer<T : Any> {
    
    /**
     * Reads a value of type [T?][T] from [reader].
     */
    fun read(reader: ByteReader): T?
    
    /**
     * Writes the given [obj] of type [T?][T] to [writer].
     */
    fun write(obj: T?, writer: ByteWriter)
    
    /**
     * Creates a deep copy of [obj].
     */
    fun copy(obj: T?): T?
    
}

/**
 * A factory for creating [BinarySerializer] instances based on a given [KType].
 */
interface BinarySerializerFactory {
    
    /**
     * Creates a [BinarySerializer] for the given [type], or null if this factory does not support the type.
     */
    fun create(type: KType): BinarySerializer<*>?
    
}

/**
 * A [BinarySerializer] for an unversioned format of [T], supporting nulls.
 * Every value is prefixed with an unsigned byte, where `0` indicates a null value and `1` indicates a non-null value.
 * 
 * This serializer can be swapped out with [VersionedBinarySerializer] to make format changes, without breaking compatibility.
 * The remaining values (`2-255`) can then be used to indicate different versions of the format.
 */
abstract class UnversionedBinarySerializer<T : Any> : BinarySerializer<T> {
    
    final override fun read(reader: ByteReader): T? {
        if (reader.readUnsignedByte() == 0U.toUByte())
            return null
        
        return readUnversioned(reader)
    }
    
    final override fun write(obj: T?, writer: ByteWriter) {
        if (obj == null) {
            writer.writeUnsignedByte(0U)
        } else {
            writer.writeUnsignedByte(1U)
            writeUnversioned(obj, writer)
        }
    }
    
    final override fun copy(obj: T?): T? {
        if (obj == null)
            return null
        
        return copyNonNull(obj)
    }
    
    /**
     * Reads a non-null value of type [T] from [reader].
     */
    abstract fun readUnversioned(reader: ByteReader): T
    
    /**
     * Writes the given non-null [obj] of type [T] to [writer].
     */
    abstract fun writeUnversioned(obj: T, writer: ByteWriter)
    
    /**
     * Creates a deep copy of the given non-null [obj].
     */
    abstract fun copyNonNull(obj: T): T
    
}

/**
 * A [BinarySerializer] for a versioned format of [T], supporting nulls.
 * Every value is prefixed with an unsigned byte, where `0` indicates a null value and any other value indicates a non-null value in a specific version.
 * A [VersionedBinarySerializer] can only write data in the current version, but can also read data serialized in previous versions.
 * 
 * @param currentVersion The current version of the format, i.e. the unsigned byte that will be written on serialization.
 */
abstract class VersionedBinarySerializer<T : Any>(
    private val currentVersion: UByte
) : BinarySerializer<T> {

    init {
        require(currentVersion > 0U) { "Version 0 is reserved for null values" }
    }

    final override fun write(obj: T?, writer: ByteWriter) {
        if (obj == null) {
            writer.writeUnsignedByte(0U)
        } else {
            writer.writeUnsignedByte(currentVersion)
            writeVersioned(obj, writer)
        }
    }

    final override fun read(reader: ByteReader): T? {
        val version = reader.readUnsignedByte()
        if (version == 0U.toUByte())
            return null

        return readVersioned(version, reader)
    }
    
    override fun copy(obj: T?): T? {
        if (obj == null)
            return null
        
        return copyNonNull(obj)
    }
    
    /**
     * Reads a non-null value of type [T] from [reader] in the specified [version].
     */
    abstract fun readVersioned(version: UByte, reader: ByteReader): T
    
    /**
     * Writes the given non-null [obj] of type [T] to [writer] in the current version format.
     */
    abstract fun writeVersioned(obj: T, writer: ByteWriter)
    
    /**
     * Creates a deep copy of the given non-null [obj].
     */
    abstract fun copyNonNull(obj: T): T
    
}