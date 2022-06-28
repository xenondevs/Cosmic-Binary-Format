package xyz.xenondevs.cbf.adapter

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.Unpooled
import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.buffer.ByteBufferProvider
import xyz.xenondevs.cbf.util.type
import java.lang.reflect.Type

fun ByteBufAllocator.cbfBuffer() = NettyByteBuffer(buffer())

fun ByteBufAllocator.cbfBuffer(initialCapacity: Int) = NettyByteBuffer(buffer(initialCapacity))

inline fun <reified T> CBF.read(buf: ByteBuf): T? = read(type<T>(), NettyByteBuffer(buf))

fun <T> CBF.read(type: Type, buf: ByteBuf): T? = read(type, NettyByteBuffer(buf))

fun CBF.write(obj: Any?, buf: ByteBuf) = write(obj, NettyByteBuffer(buf))

object NettyBufferProvider : ByteBufferProvider {
    
    override fun getBuffer(size: Int) = NettyByteBuffer(Unpooled.buffer(size))
    
    override fun getBuffer() = NettyByteBuffer(Unpooled.buffer())
    
    override fun wrappedBuffer(data: ByteArray) = NettyByteBuffer(Unpooled.wrappedBuffer(data))
    
}