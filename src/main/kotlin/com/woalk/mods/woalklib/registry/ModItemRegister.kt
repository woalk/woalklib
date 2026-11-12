package com.woalk.mods.woalklib.registry

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item

abstract class ModItemRegister: ModRegister<Item, Item>(BuiltInRegistries.ITEM) {
    protected open fun defaultItemSettings() = Item.Properties()

    @Suppress("UNCHECKED_CAST")
    fun <T : Item> registerTyped(
        name: String,
        itemCreator: (Item.Properties) -> T,
        itemSettings: Item.Properties = defaultItemSettings(),
        setId: Boolean = true
    ): T = register(name, itemCreator(itemSettings.apply {
        if (setId) {
            setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(getNamespace(name), name)))
        }
    })) as T

    override fun onRegister(toRegister: Item): Item = toRegister
}