package xyz.xenondevs.cbf

import kotlin.reflect.KType

/**
 * Requires opt-in:
 * This function cannot check whether the [KType] is correct, i.e. whether it matches the generic type of the function.
 * Before using a function annotated with this annotation, consider using a type-inferring inline function instead, if possible.
 */
@RequiresOptIn
@Target(AnnotationTarget.FUNCTION)
annotation class UncheckedApi