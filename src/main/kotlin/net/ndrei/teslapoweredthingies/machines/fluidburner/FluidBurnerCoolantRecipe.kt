package net.ndrei.teslapoweredthingies.machines.fluidburner

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslapoweredthingies.MOD_ID
import net.ndrei.teslapoweredthingies.api.fluidburner.IFluidBurnerCoolantRecipe
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistryEntry

/**
 * Created by CF on 2017-06-30.
 */
class FluidBurnerCoolantRecipe(override val fluid: FluidStack, override val timeMultiplier: Float)
    : BaseTeslaRegistryEntry<FluidBurnerCoolantRecipe>(FluidBurnerCoolantRecipe::class.java, ResourceLocation(MOD_ID, "fluid_${fluid.fluid.name}"))
    , IFluidBurnerCoolantRecipe<FluidBurnerCoolantRecipe>
