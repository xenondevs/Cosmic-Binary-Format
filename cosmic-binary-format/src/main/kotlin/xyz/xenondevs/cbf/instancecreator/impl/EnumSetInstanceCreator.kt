package xyz.xenondevs.cbf.instancecreator.impl

import xyz.xenondevs.cbf.instancecreator.InstanceCreator
import xyz.xenondevs.commons.reflection.classifierClass
import java.util.*
import kotlin.reflect.KType

internal object EnumSetInstanceCreator : InstanceCreator<EnumSet<*>> {
    
    override fun createInstance(type: KType): EnumSet<*> {
        val clazz = type.arguments[0].type!!.classifierClass!!.java
        return createEnumSet(clazz)
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun <E : Enum<E>> createEnumSet(clazz: Class<*>): EnumSet<*> {
        return EnumSet.noneOf(clazz as Class<E>)
    }
    
}