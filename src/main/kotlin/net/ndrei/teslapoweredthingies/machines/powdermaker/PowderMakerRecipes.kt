package net.ndrei.teslapoweredthingies.machines.powdermaker

import net.minecraft.item.ItemStack

/**
 * Created by CF on 2017-07-05.
 */
object PowderMakerRecipes {
    private val recipes = mutableListOf<IPowderMakerRecipe>()

    fun registerRecipe(recipe: IPowderMakerRecipe) {
        this.recipes.add(recipe)
    }

    fun getRecipes() = this.recipes.toList()

    fun findRecipe(stack: ItemStack): IPowderMakerRecipe?
            = this.recipes.firstOrNull { it.canProcess(stack) }
}