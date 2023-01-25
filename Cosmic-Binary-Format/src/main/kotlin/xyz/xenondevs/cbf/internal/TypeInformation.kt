package xyz.xenondevs.cbf.internal

import kotlin.reflect.KType

internal val KType.nonNullTypeArguments: List<KType>
    get() = arguments.map { it.type ?: throw TypeInformationException.starProjection(this) }

internal class TypeInformationException(message: String?) : Exception(message) {
    
    companion object {
        
        fun starProjection(type: KType): TypeInformationException {
            val nullArguments = type.arguments.asSequence()
                .withIndex()
                .filter { it.value.type == null }
                .map { it.index }
                .joinToString()
            
            return TypeInformationException("Type $type has a star project as type argument at: ${nullArguments}.")
        }
        
    }
    
}