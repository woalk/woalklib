package com.woalk.mods.woalklib.registry

import com.woalk.mods.woalklib.WoalklibMod
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey

/**
 * Tags for use in all of Woalk's mods.
 */
object LibWideTags {
    /**
     * Block tags for use in all of Woalk's mods.
     */
    object Blocks {
        /**
         * Contains all items that are considered Nether soil.
         */
        val netherSoil = create("nether_soil")

        /**
         * Create a [TagKey] with Identifier `block/woalklib:`[path].
         */
        private fun create(path: String) =
            TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(WoalklibMod.MOD_ID, path))
    }
}