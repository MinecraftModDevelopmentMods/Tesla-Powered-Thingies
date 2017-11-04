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
    private val fluidBurnerCoolantInstance = FluidBurnerCoolantTweaker()
    private val fluidBurnerFuelInstance = FluidBurnerFuelTweaker()
    private val fluidCompoundTweakerInstance = FluidCompoundProducerTweaker()
    private val incineratorInstance = IncineratorTweaker()
    private val itemCompoundProducerInstance = ItemCompoundProducerTweaker()
    private val itemLiquefierInstance = ItemLiquefierTweaker()
    private val powderMakerInstance = PowderMakerTweaker()
    private val poweredKilnInstance = PoweredKilnTweaker()

    @ZenMethod @JvmStatic fun compoundTweaker() = this.compoundTweakerInstance
    @ZenMethod @JvmStatic fun fluidBurnerCoolantTweaker() = this.fluidBurnerCoolantInstance
    @ZenMethod @JvmStatic fun fluidBurnerFuelTweaker() = this.fluidBurnerFuelInstance
    @ZenMethod @JvmStatic fun fluidCompoundTweaker() = this.fluidCompoundTweakerInstance
    @ZenMethod @JvmStatic fun incineratorTweaker() = this.incineratorInstance
    @ZenMethod @JvmStatic fun itemCompoundProducerTweaker() = this.itemCompoundProducerInstance
    @ZenMethod @JvmStatic fun itemLiquefierTweaker() = this.itemLiquefierInstance
    @ZenMethod @JvmStatic fun powderMakerTweaker() = this.powderMakerInstance
    @ZenMethod @JvmStatic fun poweredKilnTweaker() = this.poweredKilnInstance
}
