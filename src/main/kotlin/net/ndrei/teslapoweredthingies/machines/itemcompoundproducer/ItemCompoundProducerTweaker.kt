package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

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
@ZenClass("mods.poweredthingies.ItemCompoundMaker")
@Suppress("unused")
class ItemCompoundProducerTweaker : BaseRegistryTweaker<ItemCompoundProducerRecipe>(ItemCompoundProducerRegistry) {
    @ZenMethod
    fun addRecipe(rawInputStack: IItemStack, rawInputFluid: ILiquidStack, rawResult: IItemStack) {
        super.add {
            val inputStack = (rawInputStack.internal as? ItemStack) ?: throw Exception("Input item stack not specified or not an item stack.")
            val inputFluid = (rawInputFluid.internal as? FluidStack) ?: throw Exception("Input fluid stack not specified or not a fluid stack.")
            val result = (rawResult.internal as? ItemStack) ?: throw Exception("Result item stack not specified or not an item stack.")

            ItemCompoundProducerRecipe(result.item.registryName!!, inputStack, inputFluid, result)
        }
    }

    @SubscribeEvent
    fun onCompoundRegistrationCompleted(ev: BaseTeslaRegistry.DefaultRegistrationCompletedEvent<ItemCompoundProducerRecipe>) {
        super.runRegistrations()
    }
}