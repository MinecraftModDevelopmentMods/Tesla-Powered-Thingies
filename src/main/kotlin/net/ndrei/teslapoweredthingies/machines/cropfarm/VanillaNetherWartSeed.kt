package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
class VanillaNetherWartSeed(seed: ItemStack)
    : VanillaGenericSeed(seed) {

    override fun canPlantHere(world: World, pos: BlockPos): Boolean {
        val under = world.getBlockState(pos.down()).block
        return under === Blocks.SOUL_SAND
    }

    companion object {
        fun isSeed(stack: ItemStack)
            = !stack.isEmpty && (stack.item === Items.NETHER_WART)
    }
}