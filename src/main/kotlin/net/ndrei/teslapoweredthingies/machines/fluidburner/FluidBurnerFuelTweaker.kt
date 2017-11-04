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
@ZenClass("mods.poweredthingies.FluidBurnerFuel")
@Suppress("unused")
class FluidBurnerFuelTweaker : BaseRegistryTweaker<FluidBurnerFuelRecipe>(FluidBurnerFuelRegistry) {
    @ZenMethod
    fun addFuel(rawFluid: ILiquidStack, ticks: Int) {
        super.add {
            val fluid = (rawFluid.internal as? FluidStack) ?: throw Exception("Fluid not specified or not a fluid stack.")

            FluidBurnerFuelRecipe(fluid.copy(), ticks)
        }
    }

    @SubscribeEvent
    fun onRegistrationCompleted(ev: BaseTeslaRegistry.DefaultRegistrationCompletedEvent<FluidBurnerFuelRecipe>) {
        super.runRegistrations()
    }
}
