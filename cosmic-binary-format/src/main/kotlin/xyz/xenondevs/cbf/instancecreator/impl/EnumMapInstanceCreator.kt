package xyz.xenondevs.cbf.instancecreator.impl

import xyz.xenondevs.cbf.instancecreator.InstanceCreator
import xyz.xenondevs.commons.reflection.classifierClass
import java.util.*
import kotlin.reflect.KType

internal object EnumMapInstanceCreator : InstanceCreator<EnumMap<*, *>> {
    
    private val ENUM_MAP_CONSTRUCTOR = EnumMap::class.java.getConstructor(Class::class.java)
    
    override fun createInstance(type: KType): EnumMap<*, *> {
        return ENUM_MAP_CONSTRUCTOR.newInstance(type.arguments[0].type!!.classifierClass!!.java)
    }
    
}