package net.ndrei.teslapoweredthingies.machines.cropfarm

import com.google.common.collect.Lists
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ImmersiveHempPlant(private val world: World, private val pos: BlockPos) : IPlantWrapper {
    override fun canBeHarvested(): Boolean {
        val state = this.world.getBlockState(this.pos.up())
        return (state.block.registryName?.toString() == REGISTRY_NAME) && (state.block.getMetaFromState(state) == 5)
    }

    override fun harvest(fortune: Int): List<ItemStack> {
        val loot = Lists.newArrayList<ItemStack>()
        if (this.canBeHarvested()) {
            val state = this.world.getBlockState(pos.up())
            loot.addAll(state.block.getDrops(this.world, this.pos.up(), state, fortune))
            this.world.setBlockToAir(pos.up())
        }
        return loot
    }

    override fun canBlockNeighbours() = false
    override fun blocksNeighbour(pos: BlockPos) = false

    override fun canUseFertilizer() = false
    override fun useFertilizer(fertilizer: ItemStack) = 0

    companion object {
        const val REGISTRY_NAME = "immersiveengineering:hemp"

        fun isMatch(state: IBlockState) =
            state.block.registryName?.toString() == REGISTRY_NAME
    }
}