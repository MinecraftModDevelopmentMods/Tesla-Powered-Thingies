package net.ndrei.teslapoweredthingies.machines.treefarm

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.ndrei.teslacorelib.utils.equalsIgnoreSize
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistryEntry

class TreeFarmModRecipe(name: ResourceLocation, val recipeType: RecipeType, vararg val filters: ItemStack)
    : BaseTeslaRegistryEntry<TreeFarmModRecipe>(TreeFarmModRecipe::class.java, name), ITreeFactory {

    enum class RecipeType {
        LOG, LEAF, SAPLING
    }

    override fun getHarvestableLog(world: World, pos: BlockPos, block: IBlockState) =
        when (this.recipeType) {
            RecipeType.LOG -> if (this.isMatch(block)) VanillaTreeLog(world, pos) else null
            else -> null
        }

    override fun getHarvestableLeaf(world: World, pos: BlockPos, block: IBlockState) =
        when (this.recipeType) {
            RecipeType.LEAF -> if (this.isMatch(block)) VanillaTreeLeaf(world, pos) else null
            else -> null
        }

    override fun getPlantableSapling(stack: ItemStack)=
        when (this.recipeType) {
            RecipeType.SAPLING -> if (this.isMatch(stack)) VanillaSapling(stack) else null
            else -> null
        }

    private fun isMatch(block: IBlockState) =
        this.filters
            .filter { it.item is ItemBlock }
            .any { (it.item as? ItemBlock)?.block === block.block }

    private fun isMatch(stack: ItemStack) =
        this.filters
            .any { stack.equalsIgnoreSize(it) && (stack.count >= it.count) }
}
