package net.ndrei.teslapoweredthingies.machines.animalgym

import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextFormatting
import net.ndrei.teslacorelib.compatibility.FontRendererUtil
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.EnergyDisplayType
import net.ndrei.teslapoweredthingies.client.ThingiesTexture
import net.ndrei.teslapoweredthingies.integrations.GUI_ANIMAL_GYM
import net.ndrei.teslapoweredthingies.integrations.localize

/**
 * Created by CF on 2017-07-08.
 */
class AnimalGymInfoPiece(private val entity: AnimalGymEntity, left: Int, top: Int)
    : BasicRenderedGuiPiece(left, top, 54, 54, ThingiesTexture.FARM_TEXTURES.resource, 1, 1) {

    override fun drawForegroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        val font = FontRendererUtil.fontRenderer

        GlStateManager.pushMatrix()
        GlStateManager.translate((this.left + 2).toFloat(), (this.top + 2).toFloat(), 0f)
        GlStateManager.pushMatrix()
        GlStateManager.scale(0.65, 0.65, 1.0)
        font.drawString("" + TextFormatting.BOLD + TextFormatting.DARK_PURPLE + this.entity.currentAnimalType,
            4, 4, 0xFFFFFF)
        val energy = EnergyDisplayType.TESLA
        font.drawString(/*"" + TextFormatting.DARK_BLUE + String.format("%.2f", this.entity.powerPerTick) + " T / tick"*/
            localize(GUI_ANIMAL_GYM, "per_tick") {
                +TextFormatting.DARK_BLUE
                +energy.makeDarkTextComponent(this@AnimalGymInfoPiece.entity.powerPerTick.toLong())
            }
            ,0, 2 * (font.FONT_HEIGHT + 2), 0xFFFFFF)
        font.drawString(/*"" + TextFormatting.DARK_AQUA + this.entity.maxPowerForCurrent + " T" + TextFormatting.RESET*/
            energy.makeLightTextComponent(this@AnimalGymInfoPiece.entity.maxPowerForCurrent.toLong()).formattedText
            ,0, 3 * (font.FONT_HEIGHT + 2), 0xFFFFFF)
        GlStateManager.popMatrix()

        GlStateManager.scale(0.5, 0.5, 1.0)
        val a = this.entity.getCurrentAnimal()
        if (a != null) {
            container.mc.textureManager.bindTexture(Gui.ICONS)
            val f = a.maxHealth
            val i = MathHelper.ceil(a.health)
            val j1 = 80

            for (j5 in MathHelper.ceil(f / 2.0f) - 1 downTo 0) {
                val textureX = 16

                val x = j5 % 10 * 10
                val y = j1 - (j5 / 10 - 1) * 10

                container.drawTexturedModalRect(x, y, 16, 0, 9, 9)

                if (j5 * 2 + 1 < i) {
                    container.drawTexturedModalRect(x, y, textureX + 36, 0, 9, 9)
                }

                if (j5 * 2 + 1 == i) {
                    container.drawTexturedModalRect(x, y, textureX + 45, 0, 9, 9)
                }
            }
        }
        GlStateManager.popMatrix()
    }
}
