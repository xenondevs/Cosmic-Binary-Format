package xyz.xenondevs.cbf.util

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Array
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import kotlin.reflect.KClass

inline fun <reified T> type(): Type = object : TypeToken<T>() {}.type

val Type.representedClass: Class<*>
    get() = when (this) {
        is ParameterizedType -> rawType as Class<*>
        is WildcardType -> upperBounds[0] as Class<*>
        is GenericArrayType -> Array.newInstance(genericComponentType.representedClass, 0)::class.java
        is Class<*> -> this
        else -> throw IllegalStateException("Type $this is not a class")
    }

val Type.representedKClass: KClass<*>
    get() = representedClass.kotlin