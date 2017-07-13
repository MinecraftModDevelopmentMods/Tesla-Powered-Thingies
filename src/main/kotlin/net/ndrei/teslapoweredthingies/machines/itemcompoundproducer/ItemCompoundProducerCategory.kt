package net.ndrei.teslapoweredthingies.machines.itemliquefier

import mezz.jei.api.IModRegistry
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.integrations.jei.BaseCategory
import net.ndrei.teslapoweredthingies.integrations.jei.TeslaThingyJeiCategory
import net.ndrei.teslapoweredthingies.machines.itemcompoundproducer.ItemCompoundProducerBlock
import net.ndrei.teslapoweredthingies.machines.itemcompoundproducer.ItemCompoundProducerRecipe
import net.ndrei.teslapoweredthingies.machines.itemcompoundproducer.ItemCompoundProducerRecipes

/**
 * Created by CF on 2017-07-06.
 */
@TeslaThingyJeiCategory
object ItemCompoundProducerCategory
    : BaseCategory<ItemCompoundProducerCategory.RecipeWrapper>(ItemCompoundProducerBlock) {

    private lateinit var fluidOverlay: IDrawable

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: RecipeWrapper, ingredients: IIngredients) {
        val fluids = recipeLayout.fluidStacks
        fluids.init(0, true, 36, 18, 8, 27, 1000, true, fluidOverlay)
        fluids.set(0, ingredients.getInputs(FluidStack::class.java)[0])

        val stacks = recipeLayout.itemStacks
        stacks.init(0, false, 6, 23)
        stacks.set(0, ingredients.getInputs(ItemStack::class.java)[0])

        stacks.init(1, false, 56, 23)
        stacks.set(1, ingredients.getOutputs(ItemStack::class.java)[0])
    }

    class RecipeWrapper(val recipe: ItemCompoundProducerRecipe)
        : IRecipeWrapper {

        override fun getIngredients(ingredients: IIngredients) {
            ingredients.setInput(FluidStack::class.java, this.recipe.inputFluid.copy())
            ingredients.setInput(ItemStack::class.java, this.recipe.inputStack.copy())

            ingredients.setOutput(ItemStack::class.java, this.recipe.result.copy())
        }

        @SideOnly(Side.CLIENT)
        override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY)
        }
    }

    override fun register(registry: IRecipeCategoryRegistration) {
        super.register(registry)

        this.recipeBackground = this.guiHelper.createDrawable(Textures.JEI_TEXTURES.resource, 124, 66, 124, 66)
        fluidOverlay = this.guiHelper.createDrawable(Textures.JEI_TEXTURES.resource, 160, 84, 8, 27)
    }

    override fun register(registry: IModRegistry) {
        super.register(registry)

        registry.handleRecipes(ItemCompoundProducerRecipe::class.java, { RecipeWrapper(it) }, this.uid)
        registry.addRecipes(ItemCompoundProducerRecipes.recipes, this.uid)
    }
}