package xyz.xenondevs.cbf.util

@PublishedApi
internal object Assertions {
    @JvmField
    @PublishedApi
    internal val ENABLED: Boolean = javaClass.desiredAssertionStatus()
}

internal inline fun debugRequire(requirement: () -> Boolean, message: () -> String) {
    if (Assertions.ENABLED && !requirement()) throw IllegalArgumentException(message())
}
