package net.ndrei.teslapoweredthingies.machines.incinerator

import com.google.common.collect.Lists
import mezz.jei.api.IModRegistry
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.integrations.jei.BaseCategory
import net.ndrei.teslapoweredthingies.integrations.jei.ChanceOutputRenderer
import net.ndrei.teslapoweredthingies.integrations.jei.TeslaThingyJeiCategory

/**
 * Created by CF on 2017-06-30.
 */
@Suppress("unused")
@TeslaThingyJeiCategory
object IncineratorCategory
    : BaseCategory<IncineratorCategory.IncineratorRecipeWrapper>(IncineratorBlock) {

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IncineratorRecipeWrapper, ingredients: IIngredients) {
        val stacks = recipeLayout.itemStacks

        stacks.init(0, true, 6, 6)
        stacks.set(0, ingredients.getInputs(ItemStack::class.java)[0])

        val recipe = recipeWrapper.recipe
        if (recipe.secondaryOutputs.isNotEmpty()) {
            var index = 1
            recipe.secondaryOutputs.forEach {
                stacks.init(index, false, ChanceOutputRenderer(it),
                        6 + (index - 1) * 18, 45, 18, 18, 1, 1)
                stacks.set(index, it.getPossibleOutput())
                stacks.setBackground(index, this.slotBackground)
                index++
            }
        }
    }

    class IncineratorRecipeWrapper(val recipe: IncineratorRecipe)
        : IRecipeWrapper {

        override fun getIngredients(ingredients: IIngredients) {
            ingredients.setInput(ItemStack::class.java, this.recipe.input)
            if (this.recipe.secondaryOutputs.isNotEmpty()) {
                val secondary = Lists.newArrayList<ItemStack>()
                this.recipe.secondaryOutputs
                    .mapTo(secondary) { it.getPossibleOutput().copy() }
                ingredients.setOutputs(ItemStack::class.java, secondary)
            }
        }

        @SideOnly(Side.CLIENT)
        override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY)

            val power = String.format("%,d T", this.recipe.power)
            minecraft.fontRenderer.drawString(power, 44, 15 - minecraft.fontRenderer.FONT_HEIGHT / 2, 0x007F7F)
        }
    }

    override fun register(registry: IRecipeCategoryRegistration) {
        super.register(registry)

        this.recipeBackground = this.guiHelper.createDrawable(Textures.JEI_TEXTURES.resource, 0, 0, 124, 66)
    }

    override fun register(registry: IModRegistry) {
        super.register(registry)

        registry.handleRecipes(IncineratorRecipe::class.java, { IncineratorRecipeWrapper(it) }, this.uid)
        registry.addRecipes(IncineratorRegistry.getAllRecipes(), this.uid)
    }
}
