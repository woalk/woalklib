package com.woalk.mods.woalklib.registry

import com.woalk.mods.woalklib.WoalklibMod
import net.minecraft.world.item.Item

object LibWideItems: ModItemRegister() {
    val logoItem = register("logo", ::Item)

    override fun getModId(): String = WoalklibMod.MOD_ID
}