package xyz.xenondevs.cbf.adapter

import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import kotlin.reflect.KType

/**
 * Serializer implementation for [T].
 */
interface BinaryAdapter<T : Any> {
    
    /**
     * Writes [obj] to [writer] with additional [type] information.
     */
    fun write(obj: T, type: KType, writer: ByteWriter)
    
    /**
     * Reads [T] from [reader] with addition [type] information.
     * May throw an exception if the object could not be read.
     */
    fun read(type: KType, reader: ByteReader): T
    
    /**
     * Performs a deep copy of [obj] with additional [type] information.
     */
    fun copy(obj: T, type: KType): T
    
}

/**
 * Serializer implementation with the ability to serialize into different formats based on an ID
 * specified by an unsigned byte.
 * 
 * This [UByte] does not need to be read from the [ByteReader] as it is passed to the [read] function,
 * but does need to be written to the [ByteWriter] in [ComplexBinaryAdapter.write].
 */
interface ComplexBinaryAdapter<T : Any> : BinaryAdapter<T> {
    
    override fun read(type: KType, reader: ByteReader): T =
        read(type, 1U, reader)
    
    /**
     * Reads [T] from [reader] with additional [type] information and an [id] to determine the format.
     */
    fun read(type: KType, id: UByte, reader: ByteReader): T
    
}