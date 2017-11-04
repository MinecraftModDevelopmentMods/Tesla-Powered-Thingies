package net.ndrei.teslapoweredthingies.api.fluidburner

import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslapoweredthingies.api.IPoweredRecipe

interface IFluidBurnerFuelRecipe<R: IFluidBurnerFuelRecipe<R>>: IPoweredRecipe<R> {
    val fluid: FluidStack
    val baseTicks: Int
}
