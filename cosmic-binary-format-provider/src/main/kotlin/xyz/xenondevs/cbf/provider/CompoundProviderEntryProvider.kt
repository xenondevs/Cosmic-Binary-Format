package xyz.xenondevs.cbf.provider

import xyz.xenondevs.cbf.Compound
import xyz.xenondevs.cbf.EntryWatcher
import xyz.xenondevs.cbf.WeakEntryWatcher
import xyz.xenondevs.commons.provider.AbstractProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutable.MutableProvider
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T : Any> Provider<Compound>.entry(key: String): MutableProvider<T?> {
    val provider = CompoundProviderEntryProvider<T>(this, key, typeOf<T>())
    addChild(provider)
    return provider
}

@PublishedApi
internal class CompoundProviderEntryProvider<T : Any>(
    private val parent: Provider<Compound>,
    private val key: String,
    private val type: KType
) : AbstractProvider<T?>() {
    
    private var compound: Compound? = null
    private val entryWatcher: WeakEntryWatcher<CompoundProviderEntryProvider<T>> = { provider, _, _ -> provider.update() }
    
    override fun loadValue(): T? {
        val prevCompound = compound
        val newCompound = parent.get()
        
        if (prevCompound != newCompound) {
            prevCompound?.removeWeakEntryWatchers(this)
            newCompound.addWeakEntryWatcher(this, key, entryWatcher)
            this.compound = newCompound
        }
        
        return newCompound.get(type, key)
    }
    
    override fun set(value: T?, updateChildren: Boolean, callSubscribers: Boolean, ignoredChildren: Set<Provider<*>>) {
        super.set(value, updateChildren, callSubscribers, ignoredChildren)
        
        val compound = parent.get()
        if (compound.get<T>(type, key) != value) {
            if (value == null) {
                parent.get().remove(key)
            } else {
                parent.get().set(type, key, value)
            }
        }
    }
    
}