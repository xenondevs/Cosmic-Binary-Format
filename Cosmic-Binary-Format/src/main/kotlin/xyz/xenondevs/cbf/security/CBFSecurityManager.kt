package xyz.xenondevs.cbf.security

import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.cbf.instancecreator.InstanceCreator
import kotlin.reflect.KClass

interface CBFSecurityManager {
    
    fun <T : Any> canRegisterAdapter(clazz: KClass<T>, adapter: BinaryAdapter<T>): Boolean
    
    fun <T : Any> canRegisterHierarchyAdapter(clazz: KClass<T>, adapter: BinaryAdapter<T>): Boolean
    
    fun <T : Any> canRegisterInstanceCreator(clazz: KClass<T>, instanceCreator: InstanceCreator<T>): Boolean
    
}

class CBFSecurityException : RuntimeException("Action prevented by CBFSecurityManager")