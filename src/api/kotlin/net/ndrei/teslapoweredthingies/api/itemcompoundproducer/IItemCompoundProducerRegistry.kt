package net.ndrei.teslapoweredthingies.api.itemcompoundproducer

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslapoweredthingies.api.IPoweredRegistry

interface IItemCompoundProducerRegistry<T: IItemCompoundProducerRecipe<T>>: IPoweredRegistry<T> {
    fun hasRecipe(fluid: FluidStack): Boolean
    fun hasRecipe(stack: ItemStack): Boolean

    fun findRecipe(fluid: FluidStack, stack: ItemStack): T?
}