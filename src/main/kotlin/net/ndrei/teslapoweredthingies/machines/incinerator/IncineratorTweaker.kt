package net.ndrei.teslapoweredthingies.machines.incinerator

import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import crafttweaker.api.item.WeightedItemStack
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.common.SecondaryOutput
import net.ndrei.teslapoweredthingies.integrations.crafttweaker.BaseRegistryTweaker
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenRegister
@ZenClass("mods.poweredthingies.Incinerator")
@Suppress("unused")
class IncineratorTweaker : BaseRegistryTweaker<IncineratorRecipe>(IncineratorRegistry) {
    @ZenMethod
    fun addRecipe(rawInput: IItemStack, power: Long, outputs: Array<WeightedItemStack>) {
        super.add {
            val input = (rawInput.internal as? ItemStack) ?: throw Exception("Input not specified or not an item stack.")

            IncineratorRecipe(input, power, outputs.map {
                SecondaryOutput(it.chance, (it.stack.internal as? ItemStack) ?: throw Exception("Secondary output stack not specified or not an item stack."))
            }.toTypedArray())
        }
    }

    @SubscribeEvent
    fun onRegistrationCompleted(ev: BaseTeslaRegistry.DefaultRegistrationCompletedEvent<IncineratorRecipe>) {
        super.runRegistrations()
    }
}
