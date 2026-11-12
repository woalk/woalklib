package com.woalk.mods.woalklib.registry

import net.minecraft.core.Registry
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import java.util.*

abstract class ModRegister<T, V: Any>(private val targetRegistry: Registry<V>) : IModRegister {
    private val tmpRegistry: MutableList<Pair<String, T>> = LinkedList<Pair<String, T>>()
    private val tmpRegistry2: MutableList<Pair<ResourceKey<V>, T>> = LinkedList<Pair<ResourceKey<V>, T>>()

    protected open fun register(id: String, toRegister: T): T {
        tmpRegistry.add(Pair(id, toRegister))
        return toRegister
    }

    protected open fun register(key: ResourceKey<V>, toRegister: T): T {
        tmpRegistry2.add(Pair(key, toRegister))
        return toRegister
    }

    protected abstract fun onRegister(toRegister: T): V

    protected abstract fun getModId(): String

    protected open fun getNamespace(id: String): String = getModId()

    protected open fun registerNow(id: String, toRegister: T) {
        Registry.register(targetRegistry, Identifier.fromNamespaceAndPath(getNamespace(id), id), onRegister(toRegister))
    }

    protected open fun registerNow(key: ResourceKey<V>, toRegister: T) {
        Registry.register(targetRegistry, key, onRegister(toRegister))
    }

    override fun registerAll() {
        for ((id, toRegister) in tmpRegistry) {
            registerNow(id, toRegister)
        }
        tmpRegistry.clear()

        for ((id, toRegister) in tmpRegistry2) {
            registerNow(id, toRegister)
        }
        tmpRegistry2.clear()
    }
}