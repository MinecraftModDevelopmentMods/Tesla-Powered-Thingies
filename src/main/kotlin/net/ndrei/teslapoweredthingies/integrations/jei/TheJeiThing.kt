package net.ndrei.teslapoweredthingies.integrations.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin

/**
 * Created by CF on 2017-06-30.
 */
@JEIPlugin
class TheJeiThing : IModPlugin {
    override fun register(registry: IModRegistry) {
        super.register(registry)
        val jeiHelpers = registry.jeiHelpers
        val guiHelper = jeiHelpers.guiHelper

        FluidBurnerCategory.register(registry, guiHelper)
        FluidSolidifierCategory.register(registry, guiHelper)
        IncineratorCategory.register(registry, guiHelper)
        PowderMakerCategory.register(registry, guiHelper)
    }
}
