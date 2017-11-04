package net.ndrei.teslapoweredthingies.machines.fluidburner

import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.JsonUtils
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.config.readFluidStack
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile

@RegistryHandler
object FluidBurnerFuelRegistry
    : BaseTeslaRegistry<FluidBurnerFuelRecipe>("fluid_burner_fuel_recipes", FluidBurnerFuelRecipe::class.java), IRegistryHandler {

    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        readExtraRecipesFile(FluidBurnerBlock.registryName!!.resourcePath + "_fuel") { json ->
            val fluid = json.readFluidStack() ?: return@readExtraRecipesFile

            this.addRecipe(FluidBurnerFuelRecipe(fluid.fluid, fluid.amount, JsonUtils.getInt(json, "ticks", 100)))
        }

        this.registrationCompleted()
    }
}