package net.ndrei.teslapoweredthingies.api

import net.ndrei.teslapoweredthingies.api.compoundmaker.ICompoundMakerRegistry
import net.ndrei.teslapoweredthingies.api.fluidburner.IFluidBurnerCoolantRegistry
import net.ndrei.teslapoweredthingies.api.fluidburner.IFluidBurnerFuelRegistry
import net.ndrei.teslapoweredthingies.api.fluidcompoundproducer.IFluidCompoundProducerRegistry
import net.ndrei.teslapoweredthingies.api.itemcompoundproducer.IItemCompoundProducerRegistry
import net.ndrei.teslapoweredthingies.api.itemliquefier.IItemLiquefierRegistry

object PoweredThingiesAPI {
    lateinit var compoundMakerRegistry: ICompoundMakerRegistry<*>
    lateinit var fluidCompoundProducerRegistry: IFluidCompoundProducerRegistry<*>

    lateinit var fluidBurnerCoolantRegistry: IFluidBurnerCoolantRegistry<*>
    lateinit var fluidBurnerFuelRegistry: IFluidBurnerFuelRegistry<*>

    lateinit var itemCompoundProducerRegistry: IItemCompoundProducerRegistry<*>

    lateinit var itemLiquefierRegistry: IItemLiquefierRegistry<*>
}
