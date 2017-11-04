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
    private val compoundTweakerInstance by lazy { CompoundMakerTweaker() }
    private val fluidBurnerCoolantInstance by lazy { FluidBurnerCoolantTweaker() }
    private val fluidBurnerFuelInstance by lazy { FluidBurnerFuelTweaker() }
    private val fluidCompoundTweakerInstance by lazy { FluidCompoundProducerTweaker() }
    private val incineratorInstance by lazy { IncineratorTweaker() }
    private val itemCompoundProducerInstance by lazy { ItemCompoundProducerTweaker() }
    private val itemLiquefierInstance by lazy { ItemLiquefierTweaker() }
    private val powderMakerInstance by lazy { PowderMakerTweaker() }
    private val poweredKilnInstance by lazy { PoweredKilnTweaker() }

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
