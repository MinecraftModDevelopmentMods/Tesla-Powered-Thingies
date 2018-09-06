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
import net.ndrei.teslapoweredthingies.api.fluidburner.IFluidBurnerCoolantRegistry
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile

@RegistryHandler
object FluidBurnerCoolantRegistry
    : BaseTeslaRegistry<FluidBurnerCoolantRecipe>("fluid_burner_coolant_recipes", FluidBurnerCoolantRecipe::class.java)
    , IFluidBurnerCoolantRegistry<FluidBurnerCoolantRecipe> {

    override fun construct(asm: ASMDataTable) {
        super.construct(asm)
        PoweredThingiesAPI.fluidBurnerCoolantRegistry = this
    }

    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        readExtraRecipesFile(FluidBurnerBlock.registryName!!.path + "_coolant") { json ->
            val fluid = json.readFluidStack() ?: return@readExtraRecipesFile

            this.addRecipe(FluidBurnerCoolantRecipe(fluid, JsonUtils.getFloat(json, "time", 1.0f)))
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
