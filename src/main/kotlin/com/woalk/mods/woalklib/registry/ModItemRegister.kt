package com.woalk.mods.woalklib.registry

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item

abstract class ModItemRegister: ModRegister<ModItemRegister.PreItemRegister<out Item>, Item>(BuiltInRegistries.ITEM) {
    data class PreItemRegister <T: Item> (
        val item: T,
        val group: ResourceKey<CreativeModeTab>? = null,
    )

    protected open fun defaultItemSettings() = Item.Properties()

    @Suppress("UNCHECKED_CAST")
    fun <T : Item> register(
        id: String,
        itemCreator: (Item.Properties) -> T,
        itemSettings: Item.Properties = defaultItemSettings(),
        group: ResourceKey<CreativeModeTab>? = null,
    ): T = register(id, PreItemRegister(itemCreator(itemSettings.apply {
        setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(getNamespace(id), id)))
    }), group)).item as T

    @Suppress("UNCHECKED_CAST")
    fun <T : Item> register(
        key: ResourceKey<Item>,
        itemCreator: (Item.Properties) -> T,
        itemSettings: Item.Properties = defaultItemSettings(),
        group: ResourceKey<CreativeModeTab>? = null,
    ): T = register(key, PreItemRegister(itemCreator(itemSettings.apply {
        setId(key)
    }), group)).item as T

    override fun onRegister(toRegister: PreItemRegister<out Item>): Item = toRegister.item
}