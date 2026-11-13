package com.woalk.mods.woalklib.registry

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item

/**
 * A base class for classes that register [Item]s into the built-in `ITEMS` registry.
 */
abstract class ModItemRegister: ModRegister<ModItemRegister.PreItemRegister<out Item>, Item>(BuiltInRegistries.ITEM) {
    /**
     * A representation of an [Item] that is to be registered.
     *
     * @param item The [Item] instance to register.
     * @param group The optional [CreativeModeTab] to add this item to.
     */
    data class PreItemRegister <T: Item> (
        val item: T,
        val group: ResourceKey<CreativeModeTab>? = null,
    )

    /**
     * Generate a new instance of the [Item.Properties] used as default for items registered in this class.
     */
    protected open fun defaultItemSettings() = Item.Properties()

    /**
     * Register the given item.
     *
     * This method properly sets the [ResourceKey] to the [Item.Properties] so that Minecraft does not crash.
     * In other `register` methods that do not explicitly say that they do, you will have to do it yourself.
     *
     * @param id The path of the [Identifier] to use. The namespace will be taken from [getNamespace].
     * @param itemCreator A function that can supply a new instance of the [Item] to be registered.
     * @param itemSettings A function that can supply the [Item.Properties] to use for this item.
     * @param group The optional [CreativeModeTab] to add this item to.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Item> register(
        id: String,
        itemCreator: (Item.Properties) -> T,
        itemSettings: Item.Properties = defaultItemSettings(),
        group: ResourceKey<CreativeModeTab>? = null,
    ): T = register(id, PreItemRegister(itemCreator(itemSettings.apply {
        setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(getNamespace(id), id)))
    }), group)).item as T

    /**
     * Register the given item.
     *
     * This method properly sets the [ResourceKey] to the [Item.Properties] so that Minecraft does not crash.
     * In other `register` methods that do not explicitly say that they do, you will have to do it yourself.
     *
     * @param key The [ResourceKey] to use for this item as-is.
     * @param itemCreator A function that can supply a new instance of the [Item] to be registered.
     * @param itemSettings A function that can supply the [Item.Properties] to use for this item.
     * @param group The optional [CreativeModeTab] to add this item to.
     */
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