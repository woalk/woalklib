package com.woalk.mods.woalklib.registry

import com.woalk.mods.woalklib.blocks.IBlockWithItem
import com.woalk.mods.woalklib.config.LibWideConfigReader
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry
import net.minecraft.client.color.block.BlockTintSources
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.references.BlockItemId
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

    private val flammableBlocks = LinkedList<Pair<Block, FlammableInfo>>()
    private val foliageBlocks = LinkedList<Block>()
    private val markerBlocks = HashSet<Block>()
    private val groupedBlocks = HashMap<ResourceKey<CreativeModeTab>, MutableSet<Block>>()

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
        key: ResourceKey<Block>,
        triple: Triple<Block, ((Block, Item.Properties) -> BlockItem)?, (() -> Item.Properties)?>
    ) = register(
        key,
        BlockPreRegister(
            triple.first,
            if (triple.second != null && triple.third != null) ItemPreRegister(
                triple.second!!,
                triple.third!!,
            ) else null,
            defaultCreativeModeTab,
        ),
    )

    //region Helper methods for porting compatibility
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
    //endregion

    //region Helper methods for special kinds of blocks
    protected fun flammable(block: Block, info: FlammableInfo = FlammableInfo()): Block {
        flammableBlocks.add(Pair(block, info))
        return block
    }

    protected fun foliage(block: Block): Block {
        foliageBlocks.add(block)
        return block
    }

    protected fun marker(block: Block): Block {
        markerBlocks.add(block)
        return block
    }
    //endregion

    override fun registerNow(
        id: String,
        toRegister: BlockPreRegister<in Block>
    ) {
        val identifier = Identifier.fromNamespaceAndPath(getNamespace(id), id)
        val blockItemId = BlockItemId(
            ResourceKey.create(Registries.BLOCK, identifier),
            ResourceKey.create(Registries.ITEM, identifier),
        )
        toRegister.block.properties().setId(blockItemId.block)
        super.registerNow(id, toRegister)
        if (toRegister.item != null) {
            val item = toRegister.item.itemGenerator.invoke(toRegister.block, toRegister.item.itemPropertyGenerator().apply {
                setId(blockItemId.item)
            })
            Registry.register(
                BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(getNamespace(id), id), item
            )
            if (toRegister.group != null) {
                val groupSet = groupedBlocks.getOrPut(toRegister.group, ::HashSet)
                groupSet.add(toRegister.block)
            }
            if (markerBlocks.contains(toRegister.block)) {
                MarkerRegistry.markerItems.add(item)
            }
            if (toRegister.block is IBlockWithItem) {
                toRegister.block.item = item
            }
        }
    }

    override fun onRegister(toRegister: BlockPreRegister<in Block>): Block = toRegister.block

    override fun registerAll() {
        super.registerAll()
        for ((block, info) in flammableBlocks) {
            FlammableBlockRegistry.getDefaultInstance().add(block, info.burn, info.spread)
        }
        for ((group, blocks) in groupedBlocks) {
            CreativeModeTabEvents.modifyOutputEvent(group).register {
                for (block in blocks) {
                    it.accept(block)
                }
            }
        }
        markerBlocks.clear()
    }

    override fun registerAllClient() {
        BlockColorRegistry.register(listOf(BlockTintSources.foliage()), *foliageBlocks.toTypedArray())
        foliageBlocks.clear()
    }
}