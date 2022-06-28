package xyz.xenondevs.cbf.adapter

import xyz.xenondevs.cbf.buffer.ByteBuffer
import java.lang.reflect.Type

interface BinaryAdapter<T> {
    
    fun write(obj: T, buf: ByteBuffer)
    
    fun read(type: Type, buf: ByteBuffer): T
    
}