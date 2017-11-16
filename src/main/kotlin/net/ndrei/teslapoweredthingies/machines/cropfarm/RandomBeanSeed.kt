package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class RandomBeanSeed(override val seeds: ItemStack) : ISeedWrapper {
    override fun canPlantHere(world: World, pos: BlockPos): Boolean {
        if (!world.isAirBlock(pos)) return false

        return SPROUT_BLOCK.canPlaceBlockAt(world, pos)
    }

    override fun plant(world: World, pos: BlockPos) =
        SPROUT_BLOCK.defaultState

    companion object {
        private val SPROUT_BLOCK by lazy {
            Block.REGISTRY.getObject(ResourceLocation("randomthings", "beansprout"))
        }

        fun isSeed(stack: ItemStack) =
            (stack.item.registryName?.toString() == "randomthings:beans") && (stack.metadata == 0)
    }
}
