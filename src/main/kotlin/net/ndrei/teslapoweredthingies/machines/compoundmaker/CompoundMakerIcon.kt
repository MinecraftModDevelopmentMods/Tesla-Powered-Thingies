package net.ndrei.teslapoweredthingies.machines.compoundmaker

import net.ndrei.teslacorelib.gui.IGuiIcon
import net.ndrei.teslacorelib.gui.IGuiTexture
import net.ndrei.teslapoweredthingies.client.ThingiesTexture

enum class CompoundMakerIcon(override val texture: IGuiTexture, override val left: Int, override val top: Int, override val width: Int, override val height: Int): IGuiIcon {
    CENTER_BACKGROUND(ThingiesTexture.MACHINES_TEXTURES, 5, 105, 54, 24),

    BUTTON_BACKGROUND(ThingiesTexture.MACHINES_TEXTURES, 6, 132, 14, 7),
    RECIPE_BUTTON_UP(ThingiesTexture.MACHINES_TEXTURES, 33, 132, 6, 3),
    RECIPE_BUTTON_UP_GRAYED(ThingiesTexture.MACHINES_TEXTURES, 25, 132, 6, 3),
    RECIPE_BUTTON_DOWN(ThingiesTexture.MACHINES_TEXTURES, 33, 136, 6, 3),
    RECIPE_BUTTON_DOWN_GRAYED(ThingiesTexture.MACHINES_TEXTURES, 25, 136, 6, 3),

    RECIPE_DISPLAY_AREA(ThingiesTexture.MACHINES_TEXTURES, 23, 140, 18, 18),
    RECIPE_DISPLAY_AREA_GRAYED(ThingiesTexture.MACHINES_TEXTURES, 4, 140, 18, 18),

    TRIGGER_PAUSED(ThingiesTexture.MACHINES_TEXTURES, 46, 152, 6, 8),
    TRIGGER_ONE(ThingiesTexture.MACHINES_TEXTURES, 46, 143, 6, 8),
    TRIGGER_ALL(ThingiesTexture.MACHINES_TEXTURES, 45, 134, 10, 8)
}
