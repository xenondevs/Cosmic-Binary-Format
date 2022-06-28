package xyz.xenondevs.cbf.instancecreator

import java.lang.reflect.Type

interface InstanceCreator<T> {
    
    fun createInstance(type: Type): T
    
}