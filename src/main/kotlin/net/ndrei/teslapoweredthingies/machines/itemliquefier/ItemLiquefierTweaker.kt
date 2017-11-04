package net.ndrei.teslapoweredthingies.machines.itemliquefier

import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import crafttweaker.api.liquid.ILiquidStack
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.integrations.crafttweaker.BaseRegistryTweaker
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenRegister
@ZenClass("mods.poweredthingies.ItemLiquefier")
@Suppress("unused")
class ItemLiquefierTweaker : BaseRegistryTweaker<ItemLiquefierRecipe>(ItemLiquefierRegistry) {
    @ZenMethod
    fun addRecipe(rawInput: IItemStack, rawOutput: ILiquidStack) {
        super.add {
            val input = (rawInput.internal as? ItemStack) ?: throw Exception("Input not specified or not an item stack.")
            val output = (rawOutput.internal as? FluidStack) ?: throw Exception("Output not specified or not a fluid stack.")

            ItemLiquefierRecipe(input, output)
        }
    }

    @SubscribeEvent
    fun onRegistrationCompleted(ev: BaseTeslaRegistry.DefaultRegistrationCompletedEvent<ItemLiquefierRecipe>) {
        super.runRegistrations()
    }
}