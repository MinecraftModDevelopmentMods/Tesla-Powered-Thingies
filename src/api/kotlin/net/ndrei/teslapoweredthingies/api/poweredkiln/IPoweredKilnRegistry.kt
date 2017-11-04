package net.ndrei.teslapoweredthingies.api.poweredkiln

import net.minecraft.item.ItemStack
import net.ndrei.teslapoweredthingies.api.IPoweredRegistry

interface IPoweredKilnRegistry<T: IPoweredKilnRecipe<T>>: IPoweredRegistry<T> {
    fun hasRecipe(stack: ItemStack): Boolean
    fun findRecipe(input: ItemStack): T?
}