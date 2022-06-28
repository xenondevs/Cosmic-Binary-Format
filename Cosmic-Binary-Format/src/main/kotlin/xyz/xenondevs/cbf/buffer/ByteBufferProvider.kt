package xyz.xenondevs.cbf.buffer

interface ByteBufferProvider {
    
    fun getBuffer(size: Int): ByteBuffer
    
    fun getBuffer(): ByteBuffer
    
    fun wrappedBuffer(data: ByteArray): ByteBuffer
    
}