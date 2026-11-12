package com.woalk.mods.woalklib.registry

import com.woalk.mods.woalklib.config.LibWideConfigReader
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.block.Block

abstract class ModBlockRegister : ModRegister<ModBlockRegister.BlockPreRegister<in Block>, Block>(BuiltInRegistries.BLOCK) {
    data class BlockPreRegister <T: Block> (
        val block: T,
        val item: ItemPreRegister? = null,
        val group: ResourceKey<CreativeModeTab>? = null
    )
    data class ItemPreRegister(
        val itemGenerator: (Block, Item.Properties) -> BlockItem,
        val itemPropertyGenerator: (() -> Item.Properties),
    )

    protected open fun defaultItemSettings() = Item.Properties()
    protected open var itemSettings = defaultItemSettings()
    protected open var epicItemSettings = defaultItemSettings().rarity(Rarity.EPIC)

    protected open var defaultCreativeModeTab = LibWideCreativeTabs.woalkItemsTabKey

    protected fun register(
        id: String,
        triple: Triple<Block, ((Block, Item.Properties) -> BlockItem)?, (() -> Item.Properties)?>
    ) = register(
        id,
        BlockPreRegister(
            triple.first,
            if (triple.second != null && triple.third != null) ItemPreRegister(
                triple.second!!,
                triple.third!!,
            ) else null,
            defaultCreativeModeTab,
        ),
    )

    protected fun register(
        id: String,
        pair: Pair<Block, (() -> Item.Properties)?>
    ) = register(id, Triple(pair.first, ::BlockItem, pair.second))

    protected fun register(id: String, block: Block): Block =
        register(id, Pair(block) { itemSettings }).block

    protected fun register(id: String, block: Block, item: (Block, Item.Properties) -> BlockItem): Block =
        register(id, Triple(block, item) { itemSettings }).block

    protected fun registerEpic(id: String, block: Block): Block =
        register(id, Pair(block) { epicItemSettings }).block

    protected fun registerEpic(id: String, block: Block, item: (Block, Item.Properties) -> BlockItem): Block =
        register(id, Triple(block, item) { epicItemSettings }).block

    protected fun registerHidden(id: String, block: Block): Block =
        register(id, BlockPreRegister(
            block,
            ItemPreRegister(::BlockItem) { epicItemSettings },
            if (LibWideConfigReader.woalkConfig.ignoreRestrictedItemRecommendations) defaultCreativeModeTab else null
        )).block

    protected fun registerNoItem(id: String, block: Block): Block =
        register(id, Pair(block, null)).block

    protected fun register(id: String, block: Block, itemSettings: Item.Properties) =
        register(id, Pair(block) { itemSettings }).block

    override fun registerNow(
        id: String,
        toRegister: BlockPreRegister<in Block>
    ) {
        super.registerNow(id, toRegister)
        var item: BlockItem? = null
        if (toRegister.item != null) {
            item = toRegister.item.itemGenerator.invoke(toRegister.block, toRegister.item.itemPropertyGenerator.invoke())
            Registry.register(
                BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(getNamespace(id), id), item
            )
//            if (MARKER_BLOCKS.contains(toRegister.first)) {
//                MARKER_ITEMS.add(item)
//            }
//            if (toRegister.block is IBlockWithItem) {
//                (toRegister.block as IBlockWithItem).setItem(item)
//            }
        }
//        for (pair in FOLIAGE_BLOCKS) {
//            if (pair.left == toRegister.block && item != null) {
//                pair.right = item
//            }
//        }
    }

    override fun registerAll() {
        super.registerAll()
//        MARKER_BLOCKS.clear()
    }
}