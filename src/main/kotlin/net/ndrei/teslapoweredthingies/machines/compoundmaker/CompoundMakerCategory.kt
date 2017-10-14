package net.ndrei.teslapoweredthingies.machines.compoundmaker

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

/**
 * Created by CF on 2017-07-06.
 */
@TeslaThingyJeiCategory
object CompoundMakerCategory
    : BaseCategory<CompoundMakerCategory.RecipeWrapper>(CompoundMakerBlock) {

    private lateinit var fluidOverlay: IDrawable

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: RecipeWrapper, ingredients: IIngredients) {
        val fluids = recipeLayout.fluidStacks
        fluids.init(0, true, 8, 15, 8, 27, 1000, true, fluidOverlay)
        fluids.set(0, ingredients.getInputs(FluidStack::class.java)[0])

        val stacks = recipeLayout.itemStacks
//        stacks.init(0, true, 30, 20)
//        stacks.set(0, ingredients.getInputs(ItemStack::class.java)[0])

        stacks.init(1, false, 61, 20)
        stacks.set(1, ingredients.getOutputs(ItemStack::class.java)[0])
    }

    class RecipeWrapper(val recipe: CompoundMakerRecipe)
        : IRecipeWrapper {

        override fun getIngredients(ingredients: IIngredients) {
            val fluids = mutableListOf<FluidStack>()
            listOf(this.recipe.left, this.recipe.right).mapNotNullTo(fluids) { it?.copy() }
            if (fluids.isNotEmpty()) {
                ingredients.setInputs(FluidStack::class.java, fluids)
            }

//            ingredients.setInput(ItemStack::class.java, this.recipe.inputStack.copy())

            ingredients.setOutput(ItemStack::class.java, this.recipe.output.copy())
        }

        @SideOnly(Side.CLIENT)
        override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY)
        }
    }

    override fun register(registry: IRecipeCategoryRegistration) {
        super.register(registry)

        this.recipeBackground = this.guiHelper.createDrawable(Textures.JEI_TEXTURES.resource, 124, 132, 124, 66)
        fluidOverlay = this.guiHelper.createDrawable(Textures.JEI_TEXTURES.resource, 132, 147, 8, 27)
    }

    override fun register(registry: IModRegistry) {
        super.register(registry)

        registry.handleRecipes(CompoundMakerRecipe::class.java, { RecipeWrapper(it) }, this.uid)
        registry.addRecipes(CompoundMakerRegistry.getAllRecipes(), this.uid)
    }
}
