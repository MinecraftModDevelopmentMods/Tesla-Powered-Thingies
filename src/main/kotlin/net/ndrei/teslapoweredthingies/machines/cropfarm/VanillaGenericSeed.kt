package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.EnumPlantType
import net.minecraftforge.common.IPlantable

/**
 * Created by CF on 2017-07-07.
 */
open class VanillaGenericSeed(override final val seeds: ItemStack)
    : ISeedWrapper {

    private val plantable: IPlantable = this.seeds.item as IPlantable

    override fun canPlantHere(world: World, pos: BlockPos): Boolean {
        return world.getBlockState(pos.down()).block === Blocks.FARMLAND
                && this.plantable.getPlantType(world, pos) == EnumPlantType.Crop
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