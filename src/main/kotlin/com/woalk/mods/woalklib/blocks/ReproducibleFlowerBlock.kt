package com.woalk.mods.woalklib.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.BonemealSource
import net.minecraft.world.level.block.BonemealableBlock
import net.minecraft.world.level.block.FlowerBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

open class ReproducibleFlowerBlock(suspiciousStewEffect: Holder<MobEffect>, effectDuration: Float, settings: Properties) :
    FlowerBlock(suspiciousStewEffect, effectDuration, settings), BonemealableBlock {
    private val shape = Shapes.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0)

    override fun getVisualShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        val offset = state.getOffset(pos)
        return shape.move(offset)
    }

    override fun isValidBonemealTarget(
        level: LevelReader,
        pos: BlockPos,
        state: BlockState,
        source: BonemealSource
    ): Boolean = true

    override fun isBonemealSuccess(
        level: Level,
        random: RandomSource,
        pos: BlockPos,
        state: BlockState,
        source: BonemealSource
    ): Boolean = true

    override fun performBonemeal(
        level: ServerLevel,
        random: RandomSource,
        pos: BlockPos,
        state: BlockState,
        source: BonemealSource
    ) {
        return popResource(level, pos, ItemStack(this))
    }
}
