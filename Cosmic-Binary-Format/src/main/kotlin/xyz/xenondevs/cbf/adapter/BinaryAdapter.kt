package xyz.xenondevs.cbf.adapter

import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.lang.reflect.Type

interface BinaryAdapter<T> {
    
    fun write(obj: T, writer: ByteWriter)
    
    fun read(type: Type, reader: ByteReader): T
    
}