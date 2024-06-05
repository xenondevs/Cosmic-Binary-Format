package xyz.xenondevs.cbf.adapter.impl

import xyz.xenondevs.cbf.CBF
import kotlin.reflect.KType

internal object ListBinaryAdapter : CollectionBinaryAdapter<List<*>>() {
    
    override fun createCollection(type: KType): MutableCollection<Any?> {
        return CBF.createInstance<MutableList<Any?>>(type) ?: ArrayList()
    }
    
}