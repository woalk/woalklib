package com.woalk.mods.woalklib.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState

class DrippingStairs(baseBlockState: BlockState, settings: BlockBehaviour.Properties) : StairBlock(baseBlockState, settings) {
    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        drippingDisplayTick(ParticleTypes.DRIPPING_DRIPSTONE_WATER, state, level, pos, random)
    }
}
