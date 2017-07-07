package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
class VanillaReedsSeed(override val seeds: ItemStack)
    : ISeedWrapper {

    override fun canPlantHere(world: World, pos: BlockPos): Boolean {
        val under = world.getBlockState(pos.down()).block
        return (under === Blocks.SAND || under === Blocks.DIRT) &&
                (world.getBlockState(pos.north().down()).block === Blocks.WATER
                || world.getBlockState(pos.east().down()).block === Blocks.WATER
                || world.getBlockState(pos.south().down()).block === Blocks.WATER
                || world.getBlockState(pos.west().down()).block === Blocks.WATER)
    }

    override fun plant(world: World, pos: BlockPos): IBlockState {
        return if (this.canPlantHere(world, pos)) {
            Blocks.REEDS.getPlant(world, pos)
        }
        else
            Blocks.AIR.defaultState
    }

    companion object {
        fun isSeed(stack: ItemStack)
            = !stack.isEmpty && (stack.item === Items.REEDS)
    }
}