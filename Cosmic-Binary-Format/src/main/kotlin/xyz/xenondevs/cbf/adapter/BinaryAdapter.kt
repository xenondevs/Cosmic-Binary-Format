package xyz.xenondevs.cbf.adapter

import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import kotlin.reflect.KType

interface BinaryAdapter<T : Any> {
    
    fun write(obj: T, type: KType, writer: ByteWriter)
    
    fun read(type: KType, reader: ByteReader): T
    
    fun copy(obj: T, type: KType): T
    
}