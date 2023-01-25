package xyz.xenondevs.cbf.instancecreator

import kotlin.reflect.KType

interface InstanceCreator<T> {
    
    fun createInstance(type: KType): T
    
}