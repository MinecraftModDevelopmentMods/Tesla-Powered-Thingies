package net.ndrei.teslapoweredthingies.integrations.jei

import com.google.common.collect.Lists
import mezz.jei.api.IGuiHelper
import mezz.jei.api.IModRegistry
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredientRenderer
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.SecondaryOutput
import net.ndrei.teslapoweredthingies.machines.IncineratorBlock
import net.ndrei.teslapoweredthingies.machines.incinerator.IncineratorRecipe
import net.ndrei.teslapoweredthingies.machines.incinerator.IncineratorRecipes

/**
 * Created by CF on 2017-06-30.
 */
@TeslaThingyJeiCategory
object IncineratorCategory
    : BaseCategory<IncineratorCategory.IncineratorRecipeWrapper>(IncineratorBlock) {

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IncineratorRecipeWrapper, ingredients: IIngredients) {
        val stacks = recipeLayout.itemStacks

        stacks.init(0, true, 6, 6)
        stacks.set(0, ingredients.getInputs(ItemStack::class.java)[0])

        val recipe = recipeWrapper.recipe
        if (recipe.secondaryOutputs != null && recipe.secondaryOutputs.size > 0) {
            var index = 1
            for (so in recipe.secondaryOutputs) {
                stacks.init(index, false, object : IIngredientRenderer<ItemStack> {
                    private var so: SecondaryOutput? = null

                    @SideOnly(Side.CLIENT)
                    override fun render(minecraft: Minecraft, xPosition: Int, yPosition: Int, ingredient: ItemStack?) {
                        minecraft.renderItem.renderItemIntoGUI(ingredient!!, xPosition, yPosition)
                        minecraft.renderItem.renderItemOverlayIntoGUI(minecraft.fontRenderer, ingredient, xPosition + 1, yPosition + 1, null)

                        val percent = "" + Math.round(so!!.chance * 100.0f) + "%"
                        GlStateManager.pushMatrix()
                        GlStateManager.translate((xPosition + 8).toFloat(), (yPosition + 20).toFloat(), 0f)
                        GlStateManager.scale(.75f, .75f, 1f)
                        minecraft.fontRenderer.drawString(percent,
                                -minecraft.fontRenderer.getStringWidth(percent) / 2,
                                0, 0x424242)
                        GlStateManager.popMatrix()
                    }

                    @SideOnly(Side.CLIENT)
                    override fun getTooltip(minecraft: Minecraft, ingredient: ItemStack, tooltipFlag: ITooltipFlag): List<String> {
                        return ingredient.getTooltip(minecraft.player, tooltipFlag)
                    }

                    override fun getFontRenderer(minecraft: Minecraft, ingredient: ItemStack): FontRenderer {
                        return minecraft.fontRenderer
                    }

                    internal fun init(so: SecondaryOutput): IIngredientRenderer<ItemStack> {
                        this.so = so
                        return this
                    }
                }.init(so), 6 + (index - 1) * 18, 45, 18, 18, 1, 1)
                stacks.set(index, so.stack)
                stacks.setBackground(index, this.slotBackground!!)
                index++
            }
        }
    }

    class IncineratorRecipeWrapper(val recipe: IncineratorRecipe)
        : IRecipeWrapper {

        override fun getIngredients(ingredients: IIngredients) {
            ingredients.setInput(ItemStack::class.java, this.recipe.input)
            if (this.recipe.secondaryOutputs != null && this.recipe.secondaryOutputs.size > 0) {
                val secondary = Lists.newArrayList<ItemStack>()
                for (so in this.recipe.secondaryOutputs) {
                    secondary.add(so.stack.copy())
                }
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

        this.recipeBackground = this.guiHelper.createDrawable(TeslaThingiesMod.JEI_TEXTURES, 0, 0, 124, 66)
    }

    override fun register(registry: IModRegistry) {
        super.register(registry)

        registry.handleRecipes(IncineratorRecipe::class.java, { IncineratorRecipeWrapper(it) }, this.uid)
        registry.addRecipes(IncineratorRecipes.getRecipes(), this.uid)
    }
}
