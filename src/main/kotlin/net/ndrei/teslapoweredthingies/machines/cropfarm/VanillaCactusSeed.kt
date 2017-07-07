package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
class VanillaCactusSeed(override val seeds: ItemStack)
    : ISeedWrapper {

    override fun canPlantHere(world: World, pos: BlockPos): Boolean {
        return world.getBlockState(pos.down()).block === Blocks.SAND
                && world.isAirBlock(pos.south())
                && world.isAirBlock(pos.east())
                && world.isAirBlock(pos.north())
                && world.isAirBlock(pos.west())
    }

    override fun plant(world: World, pos: BlockPos): IBlockState {
        return if (this.canPlantHere(world, pos)) {
            Blocks.CACTUS.getPlant(world, pos)
        }
        else Blocks.AIR.defaultState
    }

    companion object {
        fun isSeed(stack: ItemStack)
            = !stack.isEmpty && (stack.item === Item.getItemFromBlock(Blocks.CACTUS))
    }
}