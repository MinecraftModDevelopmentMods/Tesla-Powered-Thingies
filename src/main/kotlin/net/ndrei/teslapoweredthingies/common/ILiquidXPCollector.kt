package net.ndrei.teslapoweredthingies.common

import net.minecraft.item.ItemStack

/**
 * Created by CF on 2017-07-06.
 */
interface ILiquidXPCollector {
    fun hasXPCollector(): Boolean

    fun onLiquidXPAddonAdded(stack: ItemStack)
    fun onLiquidXPAddonRemoved(stack: ItemStack)
}