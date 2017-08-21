package net.ndrei.teslapoweredthingies.common

import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.ndrei.teslacorelib.BaseProxy
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerRecipes
import net.ndrei.teslapoweredthingies.machines.incinerator.IncineratorRecipes

/**
 * Created by CF on 2017-06-30.
 */
@Suppress("unused")
open class CommonProxy(side: Side) : BaseProxy(side) {
    constructor() : this(Side.SERVER)

    override fun postInit(ev: FMLPostInitializationEvent) {
        super.postInit(ev)

        IncineratorRecipes.registerRecipes()
        FluidBurnerRecipes.registerRecipes()
    }
}
