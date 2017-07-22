package net.ndrei.teslapoweredthingies.machines.treefarm

import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.IPlantable
import net.ndrei.teslacorelib.compatibility.ItemStackUtil

/**
 * Created by CF on 2017-07-07.
 */
class VanillaSapling(override val stack: ItemStack)
    : ITreeSaplingWrapper {

    override fun canPlant(world: World, pos: BlockPos): Boolean {
        if (ItemStackUtil.isEmpty(this.stack)) {
            return false
        }

        val item = this.stack.item
        val rawPlantable = if (item is ItemBlock) item.block else item
        val plantable = (if (rawPlantable is IPlantable) rawPlantable else null) ?: return false

        val down = world.getBlockState(pos.down())
        return down.block.canSustainPlant(down, world, pos.down(), EnumFacing.UP, plantable)
    }

    override fun plant(world: World, pos: BlockPos): Int {
        if (this.canPlant(world, pos)) {
            val item = this.stack.item
            val block = (item as? ItemBlock)?.block ?: return 0

            val plant = block.getStateFromMeta(this.stack.itemDamage)
            if (plant != null) {
                world.setBlockState(pos, plant)
                return 1
            }
        }

        return 0
    }
}