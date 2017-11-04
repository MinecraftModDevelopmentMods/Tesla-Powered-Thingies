package net.ndrei.teslapoweredthingies.api.fluidburner

import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslapoweredthingies.api.IPoweredRegistry

interface IFluidBurnerFuelRegistry<R: IFluidBurnerFuelRecipe<R>>: IPoweredRegistry<R> {
    fun hasRecipe(fluid: FluidStack) : Boolean
    fun findRecipe(fluid: FluidStack) : R?
}
