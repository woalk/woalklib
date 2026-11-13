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

/**
 * A base class for classes that register [Block]s with the built-in `BLOCKS` registry,
 * including their [BlockItem]s, Creative Mode tabs and special behavior.
 */
abstract class ModBlockRegister : ModRegister<ModBlockRegister.BlockPreRegister<in Block>, Block>(BuiltInRegistries.BLOCK), IModClientRegister {
    /**
     * Represents a block queued for registration, with all information needed to perform the actual registration.
     *
     * @param block The [Block] instance.
     * @param item The optional [ItemPreRegister] containing information about how to handle this [block]'s [BlockItem].
     *             If `null`, no [BlockItem] is generated.
     * @param group The optional [CreativeModeTab] to add this block to. If `null`, the item will not be added to a tab.
     *              Only works if an [item] instance is provided.
     */
    data class BlockPreRegister <T: Block> (
        val block: T,
        val item: ItemPreRegister? = null,
        val group: ResourceKey<CreativeModeTab>? = null
    )

    /**
     * Represents registration information for [BlockItem]s.
     *
     * @param itemGenerator A function that's called to create a new instance of the [BlockItem].
     * @param itemPropertyGenerator A supplier to create the [Item.Properties] of the [BlockItem].
     *
     * @see BlockPreRegister.item
     */
    data class ItemPreRegister(
        val itemGenerator: (Block, Item.Properties) -> BlockItem,
        val itemPropertyGenerator: (() -> Item.Properties),
    )

    /**
     * Information about flammability of an item.
     *
     * @see flammable
     */
    protected data class FlammableInfo(
        val burnValue: Int? = 300,
        val burn: Int = 30,
        val spread: Int = 20
    )

    private val flammableBlocks = LinkedList<Pair<Block, FlammableInfo>>()
    private val foliageBlocks = LinkedList<Block>()
    private val markerBlocks = HashSet<Block>()
    private val groupedBlocks = HashMap<ResourceKey<CreativeModeTab>, MutableSet<Block>>()

    /**
     * A function that provides a fresh instance of the default [Item.Properties] that are used by this register.
     */
    protected open fun defaultItemSettings() = Item.Properties()

    /**
     * The default [Item.Properties] used by this register's convenience functions.
     * Do not modify this instance, as it will affect all following block registrations performed by this registry.
     *
     * @see defaultItemSettings
     */
    protected open var itemSettings = defaultItemSettings()

    /**
     * The default [Item.Properties] used by this register's [registerEpic] convenience functions,
     * setting the [BlockItem] as [Rarity.EPIC].
     * Do not modify this instance, as it will affect all following block registrations performed by this registry.
     *
     * @see defaultItemSettings
     */
    protected open var epicItemSettings = defaultItemSettings().rarity(Rarity.EPIC)

    /**
     * The default Creative Mode tab that [BlockItem]s in this registry are added to using the convenience functions.
     */
    protected open var defaultCreativeModeTab = LibWideCreativeTabs.woalkItemsTabKey

    /**
     * Backwards-compatible function for block registration.
     *
     * @param id The path for the block's [Identifier].
     * @param triple An older form of representation of a [BlockPreRegister] instance.
     */
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

    /**
     * Backwards-compatible function for block registration.
     *
     * @param key The [ResourceKey] to use as-is for the new block.
     * @param triple An older form of representation of a [BlockPreRegister] instance.
     */
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
    /**
     * Backwards-compatible function for block registration.
     *
     * @param id The path for the block's [Identifier].
     * @param pair An older form of representation for a [BlockPreRegister] instance, using a default [BlockItem].
     */
    protected fun register(
        id: String,
        pair: Pair<Block, (() -> Item.Properties)?>
    ) = register(id, Triple(pair.first, ::BlockItem, pair.second))

    /**
     * Backwards-compatible function for block registration.
     * This is the simplest way of registering a block.
     *
     * @param id The path for the block's [Identifier].
     * @param block The block instance to register.
     */
    protected fun register(id: String, block: Block): Block =
        register(id, Pair(block) { itemSettings }).block

    /**
     * Backwards-compatible function for block registration.
     *
     * @param id The path for the block's [Identifier].
     * @param block The block instance to register.
     * @param item A [BlockItem] constructor to tell which [BlockItem] subclass to use for this block.
     */
    protected fun register(id: String, block: Block, item: (Block, Item.Properties) -> BlockItem): Block =
        register(id, Triple(block, item) { itemSettings }).block

    /**
     * Backwards-compatible function for block registration.
     * The item will be marked as [Rarity.EPIC] using [epicItemSettings].
     *
     * @param id The path for the block's [Identifier].
     * @param block The block instance to register.
     */
    protected fun registerEpic(id: String, block: Block): Block =
        register(id, Pair(block) { epicItemSettings }).block

    /**
     * Backwards-compatible function for block registration.
     * The item will be marked as [Rarity.EPIC] using [epicItemSettings].
     *
     * @param id The path for the block's [Identifier].
     * @param block The block instance to register.
     * @param item A [BlockItem] constructor to tell which [BlockItem] subclass to use for this block.
     */
    protected fun registerEpic(id: String, block: Block, item: (Block, Item.Properties) -> BlockItem): Block =
        register(id, Triple(block, item) { epicItemSettings }).block

    /**
     * Backwards-compatible function for block registration.
     * The item will not be added to the [defaultCreativeModeTab] unless
     * [com.woalk.mods.woalklib.config.WoalkConfig.ignoreRestrictedItemRecommendations]
     * is set to `true`.
     *
     * @param id The path for the block's [Identifier].
     * @param block The block instance to register.
     */
    protected fun registerHidden(id: String, block: Block): Block =
        register(id, BlockPreRegister(
            block,
            ItemPreRegister(::BlockItem) { epicItemSettings },
            if (LibWideConfigReader.woalkConfig.ignoreRestrictedItemRecommendations) defaultCreativeModeTab else null
        )).block

    /**
     * Backwards-compatible function for block registration.
     * No [BlockItem] is registered for this block.
     *
     * @param id The path for the block's [Identifier].
     * @param block The block instance to register.
     */
    protected fun registerNoItem(id: String, block: Block): Block =
        register(id, Pair(block, null)).block

    /**
     * Backwards-compatible function for block registration.
     *
     * @param id The path for the block's [Identifier].
     * @param block The block instance to register.
     * @param itemSettings The [Item.Properties] to use for the [BlockItem].
     */
    protected fun register(id: String, block: Block, itemSettings: Item.Properties) =
        register(id, Pair(block) { itemSettings }).block
    //endregion

    //region Helper methods for special kinds of blocks
    /**
     * Mark this block as flammable, providing [info] about how much fuel it provides in a furnace.
     */
    protected fun flammable(block: Block, info: FlammableInfo = FlammableInfo()): Block {
        flammableBlocks.add(Pair(block, info))
        return block
    }

    /**
     * Mark a block as foliage, giving it a biome-specific tint similar to oak leaves.
     */
    protected fun foliage(block: Block): Block {
        foliageBlocks.add(block)
        return block
    }

    /**
     * Mark a block as having a marker item, i.e. it shows a particle when held where this block is placed in the world.
     *
     * @see MarkerRegistry
     */
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