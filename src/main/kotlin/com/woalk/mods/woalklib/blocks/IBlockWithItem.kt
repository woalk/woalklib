package com.woalk.mods.woalklib.blocks

import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block

/**
 * An interface to save the corresponding [BlockItem] to a [Block].
 * Implement in a [Block] subclass if you need access to the [BlockItem] in its methods.
 *
 * Helpful for blocks that do something special when interacted with while holding another instance of the block,
 * like layered blocks.
 */
interface IBlockWithItem {
    /**
     * The [BlockItem] corresponding to this [Block].
     */
    var item: BlockItem
}