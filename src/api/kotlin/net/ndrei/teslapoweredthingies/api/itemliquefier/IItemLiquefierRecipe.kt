package net.ndrei.teslapoweredthingies.api.itemliquefier

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslapoweredthingies.api.IPoweredRecipe

interface IItemLiquefierRecipe<T: IItemLiquefierRecipe<T>>: IPoweredRecipe<T> {
    val input: ItemStack
    val output: FluidStack
}