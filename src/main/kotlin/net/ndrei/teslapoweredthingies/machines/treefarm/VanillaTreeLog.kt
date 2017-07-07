package net.ndrei.teslapoweredthingies.machines.treefarm

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
class VanillaTreeLog internal constructor(world: World, pos: BlockPos)
    : VanillaTreeBlock(world, pos), ITreeLogWrapper