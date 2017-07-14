package net.ndrei.teslapoweredthingies.gui

import net.ndrei.teslacorelib.gui.BasicContainerGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslapoweredthingies.client.Textures

/**
 * Created by CF on 2017-07-14.
 */
class FurnaceBurnPiece(left: Int, top: Int, private val stateGetter: () -> Boolean)
    : BasicContainerGuiPiece(left, top, 14, 14) {

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        Textures.MACHINES_TEXTURES.bind(container)

        container.drawTexturedRect(this.left, this.top, if (this.stateGetter()) 8 else 44, 27, 14, 14)
    }
}