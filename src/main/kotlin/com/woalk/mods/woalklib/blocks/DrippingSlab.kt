package com.woalk.mods.woalklib.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState

class DrippingSlab(settings: BlockBehaviour.Properties) : SlabBlock(settings) {
    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        drippingDisplayTick(ParticleTypes.DRIPPING_DRIPSTONE_WATER, state, level, pos, random)
    }
}
