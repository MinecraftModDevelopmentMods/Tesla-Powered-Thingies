package net.ndrei.teslapoweredthingies.machines.poweredkiln

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.ndrei.teslacorelib.utils.copyWithSize
import net.ndrei.teslacorelib.utils.equalsIgnoreSize

/**
 * Created by CF on 2017-07-06.
 */
object PoweredKilnRecipes {
    private val recipes = mutableListOf<PoweredKilnRecipe>()

    fun registerRecipe(input: ItemStack, output: ItemStack) {
        this.recipes.add(PoweredKilnRecipe(input, output))
    }

    fun getRecipes() = this.recipes.toList()

    fun findRecipe(input: ItemStack)
            = this.recipes.firstOrNull { it.input.equalsIgnoreSize(input) } ?:
            FurnaceRecipes.instance().getSmeltingResult(input).let {
                if (it.isEmpty) null
                else PoweredKilnRecipe(input.copyWithSize(1), it)
            }

    fun hasRecipe(stack: ItemStack) = (this.findRecipe(stack) != null)
}