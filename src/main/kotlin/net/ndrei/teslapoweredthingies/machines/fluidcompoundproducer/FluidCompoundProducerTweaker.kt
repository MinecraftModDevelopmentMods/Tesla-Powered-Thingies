package net.ndrei.teslapoweredthingies.machines.fluidcompoundproducer

import crafttweaker.annotations.ZenRegister
import crafttweaker.api.liquid.ILiquidStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.integrations.crafttweaker.BaseRegistryTweaker
import net.ndrei.teslapoweredthingies.machines.itemcompoundproducer.FluidCompoundProducerRecipe
import net.ndrei.teslapoweredthingies.machines.itemcompoundproducer.FluidCompoundProducerRegistry
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenRegister
@ZenClass("mods.poweredthingies.FluidCompoundProducer")
@Suppress("unused")
class FluidCompoundProducerTweaker : BaseRegistryTweaker<FluidCompoundProducerRecipe>(FluidCompoundProducerRegistry) {
    @ZenMethod
    fun addRecipe(rawOutput: ILiquidStack, rawInputA: ILiquidStack?, rawInputB: ILiquidStack?) {
        super.add {
            val output = (rawOutput.internal as? FluidStack) ?: throw Exception("Output not specified or not a fluid stack.")
            val inputA = (rawInputA?.internal as? FluidStack) ?: throw Exception("First Input not specified or not a fluid stack.")
            val inputB = (rawInputB?.internal as? FluidStack) ?: throw Exception("Second Input not specified or not a fluid stack.")

            FluidCompoundProducerRecipe(inputA, inputB, output)
        }
    }

    @SubscribeEvent
    fun onRegistrationCompleted(ev: BaseTeslaRegistry.DefaultRegistrationCompletedEvent<FluidCompoundProducerRecipe>) {
        super.runRegistrations()
    }
}