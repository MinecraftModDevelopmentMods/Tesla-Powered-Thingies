package net.ndrei.teslapoweredthingies.machines.powdermaker

import mezz.jei.api.IModRegistry
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.item.ItemStack
import net.ndrei.teslapoweredthingies.client.ThingiesTexture
import net.ndrei.teslapoweredthingies.integrations.jei.BaseCategory
import net.ndrei.teslapoweredthingies.integrations.jei.ChanceOutputRenderer
import net.ndrei.teslapoweredthingies.integrations.jei.TeslaThingyJeiCategory

/**
 * Created by CF on 2017-07-05.
 */
@TeslaThingyJeiCategory
object PowderMakerCategory
    : BaseCategory<PowderMakerCategory.PowderMakerRecipeWrapper>(PowderMakerBlock) {

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: PowderMakerRecipeWrapper, ingredients: IIngredients) {
        val stacks = recipeLayout.itemStacks

        stacks.init(0, true, 6, 23)
        stacks.set(0, ingredients.getInputs(ItemStack::class.java)[0])

        val recipe = recipeWrapper.recipe
        val outputs = recipe.getOutputs()
        if (outputs.isNotEmpty()) {
            var index = 1
            for (so in outputs) {
                stacks.init(index, false, ChanceOutputRenderer(so),
                        36 + (index - 1) * 18, 23, 18, 18, 1, 1)
                stacks.set(index, so.getPossibleOutput())
                stacks.setBackground(index, this.slotBackground)
                index++
            }
        }
    }

    class PowderMakerRecipeWrapper(val recipe: PowderMakerRecipe)
        : IRecipeWrapper {

        override fun getIngredients(ingredients: IIngredients) {
            ingredients.setInputs(ItemStack::class.java, this.recipe.getPossibleInputs())
            ingredients.setOutputLists(ItemStack::class.java, this.recipe.getPossibleOutputs())
        }
    }

    override fun register(registry: IRecipeCategoryRegistration) {
        super.register(registry)

        this.recipeBackground = this.guiHelper.createDrawable(ThingiesTexture.JEI_TEXTURES.resource, 124, 0, 124, 66)
    }

    override fun register(registry: IModRegistry) {
        super.register(registry)

        registry.handleRecipes(PowderMakerRecipe::class.java, { PowderMakerRecipeWrapper(it) }, this.uid)
        registry.addRecipes(PowderMakerRegistry.getAllRecipes(), this.uid)
    }
}