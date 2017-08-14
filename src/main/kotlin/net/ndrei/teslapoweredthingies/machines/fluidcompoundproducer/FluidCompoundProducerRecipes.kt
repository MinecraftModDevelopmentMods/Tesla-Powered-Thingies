package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import net.minecraftforge.fluids.FluidStack

/**
 * Created by CF on 2017-07-13.
 */
object FluidCompoundProducerRecipes {
    val recipes = mutableListOf<FluidCompoundProducerRecipe>()

    fun FluidCompoundProducerRecipe.matchesInput(fluid: FluidStack, other: FluidStack?, ignoreSize: Boolean = true)
        = ((other == null) && ((this.inputA.isFluidEqual(fluid) && (ignoreSize || this.inputA.amount <= fluid.amount)) || (this.inputB.isFluidEqual(fluid) && (ignoreSize || this.inputB.amount <= fluid.amount))))
            || this.inputA.isFluidEqual(fluid) && this.inputB.isFluidEqual(other) && (ignoreSize || ((this.inputA.amount <= fluid.amount) && (this.inputB.amount <= other?.amount ?: 0)))

    fun FluidCompoundProducerRecipe?.invert()
        = if (this == null) null else FluidCompoundProducerRecipe(this.inputB, this.inputA, this.output)

    fun hasRecipe(fluid: FluidStack, other: FluidStack?) = this.recipes.any {
        it.matchesInput(fluid, other, true) || ((other != null) && it.matchesInput(other, fluid, true))
    }

    fun findRecipe(fluidA: FluidStack, fluidB: FluidStack)
        = this.recipes.firstOrNull { it.matchesInput(fluidA, fluidB, false) }
    ?: this.recipes.firstOrNull { it.matchesInput(fluidB, fluidA, false) }.invert()
}
