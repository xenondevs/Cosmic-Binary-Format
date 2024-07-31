package xyz.xenondevs.cbf.provider

import xyz.xenondevs.cbf.Compound
import xyz.xenondevs.commons.provider.AbstractProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutable.MutableProvider
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T : Any> Compound.entry(key: String): MutableProvider<T?> {
    return CompoundEntryProvider(this, key, typeOf<T>())
}

@PublishedApi
internal class CompoundEntryProvider<T : Any>(
    private val compound: Compound,
    private val key: String,
    private val type: KType,
) : AbstractProvider<T?>() {
    
    init {
        compound.addWeakEntryWatcher(this, key) { provider, _, _ -> provider.update() }
    }
    
    override fun loadValue(): T? {
        return compound.get(type, key)
    }
    
    override fun set(value: T?, ignoredChildren: Set<Provider<*>>) {
        super.set(value, ignoredChildren)
        
        if (compound.get<T>(type, key) != value) {
            if (value == null) {
                compound.remove(key)
            } else {
                compound.set(type, key, value)
            }
        }
    }
    
}