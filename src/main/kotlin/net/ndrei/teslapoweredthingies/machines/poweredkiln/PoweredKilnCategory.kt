package net.ndrei.teslapoweredthingies.machines.poweredkiln

import mezz.jei.api.IModRegistry
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.item.ItemStack
import net.ndrei.teslapoweredthingies.client.ThingiesTexture
import net.ndrei.teslapoweredthingies.integrations.jei.BaseCategory
import net.ndrei.teslapoweredthingies.integrations.jei.TeslaThingyJeiCategory

/**
 * Created by CF on 2017-07-06.
 */
@TeslaThingyJeiCategory
object PoweredKilnCategory
    : BaseCategory<PoweredKilnCategory.PoweredKilnRecipeWrapper>(PoweredKilnBlock) {

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: PoweredKilnRecipeWrapper, ingredients: IIngredients) {
        val stacks = recipeLayout.itemStacks

        stacks.init(0, true, 6, 6)
        stacks.set(0, ingredients.getInputs(ItemStack::class.java)[0])

        stacks.init(1, false, 45, 6)
        stacks.set(1, ingredients.getOutputs(ItemStack::class.java)[0])
        stacks.setBackground(1, this.slotBackground)
    }

    class PoweredKilnRecipeWrapper(val recipe: PoweredKilnRecipe)
        : IRecipeWrapper {

        override fun getIngredients(ingredients: IIngredients) {
            ingredients.setInput(ItemStack::class.java, this.recipe.input)
            ingredients.setOutput(ItemStack::class.java, this.recipe.output)
        }
    }

    override fun register(registry: IRecipeCategoryRegistration) {
        super.register(registry)

        this.recipeBackground = this.guiHelper.createDrawable(ThingiesTexture.JEI_TEXTURES.resource, 0, 0, 124, 66)
    }

    override fun register(registry: IModRegistry) {
        super.register(registry)

        registry.handleRecipes(PoweredKilnRecipe::class.java, { PoweredKilnRecipeWrapper(it) }, this.uid)
        registry.addRecipes(PoweredKilnRegistry.getRecipes(), this.uid)
    }
}