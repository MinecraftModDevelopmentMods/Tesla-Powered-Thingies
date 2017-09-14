package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslacorelib.utils.equalsIgnoreSize

/**
 * Created by CF on 2017-07-13.
 */
object ItemCompoundProducerRecipes {
    val recipes = mutableListOf<ItemCompoundProducerRecipe>()

    private fun ItemCompoundProducerRecipe.matchesInput(fluid: FluidStack, ignoreSize: Boolean = true)
        = this.inputFluid.isFluidEqual(fluid) && (ignoreSize || (this.inputFluid.amount <= fluid.amount))

    private fun ItemCompoundProducerRecipe.matchesInput(stack: ItemStack, ignoreSize: Boolean = true)
        = this.inputStack.equalsIgnoreSize(stack) && (ignoreSize || (this.inputStack.count <= stack.count))

    fun hasRecipe(fluid: FluidStack) = this.recipes.any { it.matchesInput(fluid) }

    fun hasRecipe(stack: ItemStack) = this.recipes.any { it.matchesInput(stack) }

    fun findRecipe(fluid: FluidStack, stack: ItemStack)
        = this.recipes.firstOrNull { it.matchesInput(fluid, false) && it.matchesInput(stack, false) }
}