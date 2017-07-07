package net.ndrei.teslapoweredthingies.machines.treefarm

import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
interface ITreeSaplingWrapper {
    fun canPlant(world: World, pos: BlockPos): Boolean
    fun plant(world: World, pos: BlockPos): Int

    val stack: ItemStack
}