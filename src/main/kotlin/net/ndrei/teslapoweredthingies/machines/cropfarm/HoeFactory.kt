package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.item.ItemHoe
import net.minecraft.item.ItemStack

object HoeFactory {
    fun getHoe(stack: ItemStack): IAmHoe? {
        if (!stack.isEmpty && (stack.item is ItemHoe))
            return VanillaHoe
        else
            return null
    }
}