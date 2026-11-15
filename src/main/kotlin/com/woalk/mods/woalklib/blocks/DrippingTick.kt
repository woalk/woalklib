package com.woalk.mods.woalklib.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

fun drippingDisplayTick(particleType: ParticleOptions, state: BlockState, world: Level, pos: BlockPos, random: RandomSource, bound: Int = 5) {
    if (random.nextInt(bound) != 0) {
        return
    }
    val direction = Direction.getRandom(random)
    if (direction == Direction.UP) {
        return
    }
    val blockPos = pos.offset(direction.unitVec3i)
    val blockState = world.getBlockState(blockPos)
    if (state.isSolid && blockState.isFaceSturdy(world, blockPos, direction.opposite)) {
        return;
    }
    val d = if (direction.unitVec3i.x == 0) random.nextDouble() else 0.5 + direction.unitVec3.x * 0.6
    val e = if (direction.unitVec3i.y == 0) random.nextDouble() else 0.5 + direction.unitVec3.y * 0.6
    val f = if (direction.unitVec3i.z == 0) random.nextDouble() else 0.5 + direction.unitVec3.z * 0.6
    world.addParticle(particleType, pos.x + d, pos.y + e, pos.z + f, 0.0, 0.0, 0.0);
}
