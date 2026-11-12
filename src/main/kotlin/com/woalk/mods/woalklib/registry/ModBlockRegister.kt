package com.woalk.mods.woalklib.registry

import com.woalk.mods.woalklib.blocks.IBlockWithItem
import com.woalk.mods.woalklib.config.LibWideConfigReader
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry
import net.minecraft.client.color.block.BlockTintSources
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.block.Block
import java.util.*

abstract class ModBlockRegister : ModRegister<ModBlockRegister.BlockPreRegister<in Block>, Block>(BuiltInRegistries.BLOCK), IModClientRegister {
    data class BlockPreRegister <T: Block> (
        val block: T,
        val item: ItemPreRegister? = null,
        val group: ResourceKey<CreativeModeTab>? = null
    )
    data class ItemPreRegister(
        val itemGenerator: (Block, Item.Properties) -> BlockItem,
        val itemPropertyGenerator: (() -> Item.Properties),
    )

    protected data class FlammableInfo(
        val burnValue: Int? = 300,
        val burn: Int = 30,
        val spread: Int = 20
    )

    private val FLAMMABLE_BLOCKS = LinkedList<Pair<Block, FlammableInfo>>()
    private val FOLIAGE_BLOCKS = LinkedList<Block>()
    private val MARKER_BLOCKS = HashSet<Block>()

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

    protected fun flammable(block: Block, info: FlammableInfo = FlammableInfo()): Block {
        FLAMMABLE_BLOCKS.add(Pair(block, info))
        return block
    }

    protected fun foliage(block: Block): Block {
        FOLIAGE_BLOCKS.add(block)
        return block
    }

    protected fun marker(block: Block): Block {
        MARKER_BLOCKS.add(block)
        return block
    }

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
            if (MARKER_BLOCKS.contains(toRegister.block)) {
                MarkerRegistry.MARKER_ITEMS.add(item)
            }
            if (toRegister.block is IBlockWithItem) {
                toRegister.block.item = item
            }
        }
    }

    override fun registerAll() {
        super.registerAll()
        for ((block, info) in FLAMMABLE_BLOCKS) {
            FlammableBlockRegistry.getDefaultInstance().add(block, info.burn, info.spread)
        }
        MARKER_BLOCKS.clear()
    }

    override fun registerAllClient() {
        BlockColorRegistry.register(listOf(BlockTintSources.foliage()), *FOLIAGE_BLOCKS.toTypedArray())
        FOLIAGE_BLOCKS.clear()
    }
}