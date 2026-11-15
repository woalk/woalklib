package com.woalk.mods.woalklib.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.util.RandomSource
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

/**
 * A [ReproducibleFlowerBlock] that can be placed on the ceiling, like a lantern.
 */
class HangingFlowerBlock(suspiciousStewEffect: Holder<MobEffect>, effectDuration: Float, settings: Properties) :
    ReproducibleFlowerBlock(suspiciousStewEffect, effectDuration, settings) {

    override fun mayPlaceOn(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean {
        return canSupportCenter(level as LevelReader, pos.above(), Direction.DOWN) && !level.isWaterAt(pos)
    }

    override fun updateShape(
        state: BlockState,
        level: LevelReader,
        ticks: ScheduledTickAccess,
        pos: BlockPos,
        directionToNeighbour: Direction,
        neighbourPos: BlockPos,
        neighbourState: BlockState,
        random: RandomSource
    ): BlockState {
        if (directionToNeighbour == Direction.UP && !this.mayPlaceOn(state, level, pos)) {
            return Blocks.AIR.defaultBlockState()
        }

        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
    }
}