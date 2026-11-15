package com.woalk.mods.woalklib.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.sounds.AmbientLeavesBlockSoundPlayer
import net.minecraft.world.level.block.state.BlockState

class DrippingLeavesBlock(
    settings: Properties,
    private val particle: ParticleOptions = ParticleTypes.DRIPPING_DRIPSTONE_WATER,
    private val bound: Int = 5,
    private val ambientSound: AmbientLeavesBlockSoundPlayer = AmbientLeavesBlockSoundPlayer.noAmbientSound(),
) : LeavesBlock(ambientSound, settings) {
    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        drippingDisplayTick(particle, state, level, pos, random, bound)
    }
}
