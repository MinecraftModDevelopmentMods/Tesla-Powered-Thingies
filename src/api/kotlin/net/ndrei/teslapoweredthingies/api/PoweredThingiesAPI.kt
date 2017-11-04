package net.ndrei.teslapoweredthingies.api

import net.ndrei.teslapoweredthingies.api.compoundmaker.ICompoundMakerRegistry
import net.ndrei.teslapoweredthingies.api.fluidburner.IFluidBurnerCoolantRegistry
import net.ndrei.teslapoweredthingies.api.fluidburner.IFluidBurnerFuelRegistry
import net.ndrei.teslapoweredthingies.api.fluidcompoundproducer.IFluidCompoundProducerRegistry

object PoweredThingiesAPI {
    lateinit var compoundMakerRegistry: ICompoundMakerRegistry<*>
    lateinit var fluidCompoundProducerRegistry: IFluidCompoundProducerRegistry<*>

    lateinit var fluidBurnerCoolanRegistry: IFluidBurnerCoolantRegistry<*>
    lateinit var fluidBurnerFuelRegistry: IFluidBurnerFuelRegistry<*>
}
