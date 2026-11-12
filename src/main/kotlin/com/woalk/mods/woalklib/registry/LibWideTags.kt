package com.woalk.mods.woalklib.registry

import com.woalk.mods.woalklib.WoalklibMod
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

object LibWideTags {
    object Blocks {
        val netherSoil = create("nether_soil")

        private fun create(path: String) =
            TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(WoalklibMod.MOD_ID, path))
    }
}