package net.ndrei.teslapoweredthingies.machines.liquidxpstorage

import net.minecraft.client.renderer.GlStateManager
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.ButtonPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle

class LiquidXPStorageButton(left: Int, top: Int, private val text: String, private val tooltip: String, private val onClicked: () -> Unit) : ButtonPiece(left, top, 18, 18) {
    override fun clicked() {
        this.onClicked()
    }

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        container.bindDefaultTexture()
        container.drawTexturedRect(this.left, this.top, 78, 189, this.width, this.height)
    }

    override fun drawMiddleLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        if (this.isInside(container, mouseX, mouseY)) {
            container.drawFilledRect(guiX + this.left + 1, guiY + this.top + 1, this.width - 2, this.height - 2, 0xAEAEAE)
        }
    }

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        if (this.isInside(container, mouseX, mouseY)) {
            container.drawTooltip(listOf(this.tooltip), mouseX - guiX, mouseY - guiY)
        }
    }

    override fun renderState(container: BasicTeslaGuiContainer<*>, over: Boolean, box: BoundingRectangle) {
        val font = container.fontRenderer
        val width = font.getStringWidth(this.text)
        GlStateManager.pushMatrix()
        GlStateManager.translate((box.left + box.width / 2).toFloat(), (box.top + box.height / 2).toFloat(), 0.0f)
        GlStateManager.scale(0.8f, 0.8f, 1.0f)

        font.drawString(this.text, - width / 2, - font.FONT_HEIGHT / 2,0x242424)

        GlStateManager.popMatrix()
    }
}