package net.ndrei.teslapoweredthingies.api.fluidcompoundproducer

import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslapoweredthingies.api.IPoweredRecipe

interface IFluidCompoundProducerRecipe<R: IFluidCompoundProducerRecipe<R>>: IPoweredRecipe<R> {
    val inputA: FluidStack
    val inputB: FluidStack
    val output: FluidStack
}
