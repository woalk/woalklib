package com.woalk.mods.woalklib.registry

/**
 * Any class that can register things client-side during mod startup should implement this interface.
 */
interface IModClientRegister {
    /**
     * Register all elements provided by this class with the Minecraft client.
     */
    fun registerAllClient()
}