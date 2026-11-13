package com.woalk.mods.woalklib.registry

import net.minecraft.world.item.BlockItem
import java.util.HashSet

/**
 * A holder object for all registered marker items
 * (usually invisible blocks that can be made visible by holding an instance of the block).
 */
object MarkerRegistry {
    /**
     * The [BlockItem]s corresponding to registered marker items.
     */
    val markerItems = HashSet<BlockItem>()
}