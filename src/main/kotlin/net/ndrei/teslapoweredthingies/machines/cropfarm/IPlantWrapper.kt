package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos

/**
 * Created by CF on 2017-07-07.
 */
interface IPlantWrapper {
    fun canBeHarvested(): Boolean
    fun harvest(fortune: Int): List<ItemStack>

    fun canBlockNeighbours(): Boolean
    fun blocksNeighbour(pos: BlockPos): Boolean

    fun canUseFertilizer(): Boolean
    fun useFertilizer(fertilizer: ItemStack): Int
}