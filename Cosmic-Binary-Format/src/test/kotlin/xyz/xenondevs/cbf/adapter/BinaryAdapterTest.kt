package xyz.xenondevs.cbf.adapter

import xyz.xenondevs.cbf.io.ByteReader
import xyz.xenondevs.cbf.io.ByteWriter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.reflect.typeOf

abstract class BinaryAdapterTest<T : Any>(val adapter: BinaryAdapter<T>) {
    
    inline fun <reified E : T> reserializeValue(value: E): E {
        val type = typeOf<E>()
        
        val out = ByteArrayOutputStream()
        val writer = ByteWriter.fromStream(out)
        adapter.write(value, type, writer)
        
        val ins = ByteArrayInputStream(out.toByteArray())
        val reader = ByteReader.fromStream(ins)
        
        return adapter.read(type, reader) as E
    }
    
    inline fun <reified E : T> copyValue(value: E): E {
        val type = typeOf<E>()
        return adapter.copy(value, type) as E
    }
    
}