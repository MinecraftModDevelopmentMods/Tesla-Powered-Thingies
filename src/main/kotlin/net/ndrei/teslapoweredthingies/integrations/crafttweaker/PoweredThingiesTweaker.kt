package net.ndrei.teslapoweredthingies.integrations.crafttweaker

import crafttweaker.annotations.ZenRegister
import net.ndrei.teslacorelib.annotations.InitializeDuringConstruction
import net.ndrei.teslapoweredthingies.machines.compoundmaker.CompoundMakerTweaker
import net.ndrei.teslapoweredthingies.machines.fluidcompoundproducer.FluidCompoundProducerTweaker
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenRegister
@ZenClass("mod.poweredthingies.Tweaker")
@Suppress("unused")
@InitializeDuringConstruction
object PoweredThingiesTweaker {
    private val compoundTweakerInstance = CompoundMakerTweaker()
    @ZenMethod @JvmStatic fun compoundTweaker() = this.compoundTweakerInstance

    private val fluidCompoundTweakerInstance = FluidCompoundProducerTweaker()
    @ZenMethod @JvmStatic fun fluidCompoundTweaker() = this.fluidCompoundTweakerInstance
}
