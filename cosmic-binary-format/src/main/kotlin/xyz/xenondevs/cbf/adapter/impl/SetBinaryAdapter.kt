package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.cbf.instancecreator.impl.EnumSetInstanceCreator
import xyz.xenondevs.commons.reflection.nonNullTypeArguments
import java.util.*
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

internal object SetBinaryAdapter : CollectionBinaryAdapter<Set<*>>() {
    
    @Suppress("UNCHECKED_CAST")
    override fun createCollection(type: KType): MutableCollection<Any?> {
        val set = CBF.createInstance<MutableSet<Any?>>(type)
        if (set != null)
            return set
        
        if (type.nonNullTypeArguments[0].isSubtypeOf(typeOf<Enum<*>>()))
            return EnumSetInstanceCreator.createInstance(type) as MutableCollection<Any?>
        
        return HashSet()
    }
    
}