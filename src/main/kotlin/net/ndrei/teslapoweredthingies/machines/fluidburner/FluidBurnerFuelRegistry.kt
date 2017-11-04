package net.ndrei.teslapoweredthingies.machines.fluidburner

import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.JsonUtils
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.config.readFluidStack
import net.ndrei.teslacorelib.utils.isEnough
import net.ndrei.teslapoweredthingies.api.PoweredThingiesAPI
import net.ndrei.teslapoweredthingies.api.fluidburner.IFluidBurnerFuelRegistry
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile

@RegistryHandler
object FluidBurnerFuelRegistry
    : BaseTeslaRegistry<FluidBurnerFuelRecipe>("fluid_burner_fuel_recipes", FluidBurnerFuelRecipe::class.java)
    , IFluidBurnerFuelRegistry<FluidBurnerFuelRecipe> {

    override fun construct(asm: ASMDataTable) {
        super.construct(asm)
        PoweredThingiesAPI.fluidBurnerFuelRegistry = this
    }

    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        readExtraRecipesFile(FluidBurnerBlock.registryName!!.resourcePath + "_fuel") { json ->
            val fluid = json.readFluidStack() ?: return@readExtraRecipesFile

            this.addRecipe(FluidBurnerFuelRecipe(fluid, JsonUtils.getInt(json, "ticks", 100)))
        }

        this.registrationCompleted()
    }

    override fun hasRecipe(fluid: FluidStack) = this.hasRecipe {
        it.fluid.isFluidEqual(fluid)
    }

    override fun findRecipe(fluid: FluidStack) = this.findRecipe {
        it.fluid.isEnough(fluid)
    }
}