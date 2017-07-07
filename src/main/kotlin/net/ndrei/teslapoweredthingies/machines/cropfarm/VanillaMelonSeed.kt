package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
class VanillaMelonSeed(seed: ItemStack)
    : VanillaGenericSeed(seed) {

    override fun canPlantHere(world: World, pos: BlockPos): Boolean {
        return (world.isAirBlock(pos.north())
                || world.isAirBlock(pos.east())
                || world.isAirBlock(pos.south())
                || world.isAirBlock(pos.west())) && super.canPlantHere(world, pos)
    }

    companion object {
        fun isSeed(stack: ItemStack)
            = !stack.isEmpty
                && ((stack.item === Items.MELON_SEEDS) || (stack.item === Items.PUMPKIN_SEEDS))
    }
}
