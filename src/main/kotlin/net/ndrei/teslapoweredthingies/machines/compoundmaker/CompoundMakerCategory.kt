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
import net.ndrei.teslacorelib.utils.isEmpty
import net.ndrei.teslapoweredthingies.client.ThingiesTexture
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
        val recipe = recipeWrapper.recipe

        val fluids = recipeLayout.fluidStacks
        var fluidIndex = 0
        if (!recipe.left.isEmpty) {
            fluids.init(0, true, 5, 15, 8, 27, 1000, true, fluidOverlay)
            fluids.set(0, recipe.left)
            fluidIndex = 1
        }
        if (!recipe.right.isEmpty) {
            fluids.init(fluidIndex, true, 71, 15, 8, 27, 1000, true, fluidOverlay)
            fluids.set(fluidIndex, recipe.right)
        }

        val stacks = recipeLayout.itemStacks
        var stackIndex = 0
        recipe.top.forEachIndexed { i, it ->
            stacks.init(stackIndex, true, 15 + i * 18, 6)
            stacks.set(stackIndex, it)
            stackIndex++
        }
        recipe.bottom.forEachIndexed { i, it ->
            stacks.init(stackIndex, true, 15 + i * 18, 33)
            stacks.set(stackIndex, it)
            stackIndex++
        }

        stacks.init(stackIndex, false, 92, 20)
        stacks.set(stackIndex, recipe.output)
    }

    class RecipeWrapper(val recipe: CompoundMakerRecipe)
        : IRecipeWrapper {

        override fun getIngredients(ingredients: IIngredients) {
            val fluids = mutableListOf<FluidStack>()
            listOf(this.recipe.left, this.recipe.right).mapNotNullTo(fluids) { it?.copy() }
            if (fluids.isNotEmpty()) {
                ingredients.setInputs(FluidStack::class.java, fluids)
            }
            val items = mutableListOf<ItemStack>()
            items.addAll(this.recipe.top)
            items.addAll(this.recipe.bottom)
            if (items.isNotEmpty()) {
                ingredients.setInputs(ItemStack::class.java, items.map { it.copy() })
            }

            ingredients.setOutput(ItemStack::class.java, this.recipe.output.copy())
        }

        @SideOnly(Side.CLIENT)
        override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY)
        }
    }

    override fun register(registry: IRecipeCategoryRegistration) {
        super.register(registry)

        this.recipeBackground = this.guiHelper.createDrawable(ThingiesTexture.JEI_TEXTURES_2.resource, 124, 66, 124, 57)
        fluidOverlay = this.guiHelper.createDrawable(ThingiesTexture.JEI_TEXTURES.resource, 132, 147, 8, 27)
    }

    override fun register(registry: IModRegistry) {
        super.register(registry)

        registry.handleRecipes(CompoundMakerRecipe::class.java, { RecipeWrapper(it) }, this.uid)
        registry.addRecipes(CompoundMakerRegistry.getAllRecipes(), this.uid)
    }
}
