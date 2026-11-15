package com.woalk.mods.woalklib.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class DrippingBlock(
    settings: Properties,
    private val particle: ParticleOptions = ParticleTypes.DRIPPING_DRIPSTONE_WATER
) : Block(settings) {
    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        drippingDisplayTick(particle, state, level, pos, random)
    }
}
