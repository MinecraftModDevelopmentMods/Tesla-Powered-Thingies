package net.ndrei.teslapoweredthingies.common.gui

import net.minecraft.block.Block
import net.minecraft.client.renderer.GlStateManager
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.SideDrawerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslapoweredthingies.client.ThingiesTexture
import net.ndrei.teslapoweredthingies.integrations.GUI_BUTTONS
import net.ndrei.teslapoweredthingies.integrations.jei.TheJeiThing
import net.ndrei.teslapoweredthingies.integrations.localize

/**
 * Created by CF on 2017-07-06.
 */
class OpenJEICategoryPiece(private val block: Block, topIndex: Int = 1) : SideDrawerPiece(topIndex) {
    override val isVisible: Boolean
        get() = TheJeiThing.isJeiAvailable && TheJeiThing.isBlockRegistered(this.block)

    override fun renderState(container: BasicTeslaGuiContainer<*>, state: Int, box: BoundingRectangle) {
        ThingiesTexture.MACHINES_TEXTURES.bind(container)

        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        container.drawTexturedModalRect(
                box.left, box.top + 1,
                81, 7,
                14, 14)
        GlStateManager.disableBlend()
    }

    override fun getStateToolTip(state: Int)
        = listOf(localize(GUI_BUTTONS, "open jei"))

    override fun clicked() {
        TheJeiThing.showCategory(this.block)
    }
}
