package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

/**
 * Created by CF on 2017-07-13.
 */
object FluidCompoundProducerRecipes {
    val recipes = mutableListOf<FluidCompoundProducerRecipe>()

    fun FluidCompoundProducerRecipe.matchesInput(fluid: FluidStack, other: FluidStack?, ignoreSize: Boolean = true) =
        ((other == null) && (this.inputA.isEnough(fluid, ignoreSize) || this.inputB.isEnough(fluid, ignoreSize)))
            || (this.inputA.isEnough(fluid, ignoreSize) && this.inputB.isEnough(other, ignoreSize))

    fun FluidCompoundProducerRecipe?.invert() =
        if (this == null) null else FluidCompoundProducerRecipe(this.inputB, this.inputA, this.output)

    fun hasRecipe(fluid: FluidStack, other: FluidStack?) = this.recipes.any {
        it.matchesInput(fluid, other, true) || ((other != null) && it.matchesInput(other, fluid, true))
    }

    fun findRecipe(fluidA: FluidStack, fluidB: FluidStack) =
        this.recipes.firstOrNull { it.matchesInput(fluidA, fluidB, false) }
            ?: this.recipes.firstOrNull { it.matchesInput(fluidB, fluidA, false) }.invert()
}
