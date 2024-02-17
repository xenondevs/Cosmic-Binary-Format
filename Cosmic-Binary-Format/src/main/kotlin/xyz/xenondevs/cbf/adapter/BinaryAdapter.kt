package xyz.xenondevs.cbf.adapter

import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import kotlin.reflect.KType

interface BinaryAdapter<T : Any> {
    
    fun write(obj: T, type: KType, writer: ByteWriter)
    
    fun read(type: KType, reader: ByteReader): T
    
    fun copy(obj: T, type: KType): T
    
}

interface ComplexBinaryAdapter<T : Any> : BinaryAdapter<T> {
    
    override fun read(type: KType, reader: ByteReader): T =
        read(type, 1U, reader)
    
    fun read(type: KType, id: UByte, reader: ByteReader): T
    
}