package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.IPlantable

/**
 * Created by CF on 2017-07-07.
 */
open class VanillaGenericSeed(override final val seeds: ItemStack)
    : ISeedWrapper {

    private val plantable: IPlantable = this.seeds.item as IPlantable

    override fun canPlantHere(world: World, pos: BlockPos): Boolean {
//        return world.getBlockState(pos.down()).block === Blocks.FARMLAND
//                && this.plantable.getPlantType(world, pos) == EnumPlantType.Crop

        val under = world.getBlockState(pos.down())
        return under.block.canSustainPlant(under, world, pos.down(), EnumFacing.UP, this.plantable)
    }

    override fun plant(world: World, pos: BlockPos): IBlockState {
        return if (this.canPlantHere(world, pos))
            this.plantable.getPlant(world, pos)
        else
            Blocks.AIR.defaultState
    }

    companion object {
        fun isSeed(stack: ItemStack)
                = (!stack.isEmpty) && (stack.item is IPlantable)
    }
}