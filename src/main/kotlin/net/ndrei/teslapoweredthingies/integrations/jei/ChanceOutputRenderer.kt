package net.ndrei.teslapoweredthingies.integrations.jei

import mezz.jei.api.ingredients.IIngredientRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.ndrei.teslapoweredthingies.common.IRecipeOutput
import net.ndrei.teslapoweredthingies.common.SecondaryOutput

/**
 * Created by CF on 2017-07-06.
 */
class ChanceOutputRenderer(val so: IRecipeOutput): IIngredientRenderer<ItemStack> {
    override fun getTooltip(minecraft: Minecraft, ingredient: ItemStack, tooltipFlag: ITooltipFlag)
            = ingredient.getTooltip(minecraft.player, tooltipFlag)!!

    override fun render(minecraft: Minecraft, xPosition: Int, yPosition: Int, ingredient: ItemStack?) {
        if ((ingredient != null) && !ingredient.isEmpty) {
            RenderHelper.enableGUIStandardItemLighting()
            minecraft.renderItem.renderItemAndEffectIntoGUI(ingredient, xPosition, yPosition)
            minecraft.renderItem.renderItemOverlayIntoGUI(minecraft.fontRenderer, ingredient, xPosition + 1, yPosition + 1, null)

            val percent = when (this.so) {
                is SecondaryOutput -> "${Math.round(this.so.chance * 100f)}%"
                else -> "100%"
            }
            GlStateManager.pushMatrix()
            GlStateManager.translate((xPosition + 8).toFloat(), (yPosition + 20).toFloat(), 0f)
            GlStateManager.scale(.60f, .60f, 1f)
            minecraft.fontRenderer.drawString(percent,
                    -minecraft.fontRenderer.getStringWidth(percent) / 2,
                    0, 0x424242)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.popMatrix()
        }
    }
}