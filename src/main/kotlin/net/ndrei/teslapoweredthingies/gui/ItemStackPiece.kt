package net.ndrei.teslapoweredthingies.gui

import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.ndrei.teslacorelib.compatibility.FontRendererUtil
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.gui.BasicContainerGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.render.GhostedItemRenderer

/**
 * Created by CF on 2017-06-30.
 */
open class ItemStackPiece(left: Int, top: Int, width: Int, height: Int, private val provider: IWorkItemProvider, private val alpha: Float = 1.0f)
    : BasicContainerGuiPiece(left, top, width, height) {

    override fun drawMiddleLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        val stack = if (this.provider == null) ItemStack.EMPTY else this.provider.workItem
        if (!ItemStackUtil.isEmpty(stack)) {
            val x = this.left + (this.width - 16) / 2
            val y = this.top + (this.height - 16) / 2

            if (this.alpha >= 1.0f) {
                RenderHelper.enableGUIStandardItemLighting()
                container.itemRenderer.renderItemAndEffectIntoGUI(stack, guiX + x, guiY + y)
                container.itemRenderer.renderItemOverlayIntoGUI(FontRendererUtil.fontRenderer, stack, x, y, null)
                RenderHelper.disableStandardItemLighting()
            }
            else {
                RenderHelper.enableGUIStandardItemLighting()
                GhostedItemRenderer.renderItemInGUI(container.itemRenderer, stack, guiX + x, guiY + y, this.alpha)
                RenderHelper.disableStandardItemLighting()
            }
        }
    }
}
