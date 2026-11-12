package com.woalk.mods.woalklib.blocks

import com.woalk.mods.woalklib.registry.LibWideTags
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState

class NetherFlowerBlock(suspiciousStewEffect: Holder<MobEffect>, effectDuration: Float, properties: Properties) :
    ReproducibleFlowerBlock(suspiciousStewEffect, effectDuration, properties) {
    override fun mayPlaceOn(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean {
        return super.mayPlaceOn(state, level, pos) || state.`is`(LibWideTags.Blocks.netherSoil)
    }
}