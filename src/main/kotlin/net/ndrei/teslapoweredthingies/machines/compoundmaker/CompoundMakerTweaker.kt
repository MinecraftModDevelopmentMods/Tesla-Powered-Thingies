package net.ndrei.teslapoweredthingies.machines.compoundmaker

import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import crafttweaker.api.liquid.ILiquidStack
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.ndrei.teslacorelib.utils.isEmpty
import net.ndrei.teslapoweredthingies.api.PoweredThingiesAPI
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenRegister
@ZenClass("mod.poweredthingies.CompoundMaker")
@Suppress("unused")
@Mod.EventBusSubscriber
object CompoundMakerTweaker {
    private val recipesToRegister = mutableListOf<() -> Unit>()
    private var compoundRegistryCompleted = false

    @ZenMethod
    @JvmStatic
    fun addRecipe(rawOutput: IItemStack, rawLeft: ILiquidStack?, rawTop: Array<IItemStack>?, rawRight: ILiquidStack?, rawBottom: Array<IItemStack>?) {
        this.addDelayedAction {
            val output = (rawOutput.internal as? ItemStack) ?: throw Exception("Output not specified or not an item stack.")
            val left = rawLeft?.internal as? FluidStack
            val top = rawTop?.mapNotNull { it.internal as? ItemStack }?.toTypedArray() ?: arrayOf()
            val right = rawRight?.internal as? FluidStack
            val bottom = rawBottom?.mapNotNull { it.internal as? ItemStack }?.toTypedArray() ?: arrayOf()

            if (left.isEmpty && top.isEmpty() && right.isEmpty && bottom.isEmpty()) {
                throw Exception("No ingredients specified.")
            }

            PoweredThingiesAPI.compoundMakerRegistry.registerRecipe(output, left, top, right, bottom)
        }
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(registration: String) {
        this.addDelayedAction {
            PoweredThingiesAPI.compoundMakerRegistry.removeRecipe(ResourceLocation(registration))
        }
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipes(rawOutput: IItemStack) {
        this.addDelayedAction {
            val output = (rawOutput.internal as? ItemStack) ?: throw Exception("Output not specified or not an item stack.")
            PoweredThingiesAPI.compoundMakerRegistry
                .findRecipes { ItemStack.areItemStacksEqual(it.output, output) }
                .mapNotNull { it.output.item.registryName }
                .forEach { PoweredThingiesAPI.compoundMakerRegistry.removeRecipe(it) }
        }
    }

    @ZenMethod
    @JvmStatic
    fun replaceRecipes(rawOutput: IItemStack, rawLeft: ILiquidStack?, rawTop: Array<IItemStack>?, rawRight: ILiquidStack?, rawBottom: Array<IItemStack>?) {
        this.removeRecipes(rawOutput)
        this.addRecipe(rawOutput, rawLeft, rawTop, rawRight, rawBottom)
    }

    fun addDelayedAction(action: () -> Unit) {
        if (!this.compoundRegistryCompleted) {
            this.recipesToRegister.add(action)
        }
        else {
            action()
        }
    }

    @SubscribeEvent
    @JvmStatic
    fun onCompoundRegistrationCompleted(ev: BaseTeslaRegistry.DefaultRegistrationCompletedEvent<CompoundMakerRecipe>) {
        this.compoundRegistryCompleted = true
        this.recipesToRegister.forEach { it() }
        this.recipesToRegister.clear()
    }
}
