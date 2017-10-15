package net.ndrei.teslapoweredthingies.machines.poweredkiln

import net.ndrei.teslacorelib.gui.BasicContainerGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslapoweredthingies.client.ThingiesTexture

/**
 * Created by CF on 2017-07-14.
 */
class FurnaceBurnPiece(left: Int, top: Int, private val stateGetter: () -> Boolean)
    : BasicContainerGuiPiece(left, top, 14, 14) {

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        ThingiesTexture.MACHINES_TEXTURES.bind(container)

        container.drawTexturedRect(this.left, this.top, if (this.stateGetter()) 8 else 44, 27, 14, 14)
    }
}