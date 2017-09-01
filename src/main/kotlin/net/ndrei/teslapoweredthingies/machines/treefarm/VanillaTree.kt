package net.ndrei.teslapoweredthingies.machines.treefarm

import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
class VanillaTree : ITreeFactory {
    override fun getHarvestableLog(world: World, pos: BlockPos, block: IBlockState): ITreeLogWrapper? {
        return if ((block.block === Blocks.LOG || block.block === Blocks.LOG2))
            VanillaTreeLog(world, pos)
        else
            null
    }

    override fun getHarvestableLeaf(world: World, pos: BlockPos, block: IBlockState): ITreeLeafWrapper? {
        return if ((block.block === Blocks.LEAVES || block.block === Blocks.LEAVES2))
            VanillaTreeLeaf(world, pos)
        else
            null
    }

    override fun getPlantableSapling(stack: ItemStack): ITreeSaplingWrapper? {
        if (!stack.isEmpty && stack.item === Item.getItemFromBlock(Blocks.SAPLING)) {
            return VanillaSapling(stack)
        }
        return null
    }
}
