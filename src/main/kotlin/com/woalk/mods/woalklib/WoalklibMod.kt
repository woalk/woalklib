package com.woalk.mods.woalklib

import com.woalk.mods.woalklib.registry.LibWideCreativeTabs
import com.woalk.mods.woalklib.registry.LibWideItems
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.Identifier
import org.slf4j.LoggerFactory


class WoalklibMod : ModInitializer {
    companion object {
        const val MOD_ID = "woalklib"
        val LOGGER = LoggerFactory.getLogger(MOD_ID)!!

        fun id(path: String): Identifier = Identifier.fromNamespaceAndPath(MOD_ID, path)
    }

    override fun onInitialize() {
        LibWideItems.registerAll()
        LibWideCreativeTabs.registerAll()
    }
}
