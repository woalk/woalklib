package com.woalk.mods.woalklib.registry

/**
 * Any class that can register things server-side during mod startup should implement this interface.
 */
interface IModRegister {
    /**
     * Register all elements provided by this class with the Minecraft server.
     */
    fun registerAll()
}