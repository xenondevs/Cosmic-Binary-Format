package xyz.xenondevs.cbf.adapter

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.Unpooled
import xyz.xenondevs.cbf.CBF
import kotlin.reflect.KType
import kotlin.reflect.typeOf

fun ByteBufAllocator.cbfBuffer() = NettyByteBuffer(buffer())

fun ByteBufAllocator.cbfBuffer(initialCapacity: Int) = NettyByteBuffer(buffer(initialCapacity))

inline fun <reified T> CBF.read(buf: ByteBuf): T? = read(typeOf<T>(), NettyByteBuffer(buf))

fun <T> CBF.read(type: KType, buf: ByteBuf): T? = read(type, NettyByteBuffer(buf))

fun CBF.write(obj: Any?, buf: ByteBuf) = write(obj, NettyByteBuffer(buf))

object NettyBufferProvider {
    
    fun getBuffer(size: Int) = NettyByteBuffer(Unpooled.buffer(size))
    
    fun getBuffer() = NettyByteBuffer(Unpooled.buffer())
    
    fun wrappedBuffer(data: ByteArray) = NettyByteBuffer(Unpooled.wrappedBuffer(data))
    
}