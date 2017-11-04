package net.ndrei.teslapoweredthingies.machines.powdermaker

import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import crafttweaker.api.item.WeightedItemStack
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.common.SecondaryOutput
import net.ndrei.teslapoweredthingies.integrations.crafttweaker.BaseRegistryTweaker
import net.ndrei.teslapoweredthingies.machines.incinerator.IncineratorRecipe
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenRegister
@ZenClass("mods.poweredthingies.PowderMaker")
@Suppress("unused")
class PowderMakerTweaker : BaseRegistryTweaker<PowderMakerRecipe>(PowderMakerRegistry) {
    @ZenMethod
    fun addRecipe(rawInput: IItemStack, outputs: Array<WeightedItemStack>) {
        super.add {
            val input = (rawInput.internal as? ItemStack) ?: throw Exception("Input not specified or not an item stack.")

            PowderMakerRecipe(input.item.registryName!!, listOf(input), outputs.map {
                SecondaryOutput(it.chance, (it.stack.internal as? ItemStack) ?: throw Exception("Secondary output stack not specified or not an item stack."))
            })
        }
    }

    @SubscribeEvent
    fun onRegistrationCompleted(ev: BaseTeslaRegistry.DefaultRegistrationCompletedEvent<IncineratorRecipe>) {
        super.runRegistrations()
    }
}