package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile
import net.ndrei.teslacorelib.config.readFluidStack
import net.ndrei.teslacorelib.utils.isEnough
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry

/**
 * Created by CF on 2017-07-13.
 */
@RegistryHandler
object FluidCompoundProducerRegistry
    : BaseTeslaRegistry<FluidCompoundProducerRecipe>("fluid_compound_recipes", FluidCompoundProducerRecipe::class.java) {

    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        readExtraRecipesFile(FluidCompoundProducerBlock.registryName!!.resourcePath) { json ->
            val inputA = json.readFluidStack("input_a") ?: return@readExtraRecipesFile
            val inputB = json.readFluidStack("input_b") ?: return@readExtraRecipesFile
            val output = json.readFluidStack("output") ?: return@readExtraRecipesFile

            this.addRecipe(FluidCompoundProducerRecipe(inputA, inputB, output))
        }
    }

    private fun FluidCompoundProducerRecipe.matchesInput(fluid: FluidStack, other: FluidStack?, ignoreSize: Boolean = true) =
        ((other == null) && (this.inputA.isEnough(fluid, ignoreSize) || this.inputB.isEnough(fluid, ignoreSize)))
            || (this.inputA.isEnough(fluid, ignoreSize) && this.inputB.isEnough(other, ignoreSize))

    private fun FluidCompoundProducerRecipe?.invert() =
        if (this == null) null else FluidCompoundProducerRecipe(this.registryName!!, this.inputB, this.inputA, this.output)

    fun hasRecipe(fluid: FluidStack, other: FluidStack?) = this.hasRecipe {
        it.matchesInput(fluid, other, true) || ((other != null) && it.matchesInput(other, fluid, true))
    }

    fun findRecipe(fluidA: FluidStack, fluidB: FluidStack) = this.findRecipe {
        it.matchesInput(fluidA, fluidB, false)
    } ?: this.findRecipe { it.matchesInput(fluidB, fluidA, false) }.invert()
}
