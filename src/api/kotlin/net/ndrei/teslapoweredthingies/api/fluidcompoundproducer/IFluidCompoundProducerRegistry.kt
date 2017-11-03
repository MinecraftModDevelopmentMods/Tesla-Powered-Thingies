package net.ndrei.teslapoweredthingies.api.fluidcompoundproducer

import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslapoweredthingies.api.IPoweredRegistry

interface IFluidCompoundProducerRegistry<R: IFluidCompoundProducerRecipe<R>>: IPoweredRegistry<R> {
    fun hasRecipe(fluid: FluidStack, other: FluidStack?) : Boolean
    fun findRecipe(fluidA: FluidStack, fluidB: FluidStack) : R?
}