package com.woalk.mods.woalklib.registry

import com.woalk.mods.woalklib.WoalklibMod
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab

/**
 * This class provides Creative Mode tabs for usage of all Woalk mods.
 */
object LibWideCreativeTabs: ModRegister<CreativeModeTab, CreativeModeTab>(BuiltInRegistries.CREATIVE_MODE_TAB) {
    /**
     * The resource key for the [woalkItemsTab].
     */
    val woalkItemsTabKey = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), WoalklibMod.id("tab"))

    /**
     * The "Woalk's Items" tab, using a Woalk logo as its icon.
     */
    val woalkItemsTab = register(woalkItemsTabKey, FabricCreativeModeTab.builder()
        .title(Component.translatable("creativeTab.woalklib"))
        .icon { LibWideItems.logoItem.defaultInstance }
        .build()
    )

    override fun getModId() = WoalklibMod.MOD_ID
    override fun onRegister(toRegister: CreativeModeTab): CreativeModeTab = toRegister
}