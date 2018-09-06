package net.ndrei.teslapoweredthingies.machines.treefarm

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by CF on 2017-07-07.
 */
open class BaseModTreeFactory(val modId: String) : ITreeFactory {
    protected open val logOreName get() = "logWood"
    protected open val leavesOreName get() = "treeLeaves"
    protected open val saplingOreName get() = "treeSapling"

    override fun getHarvestableLog(world: World, pos: BlockPos, block: IBlockState): ITreeLogWrapper? {
        val stack = ItemStack(block.block)
        return if (!stack.isEmpty
                && (this.modId.isNullOrEmpty() || (block.block.registryName?.namespace == this.modId))
                && OreDictionary.getOreIDs(stack).any { OreDictionary.getOreName(it) == this.logOreName })
            VanillaTreeLog(world, pos)
        else
            null
    }

    override fun getHarvestableLeaf(world: World, pos: BlockPos, block: IBlockState): ITreeLeafWrapper? {
        val stack = ItemStack(block.block)
        return if (!stack.isEmpty
                && (this.modId.isNullOrEmpty() || (block.block.registryName?.namespace == this.modId))
                && OreDictionary.getOreIDs(stack).any { OreDictionary.getOreName(it) == this.leavesOreName })
            VanillaTreeLeaf(world, pos)
        else
            null
    }

    override fun getPlantableSapling(stack: ItemStack): ITreeSaplingWrapper? {
        if (!stack.isEmpty
                && (this.modId.isNullOrEmpty() || (stack.item.registryName?.namespace == this.modId))
                && (OreDictionary.getOreIDs(stack).any { OreDictionary.getOreName(it) == this.saplingOreName })) {
            return VanillaSapling(stack)
        }
        return null
    }
}
