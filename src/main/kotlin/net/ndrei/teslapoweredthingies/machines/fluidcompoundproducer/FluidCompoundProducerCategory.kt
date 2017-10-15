package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import mezz.jei.api.IModRegistry
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslapoweredthingies.client.ThingiesTexture
import net.ndrei.teslapoweredthingies.integrations.jei.BaseCategory
import net.ndrei.teslapoweredthingies.integrations.jei.TeslaThingyJeiCategory

/**
 * Created by CF on 2017-07-06.
 */
@TeslaThingyJeiCategory
object FluidCompoundProducerCategory
    : BaseCategory<FluidCompoundProducerCategory.RecipeWrapper>(FluidCompoundProducerBlock) {

    private lateinit var fluidOverlay: IDrawable

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: RecipeWrapper, ingredients: IIngredients) {
        val fluids = recipeLayout.fluidStacks
        fluids.init(0, true, 8, 15, 8, 27, 1000, true, fluidOverlay)
        fluids.set(0, ingredients.getInputs(FluidStack::class.java)[0])

        fluids.init(1, true, 31, 15, 8, 27, 1000, true, fluidOverlay)
        fluids.set(1, ingredients.getInputs(FluidStack::class.java)[1])

        fluids.init(2, false, 54, 15, 8, 27, 1000, true, fluidOverlay)
        fluids.set(2, ingredients.getOutputs(FluidStack::class.java)[0])
    }

    class RecipeWrapper(val recipe: FluidCompoundProducerRecipe)
        : IRecipeWrapper {

        override fun getIngredients(ingredients: IIngredients) {
            ingredients.setInputs(FluidStack::class.java, mutableListOf(this.recipe.inputA.copy(), this.recipe.inputB.copy()))
            ingredients.setOutput(FluidStack::class.java, this.recipe.output.copy())
        }

        @SideOnly(Side.CLIENT)
        override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY)
        }
    }

    override fun register(registry: IRecipeCategoryRegistration) {
        super.register(registry)

        this.recipeBackground = this.guiHelper.createDrawable(ThingiesTexture.JEI_TEXTURES_2.resource, 124, 132, 124, 66)
        fluidOverlay = this.guiHelper.createDrawable(ThingiesTexture.JEI_TEXTURES_2.resource, 132, 147, 8, 27)
    }

    override fun register(registry: IModRegistry) {
        super.register(registry)

        registry.handleRecipes(FluidCompoundProducerRecipe::class.java, { RecipeWrapper(it) }, this.uid)
        registry.addRecipes(FluidCompoundProducerRegistry.getAllRecipes(), this.uid)
    }
}