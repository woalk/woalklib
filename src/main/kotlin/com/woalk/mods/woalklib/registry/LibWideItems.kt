package com.woalk.mods.woalklib.registry

import com.woalk.mods.woalklib.WoalklibMod
import net.minecraft.world.item.Item

/**
 * Items provided by WoalkLib for use in all Woalk mods.
 */
object LibWideItems: ModItemRegister() {
    /**
     * A simple item without usage that has the Woalk Logo as texture.
     * For use as the icon in [LibWideCreativeTabs.woalkItemsTab].
     */
    val logoItem = register("logo", ::Item)

    override fun getModId(): String = WoalklibMod.MOD_ID
}