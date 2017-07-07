package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
interface ISeedWrapper {
    val seeds: ItemStack

    fun canPlantHere(world: World, pos: BlockPos): Boolean
    fun plant(world: World, pos: BlockPos): IBlockState
}