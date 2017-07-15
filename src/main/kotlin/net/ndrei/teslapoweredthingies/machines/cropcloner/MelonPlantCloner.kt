package net.ndrei.teslapoweredthingies.machines.cropcloner

import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-15.
 */
object MelonPlantCloner : GenericCropClonerPlant() {
    override fun getDrops(world: World, pos: BlockPos, state: IBlockState)
            = listOf(ItemStack(Blocks.MELON_BLOCK), *super.getDrops(world, pos, state).toTypedArray())
}