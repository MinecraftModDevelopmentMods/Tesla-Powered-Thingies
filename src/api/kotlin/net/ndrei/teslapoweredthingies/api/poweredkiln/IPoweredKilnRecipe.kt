package net.ndrei.teslapoweredthingies.api.poweredkiln

import net.minecraft.item.ItemStack
import net.ndrei.teslapoweredthingies.api.IPoweredRecipe

interface IPoweredKilnRecipe<T: IPoweredKilnRecipe<T>>: IPoweredRecipe<T> {
    val input: ItemStack
    val output: ItemStack
}
