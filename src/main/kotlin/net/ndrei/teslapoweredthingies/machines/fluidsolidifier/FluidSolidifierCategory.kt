package net.ndrei.teslapoweredthingies.machines.fluidsolidifier

import com.google.common.collect.Lists
import mezz.jei.api.IModRegistry
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.integrations.jei.BaseCategory
import net.ndrei.teslapoweredthingies.integrations.jei.TeslaThingyJeiCategory

/**
 * Created by CF on 2017-06-30.
 */
@TeslaThingyJeiCategory
object FluidSolidifierCategory
    : BaseCategory<FluidSolidifierCategory.FluidSolidifierRecipeWrapper>(FluidSolidifierBlock) {

    private lateinit var lavaOverlay: IDrawable
    private lateinit var waterOverlay: IDrawable

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: FluidSolidifierRecipeWrapper, ingredients: IIngredients) {
        val fluids = recipeLayout.fluidStacks

        fluids.init(0, true, 8, 15, 8, 27, recipeWrapper.recipe.lavaMbMin, true, lavaOverlay)
        fluids.set(0, ingredients.getInputs(FluidStack::class.java)[0])
        fluids.init(1, true, 20, 15, 8, 27, recipeWrapper.recipe.waterMbMin, true, waterOverlay)
        fluids.set(1, ingredients.getInputs(FluidStack::class.java)[1])

        fluids.init(2, true, 43, 15, 8, 27, recipeWrapper.recipe.lavaMbMin, true, lavaOverlay)
        fluids.set(2, FluidStack(FluidRegistry.LAVA, recipeWrapper.recipe.lavaMbConsumed))
        fluids.init(3, true, 55, 15, 8, 27, recipeWrapper.recipe.waterMbMin, true, waterOverlay)
        fluids.set(3, FluidStack(FluidRegistry.WATER, recipeWrapper.recipe.waterMbConsumed))

        val stacks = recipeLayout.itemStacks
        stacks.init(0, false, 77, 20)
        stacks.set(0, recipeWrapper.recipe.resultStack)
    }

    class FluidSolidifierRecipeWrapper(val recipe: FluidSolidifierResult)
        : IRecipeWrapper {

        override fun getIngredients(ingredients: IIngredients) {
            ingredients.setInputs(FluidStack::class.java, Lists.newArrayList(
                    FluidStack(FluidRegistry.LAVA, this.recipe.lavaMbMin),
                    FluidStack(FluidRegistry.WATER, this.recipe.waterMbMin)
            ))
            ingredients.setOutput(ItemStack::class.java, this.recipe.resultStack)
        }

        @SideOnly(Side.CLIENT)
        override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY)

            val required = "required"
            val consumed = "consumed"
            minecraft.fontRenderer.drawString(required, 18 - minecraft.fontRenderer.getStringWidth(required) / 2, 8 - minecraft.fontRenderer.FONT_HEIGHT, 0x424242)
            minecraft.fontRenderer.drawString(consumed, 54 - minecraft.fontRenderer.getStringWidth(consumed) / 2, 47, 0x424242)
        }
    }

    override fun register(registry: IRecipeCategoryRegistration) {
        super.register(registry)

        this.recipeBackground = this.guiHelper.createDrawable(Textures.JEI_TEXTURES.resource, 0, 132, 124, 66)
        lavaOverlay = this.guiHelper.createDrawable(Textures.JEI_TEXTURES.resource, 8, 147, 8, 27)
        waterOverlay = this.guiHelper.createDrawable(Textures.JEI_TEXTURES.resource, 20, 147, 8, 27)
    }

    override fun register(registry: IModRegistry) {
        super.register(registry)

        registry.handleRecipes(FluidSolidifierResult::class.java, { FluidSolidifierRecipeWrapper(it) }, this.uid)
        registry.addRecipes(FluidSolidifierResult.values().toMutableList(), this.uid)
    }
}
