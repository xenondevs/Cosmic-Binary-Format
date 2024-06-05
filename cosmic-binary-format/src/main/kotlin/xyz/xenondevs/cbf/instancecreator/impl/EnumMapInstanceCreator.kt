package xyz.xenondevs.cbf.instancecreator.impl

import xyz.xenondevs.cbf.instancecreator.InstanceCreator
import xyz.xenondevs.commons.reflection.classifierClass
import xyz.xenondevs.commons.reflection.nonNullTypeArguments
import java.util.*
import kotlin.reflect.KType

internal object EnumMapInstanceCreator : InstanceCreator<EnumMap<*, *>> {
    
    override fun createInstance(type: KType): EnumMap<*, *> {
        val clazz = type.arguments[0].type!!.classifierClass!!.java
        return createEnumMap(clazz)
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun <E : Enum<E>> createEnumMap(clazz: Class<*>): EnumMap<*, *> {
        return EnumMap<E, Any>(clazz as Class<E>)
    }
    
}