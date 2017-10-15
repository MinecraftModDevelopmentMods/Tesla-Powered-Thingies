package net.ndrei.teslapoweredthingies.machines.fluidburner

import com.google.common.collect.Lists
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
 * Created by CF on 2017-06-30.
 */
@TeslaThingyJeiCategory
object FluidBurnerCategory
    : BaseCategory<FluidBurnerCategory.FluidBurnerRecipeWrapper>(FluidBurnerBlock) {

    private lateinit var fuelOverlay: IDrawable
    private lateinit var coolantOverlay: IDrawable

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: FluidBurnerRecipeWrapper, ingredients: IIngredients) {
        val fluids = recipeLayout.fluidStacks

        val capacity = if (recipeWrapper.coolant != null)
            Math.max(recipeWrapper.fuel.amount, recipeWrapper.coolant.amount)
        else
            recipeWrapper.fuel.amount
        fluids.init(0, true, 8, 8, 8, 27, capacity, false, fuelOverlay)
        fluids.set(0, ingredients.getInputs(FluidStack::class.java)[0])
        if (ingredients.getInputs(FluidStack::class.java).size == 2) {
            fluids.init(1, true, 20, 8, 8, 27, capacity, false, coolantOverlay)
            fluids.set(1, ingredients.getInputs(FluidStack::class.java)[1])
        }
    }

    class FluidBurnerRecipeWrapper(val fuel: FluidBurnerFuelRecipe, val coolant: FluidBurnerCoolantRecipe?)
        : IRecipeWrapper {

        override fun getIngredients(ingredients: IIngredients) {
            if (this.coolant == null) {
                ingredients.setInput(FluidStack::class.java, FluidStack(this.fuel.fluid, this.fuel.amount))
            } else {
                ingredients.setInputs(FluidStack::class.java, Lists.newArrayList(
                        FluidStack(this.fuel.fluid, this.fuel.amount),
                        FluidStack(this.coolant.fluid, this.coolant.amount)
                ))
            }
        }

        @SideOnly(Side.CLIENT)
        override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY)

            var ticks = this.fuel.baseTicks
            if (this.coolant != null)
                ticks = Math.round(ticks.toFloat() * this.coolant.timeMultiplier)

            val duration = String.format("%,d ticks", ticks)
            val power = String.format("%,d T", ticks * 80)
            minecraft.fontRenderer.drawString(duration, 36, 12, 0x007F7F)
            minecraft.fontRenderer.drawString(power, 36, 12 + minecraft.fontRenderer.FONT_HEIGHT, 0x007F7F)
        }
    }

    override fun register(registry: IRecipeCategoryRegistration) {
        super.register(registry)

        this.recipeBackground = this.guiHelper.createDrawable(ThingiesTexture.JEI_TEXTURES.resource, 0, 66, 124, 66)
        fuelOverlay = this.guiHelper.createDrawable(ThingiesTexture.JEI_TEXTURES.resource, 8, 74, 8, 27)
        coolantOverlay = this.guiHelper.createDrawable(ThingiesTexture.JEI_TEXTURES.resource, 20, 74, 8, 27)
    }

    override fun register(registry: IModRegistry) {
        super.register(registry)

        val recipes = Lists.newArrayList<FluidBurnerRecipeWrapper>()
        for (fuel in FluidBurnerRecipes.fuels) {
            recipes.add(FluidBurnerRecipeWrapper(fuel, null))
            FluidBurnerRecipes.coolants.mapTo(recipes) { FluidBurnerRecipeWrapper(fuel, it) }
        }
        registry.addRecipes(recipes, this.uid)
    }
}
