package net.ndrei.teslapoweredthingies.integrations.crafttweaker

import crafttweaker.annotations.ZenRegister
import net.ndrei.teslacorelib.annotations.InitializeDuringConstruction
import net.ndrei.teslapoweredthingies.machines.compoundmaker.CompoundMakerTweaker
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerCoolantTweaker
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerFuelTweaker
import net.ndrei.teslapoweredthingies.machines.fluidcompoundproducer.FluidCompoundProducerTweaker
import net.ndrei.teslapoweredthingies.machines.incinerator.IncineratorTweaker
import net.ndrei.teslapoweredthingies.machines.itemcompoundproducer.ItemCompoundProducerTweaker
import net.ndrei.teslapoweredthingies.machines.itemliquefier.ItemLiquefierTweaker
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerTweaker
import net.ndrei.teslapoweredthingies.machines.poweredkiln.PoweredKilnTweaker
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenRegister
@ZenClass("mods.poweredthingies.Tweaker")
@Suppress("unused")
@InitializeDuringConstruction
object PoweredThingiesTweaker {
    private val compoundTweakerInstance = CompoundMakerTweaker()
    @ZenMethod @JvmStatic fun compoundTweaker() = this.compoundTweakerInstance

    private val fluidCompoundTweakerInstance = FluidCompoundProducerTweaker()
    @ZenMethod @JvmStatic fun fluidCompoundTweaker() = this.fluidCompoundTweakerInstance

    private val fluidBurnerCoolantInstance = FluidBurnerCoolantTweaker()
    @ZenMethod @JvmStatic fun fluidBurnerCoolantTweaker() = this.fluidBurnerCoolantInstance

    private val fluidBurnerFuelInstance = FluidBurnerFuelTweaker()
    @ZenMethod @JvmStatic fun fluidBurnerFuelTweaker() = this.fluidBurnerFuelInstance

    private val itemCompoundProducerInstance = ItemCompoundProducerTweaker()
    @ZenMethod @JvmStatic fun itemCompoundProducerTweaker() = this.itemCompoundProducerInstance

    private val incineratorInstance = IncineratorTweaker()
    @ZenMethod @JvmStatic fun incineratorTweaker() = this.incineratorInstance

    private val itemLiquefierInstance = ItemLiquefierTweaker()
    @ZenMethod @JvmStatic fun itemLiquefierTweaker() = this.itemLiquefierInstance

    private val poweredKilnInstance = PoweredKilnTweaker()
    @ZenMethod @JvmStatic fun poweredKilnTweaker() = this.poweredKilnInstance

    private val powderMakerInstance = PowderMakerTweaker()
    @ZenMethod @JvmStatic fun powderMakerTweaker() = this.powderMakerInstance
}
