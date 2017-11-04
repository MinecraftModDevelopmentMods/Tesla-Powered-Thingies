package net.ndrei.teslapoweredthingies.api.fluidburner

import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslapoweredthingies.api.IPoweredRecipe

interface IFluidBurnerCoolantRecipe<R: IFluidBurnerCoolantRecipe<R>>: IPoweredRecipe<R> {
    val fluid: FluidStack
    val timeMultiplier: Float
}
