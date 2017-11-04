package net.ndrei.teslapoweredthingies.machines.poweredkiln

import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.integrations.crafttweaker.BaseRegistryTweaker
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenRegister
@ZenClass("mods.poweredthingies.PoweredKiln")
@Suppress("unused")
class PoweredKilnTweaker : BaseRegistryTweaker<PoweredKilnRecipe>(PoweredKilnRegistry) {
    @ZenMethod
    fun addRecipe(rawInput: IItemStack, rawOutput: IItemStack) {
        super.add {
            val input = (rawInput.internal as? ItemStack) ?: throw Exception("Input not specified or not an item stack.")
            val output = (rawOutput.internal as? ItemStack) ?: throw Exception("Output not specified or not an item stack.")

            PoweredKilnRecipe(input, output)
        }
    }

    @SubscribeEvent
    fun onRegistrationCompleted(ev: BaseTeslaRegistry.DefaultRegistrationCompletedEvent<PoweredKilnRecipe>) {
        super.runRegistrations()
    }
}
