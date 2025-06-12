package xyz.xenondevs.cbf

import xyz.xenondevs.cbf.serializer.BinarySerializer
import kotlin.reflect.KType

/**
 * The security manager is responsible for controlling the creation of serializers.
 *
 * For example, in environments with a plugin system, the security manager can be used to prevent plugins from registering serializers
 * for third-party types, preventing multiple serializers with different formats from existing.
 */
interface CbfSecurityManager {
    
    /**
     * Checks whether the given [serializer] for [type] is allowed to be registered.
     */
    fun <T : Any> isAllowed(type: KType, serializer: BinarySerializer<T>): Boolean
    
}

/**
 * An exception that is thrown when an action is prevented by the [CbfSecurityManager].
 */
class CbfSecurityException internal constructor(type: KType, any: Any) : RuntimeException(
    "Registration of $any for $type prevented by CbfSecurityManager"
)