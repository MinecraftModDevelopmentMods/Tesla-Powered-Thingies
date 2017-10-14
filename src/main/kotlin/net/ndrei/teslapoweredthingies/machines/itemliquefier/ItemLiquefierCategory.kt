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

/**
 * Created by CF on 2017-07-06.
 */
@TeslaThingyJeiCategory
object ItemLiquefierCategory
    : BaseCategory<ItemLiquefierCategory.ItemLiquefierCategoryWrapper>(ItemLiquefierBlock) {

    private lateinit var fluidOverlay: IDrawable

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: ItemLiquefierCategoryWrapper, ingredients: IIngredients) {
        val fluids = recipeLayout.fluidStacks

        fluids.init(0, true, 36, 18, 8, 27, 1000, true, fluidOverlay)
        fluids.set(0, ingredients.getOutputs(FluidStack::class.java)[0])

        val stacks = recipeLayout.itemStacks
        stacks.init(0, false, 6, 23)
        stacks.set(0, ingredients.getInputs(ItemStack::class.java)[0])
    }

    class ItemLiquefierCategoryWrapper(val recipe: LiquefierRecipe)
        : IRecipeWrapper {

        override fun getIngredients(ingredients: IIngredients) {
            ingredients.setInput(ItemStack::class.java, this.recipe.input.copy()) // ItemStack(this.recipe.input, this.recipe.inputStackSize))
            ingredients.setOutput(FluidStack::class.java, this.recipe.output.copy()) // FluidStack(this.recipe.output, this.recipe.outputQuantity))
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

        registry.handleRecipes(LiquefierRecipe::class.java, { ItemLiquefierCategoryWrapper(it) }, this.uid)
        registry.addRecipes(ItemLiquefierRegistry.getAllRecipes(), this.uid)
    }
}