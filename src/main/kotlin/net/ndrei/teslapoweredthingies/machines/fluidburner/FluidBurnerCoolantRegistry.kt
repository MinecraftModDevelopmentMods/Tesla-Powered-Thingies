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
object FluidBurnerCoolantRegistry
    : BaseTeslaRegistry<FluidBurnerCoolantRecipe>("fluid_burner_coolant_recipes", FluidBurnerCoolantRecipe::class.java), IRegistryHandler {

    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        readExtraRecipesFile(FluidBurnerBlock.registryName!!.resourcePath + "_coolant") { json ->
            val fluid = json.readFluidStack() ?: return@readExtraRecipesFile

            this.addRecipe(FluidBurnerCoolantRecipe(fluid.fluid, fluid.amount, JsonUtils.getFloat(json, "time", 1.0f)))
        }

        this.registrationCompleted()
    }
}