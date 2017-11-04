package net.ndrei.teslapoweredthingies.machines.fluidburner

import crafttweaker.annotations.ZenRegister
import crafttweaker.api.liquid.ILiquidStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.integrations.crafttweaker.BaseRegistryTweaker
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenRegister
@ZenClass("mods.poweredthingies.FluidBurnerCoolant")
@Suppress("unused")
class FluidBurnerCoolantTweaker : BaseRegistryTweaker<FluidBurnerCoolantRecipe>(FluidBurnerCoolantRegistry) {
    @ZenMethod
    fun addCoolant(rawFluid: ILiquidStack, timeMultiplier: Float) {
        super.add {
            val fluid = (rawFluid.internal as? FluidStack) ?: throw Exception("Fluid not specified or not a fluid stack.")

            FluidBurnerCoolantRecipe(fluid.fluid, fluid.amount, timeMultiplier)
        }
    }

    @SubscribeEvent
    fun onRegistrationCompleted(ev: BaseTeslaRegistry.DefaultRegistrationCompletedEvent<FluidBurnerCoolantRecipe>) {
        super.runRegistrations()
    }
}