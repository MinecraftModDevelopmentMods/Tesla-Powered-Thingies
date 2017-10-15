package net.ndrei.teslapoweredthingies.machines.compoundmaker

import net.minecraft.client.Minecraft
import net.minecraft.util.text.TextFormatting
import net.ndrei.teslacorelib.gui.BasicContainerGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslapoweredthingies.client.ThingiesTexture

class CompoundRecipeSelectorPiece(private val entity: CompoundMakerEntity, left: Int = 149, top: Int = 33) : BasicContainerGuiPiece(left, top, 18, 18) {
    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY)

        (if (!this.entity.hasCurrentRecipe) CompoundMakerIcon.RECIPE_DISPLAY_AREA_GRAYED else CompoundMakerIcon.RECIPE_DISPLAY_AREA)
            .drawCentered(container, this)
    }

    override fun drawForegroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        super.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY)

        val recipe = this.entity.selectedRecipe
        if (recipe != null) {
            Minecraft.getMinecraft().renderItem.renderItemIntoGUI(recipe.output, this.left + 1, this.top + 1)
        }
    }

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        super.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY)

        if (this.isInside(container, mouseX, mouseY)) {
            val recipe = this.entity.selectedRecipe

            container.drawTooltip(if (recipe != null) listOf(
                "${TextFormatting.LIGHT_PURPLE}Making:${TextFormatting.WHITE} ${recipe.output.displayName}"
            ) else listOf("${TextFormatting.GRAY}No recipe available"), mouseX - guiX, mouseY - guiY)
        }
    }
}
