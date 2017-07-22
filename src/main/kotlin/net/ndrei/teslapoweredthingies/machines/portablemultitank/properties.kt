package net.ndrei.teslapoweredthingies.machines.portablemultitank

import net.minecraftforge.common.property.IUnlistedProperty
import net.minecraftforge.fluids.FluidStack

class UnlistedFluidProperty(val propName: String) : IUnlistedProperty<FluidStack> {
    override fun getName() = this.propName

    override fun isValid(value: FluidStack?) = true

    override fun getType() = FluidStack::class.java

    override fun valueToString(value: FluidStack?) = "${value?.fluid?.name ?: ""}::${value?.amount ?: 0}mb"
}