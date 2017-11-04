package net.ndrei.teslapoweredthingies.machines.compoundmaker

import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import crafttweaker.api.liquid.ILiquidStack
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.ndrei.teslacorelib.utils.isEmpty
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.integrations.crafttweaker.BaseRegistryTweaker
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenRegister
@ZenClass("mods.poweredthingies.CompoundMaker")
@Suppress("unused")
class CompoundMakerTweaker: BaseRegistryTweaker<CompoundMakerRecipe>(CompoundMakerRegistry) {
    @ZenMethod
    fun addRecipe(rawOutput: IItemStack, rawLeft: ILiquidStack?, rawTop: Array<IItemStack>?, rawRight: ILiquidStack?, rawBottom: Array<IItemStack>?) {
        super.add {
            val output = (rawOutput.internal as? ItemStack) ?: throw Exception("Output not specified or not an item stack.")
            val left = rawLeft?.internal as? FluidStack
            val top = rawTop?.mapNotNull { it.internal as? ItemStack }?.toTypedArray() ?: arrayOf()
            val right = rawRight?.internal as? FluidStack
            val bottom = rawBottom?.mapNotNull { it.internal as? ItemStack }?.toTypedArray() ?: arrayOf()

            if (left.isEmpty && top.isEmpty() && right.isEmpty && bottom.isEmpty()) {
                throw Exception("No ingredients specified.")
            }

            CompoundMakerRecipe(output.item.registryName!!, output, left, top, right, bottom)
        }
    }

    @SubscribeEvent
    fun onCompoundRegistrationCompleted(ev: BaseTeslaRegistry.DefaultRegistrationCompletedEvent<CompoundMakerRecipe>) {
        super.runRegistrations()
    }
}
