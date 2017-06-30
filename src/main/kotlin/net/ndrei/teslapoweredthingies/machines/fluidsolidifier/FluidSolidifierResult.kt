package net.ndrei.teslapoweredthingies.machines.fluidsolidifier

import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack

/**
 * Created by CF on 2017-06-30.
 */
enum class FluidSolidifierResult(val stateIndex: Int, val resultStack: ItemStack, val ticksRequired: Int, val waterMbMin: Int, val waterMbConsumed: Int, val lavaMbMin: Int, val lavaMbConsumed: Int) {
    COBBLESTONE(0, ItemStack(Blocks.COBBLESTONE), 30, 1000, 0, 1000, 0),
    STONE(1, ItemStack(Blocks.STONE), 40, 1000, 125, 1000, 125),
    OBSIDIAN(2, ItemStack(Blocks.OBSIDIAN), 900, 1000, 1000, 1000, 1000);

    companion object {
        fun fromStateIndex(ordinal: Int): FluidSolidifierResult {
            return values()[ordinal]
        }
    }
}
