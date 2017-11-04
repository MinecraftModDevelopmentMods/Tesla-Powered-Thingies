package net.ndrei.teslapoweredthingies.api.itemcompoundproducer

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslapoweredthingies.api.IPoweredRecipe

interface IItemCompoundProducerRecipe<T: IItemCompoundProducerRecipe<T>>: IPoweredRecipe<T> {
    val inputStack: ItemStack
    val inputFluid: FluidStack
    val result: ItemStack
}