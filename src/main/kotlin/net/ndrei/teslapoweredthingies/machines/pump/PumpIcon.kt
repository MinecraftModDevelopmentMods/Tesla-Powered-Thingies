package net.ndrei.teslapoweredthingies.machines.pump

import net.ndrei.teslacorelib.gui.IGuiIcon
import net.ndrei.teslacorelib.gui.IGuiTexture
import net.ndrei.teslapoweredthingies.client.ThingiesTexture

enum class PumpIcon(override val texture: IGuiTexture, override val left: Int, override val top: Int, override val width: Int, override val height: Int): IGuiIcon {
    MODE_AUTO(ThingiesTexture.MACHINES_TEXTURES, 102, 138, 10, 10),
    MODE_FLUID(ThingiesTexture.MACHINES_TEXTURES, 102, 114, 10, 10),
    MODE_BLOCKS(ThingiesTexture.MACHINES_TEXTURES, 102, 126, 10, 10)
}