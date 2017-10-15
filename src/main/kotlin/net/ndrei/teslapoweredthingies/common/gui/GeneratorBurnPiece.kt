package net.ndrei.teslapoweredthingies.common.gui

import net.minecraft.util.text.TextFormatting
import net.ndrei.teslacorelib.gui.BasicContainerGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.EnergyDisplayType
import net.ndrei.teslacorelib.localization.makeTextComponent
import net.ndrei.teslacorelib.tileentities.ElectricGenerator
import net.ndrei.teslapoweredthingies.client.ThingiesTexture
import net.ndrei.teslapoweredthingies.integrations.GUI_GENERATOR_BURN
import net.ndrei.teslapoweredthingies.integrations.localize

/**
 * Created by CF on 2017-06-30.
 */
class GeneratorBurnPiece(left: Int, top: Int, private val te: ElectricGenerator)
    : BasicContainerGuiPiece(left, top, 14, 14) {

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        // container.mc.textureManager.bindTexture(TeslaThingiesMod.MACHINES_TEXTURES)
        ThingiesTexture.MACHINES_TEXTURES.bind(container)

        container.drawTexturedRect(this.left, this.top, 44, 27, 14, 14)
        val generated = this.te.generatedPowerCapacity
        val stored = this.te.generatedPowerStored
        val percent = Math.round(14 * Math.min(Math.max((generated - stored).toFloat() / generated.toFloat(), 0f), 1f))
        if (percent > 0) {
            container.drawTexturedRect(this.left, this.top + percent, 8, 27 + percent, 14, 14 - percent)
        }
    }

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        if (!this.isInside(container, mouseX, mouseY)) {
            return
        }

        val lines = GeneratorBurnPiece.getTooltipLines(this.te)
        if (lines != null && lines.size > 0) {
            container.drawTooltip(lines, mouseX - guiX, mouseY - guiY)
        }
    }

    companion object {
        fun getTooltipLines(entity: ElectricGenerator): List<String>? {
            val lines = mutableListOf<String>()
            val generated = entity.generatedPowerCapacity
            if (generated > 0 && entity.generatedPowerStored > 0) {
                val energy = EnergyDisplayType.TESLA
                lines.add(localize(GUI_GENERATOR_BURN, "total_for_fuel") {
                    +TextFormatting.GRAY
                    +energy.makeLightTextComponent(generated)
                })
                lines.add(localize(GUI_GENERATOR_BURN, "generating_tick") {
                    +TextFormatting.GRAY
                    +energy.makeLightTextComponent(entity.generatedPowerReleaseRate)
                })

                val ticks = entity.generatedPowerStored.toDouble() / entity.generatedPowerReleaseRate.toDouble() / 20.0
                lines.add(localize(GUI_GENERATOR_BURN, "ticks") {
                    +TextFormatting.GRAY
                    +String.format("%.2f", ticks).makeTextComponent()
                })
            }
            return lines
        }
    }
}
