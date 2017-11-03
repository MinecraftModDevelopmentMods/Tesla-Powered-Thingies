package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslapoweredthingies.MOD_ID
import net.ndrei.teslapoweredthingies.api.fluidcompoundproducer.IFluidCompoundProducerRecipe
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistryEntry

/**
 * Created by CF on 2017-07-13.
 */
class FluidCompoundProducerRecipe(name: ResourceLocation, override val inputA: FluidStack, override val inputB: FluidStack, override val output: FluidStack)
    : BaseTeslaRegistryEntry<FluidCompoundProducerRecipe>(FluidCompoundProducerRecipe::class.java, name), IFluidCompoundProducerRecipe<FluidCompoundProducerRecipe> {
    constructor(inputA: FluidStack, inputB: FluidStack, output: FluidStack)
        : this(ResourceLocation(MOD_ID, "fluid_${output.fluid.name}"), inputA, inputB, output)

    constructor(name: String, inputA: FluidStack, inputB: FluidStack, output: FluidStack)
        : this(ResourceLocation(MOD_ID, name), inputA, inputB, output)
}