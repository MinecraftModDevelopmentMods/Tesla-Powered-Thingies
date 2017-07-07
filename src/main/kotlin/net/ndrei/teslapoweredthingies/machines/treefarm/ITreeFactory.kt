package net.ndrei.teslapoweredthingies.machines.treefarm

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
interface ITreeFactory {
    fun getHarvestableLog(world: World, pos: BlockPos, block: IBlockState): ITreeLogWrapper?
    fun getHarvestableLeaf(world: World, pos: BlockPos, block: IBlockState): ITreeLeafWrapper?
    fun getPlantableSapling(stack: ItemStack): ITreeSaplingWrapper?
}