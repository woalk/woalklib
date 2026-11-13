package com.woalk.mods.woalklib.registry

import net.minecraft.core.Registry
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import java.util.*

/**
 * The main base class for any register class that can register things in a vanilla [Registry].
 *
 * @param T The type of the objects that are queued for registration.
 * @param V The type of the objects in the registry. Can be the same as `T`.
 *
 * @param targetRegistry The vanilla registry that the objects of type `V` will be registered to.
 *
 * @see onRegister This method transforms queued objects of type `T` into registerable objects of type `V`.
 * @see registerAll This method starts the actual registration of queued elements into the registry.
 */
abstract class ModRegister<T, V: Any>(private val targetRegistry: Registry<V>) : IModRegister {
    private val tmpRegistry: MutableList<Pair<String, T>> = LinkedList<Pair<String, T>>()
    private val tmpRegistry2: MutableList<Pair<ResourceKey<V>, T>> = LinkedList<Pair<ResourceKey<V>, T>>()

    /**
     * Queue the given [toRegister] to be registered in the registry.
     *
     * @param id The path part of the [Identifier] to be used for registering.
     * The namespace will be taken from [getNamespace].
     */
    protected open fun register(id: String, toRegister: T): T {
        tmpRegistry.add(Pair(id, toRegister))
        return toRegister
    }

    /**
     * Queue the given [toRegister] to be registered in the registry.
     * The [key] will be taken as-is, [getNamespace] is **not** called to override it.
     */
    protected open fun register(key: ResourceKey<V>, toRegister: T): T {
        tmpRegistry2.add(Pair(key, toRegister))
        return toRegister
    }

    /**
     * Convert the given [toRegister] of type `T` into an object of type `V` that the [targetRegistry] can accept.
     *
     * If `T` and `V` are the same, you can simply return the input:
     * ```kotlin
     * override fun onRegister(toRegister: <Type>) = toRegister
     * ```
     */
    protected abstract fun onRegister(toRegister: T): V

    /**
     * The Mod ID of the mod that this register belongs to.
     * Will be used as default for [getNamespace] in case no other implementation is given.
     */
    protected abstract fun getModId(): String

    /**
     * The namespace to use for [register]`(String, T)`.
     * By default, calls [getModId].
     *
     * @param id The path for the identifier that will be assigned into the returned namespace.
     * This can be used to assign some items a different namespace based on string patterns.
     */
    protected open fun getNamespace(id: String): String = getModId()

    /**
     * Handle the actual registration of the given queued object.
     * This calls [onRegister] to convert the object into a registerable element,
     * and calls [getNamespace] to create the [Identifier] of the registered element.
     * If you have any additional registration logic to perform (e.g. adding a `BlockItem` to a `Block`), do it here.
     */
    protected open fun registerNow(id: String, toRegister: T) {
        Registry.register(targetRegistry, Identifier.fromNamespaceAndPath(getNamespace(id), id), onRegister(toRegister))
    }

    /**
     * Handle the actual registration of the given queued object.
     * This calls [onRegister] to convert the object into a registerable element.
     * The [key] is taken as-is.
     * If you have any additional registration logic to perform (e.g. adding a `BlockItem` to a `Block`), do it here.
     */
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