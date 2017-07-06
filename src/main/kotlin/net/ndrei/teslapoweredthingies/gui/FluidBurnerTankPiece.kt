package net.ndrei.teslapoweredthingies.gui

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraftforge.fluids.Fluid
import net.ndrei.teslacorelib.gui.BasicContainerGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerEntity
import org.lwjgl.opengl.GL11

/**
 * Created by CF on 2017-06-30.
 */
class FluidBurnerTankPiece(left: Int, top: Int, private val te: FluidBurnerEntity)
    : BasicContainerGuiPiece(left, top, 18, 34) {

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        val generated = this.te.generatedPowerCapacity
        val stored = this.te.generatedPowerStored
        val percent = Math.round(Math.min(Math.max(stored.toFloat() / generated.toFloat(), 0f), 1f) * (this.height - if (null == this.te.coolantInUse) 5 else 2))
        container.mc.textureManager.bindTexture(TeslaThingiesMod.MACHINES_TEXTURES)
        container.drawTexturedRect(this.left, this.top,
                27, 44, this.width, this.height)
        if (percent > 0) {
            val h = percent
            this.drawFluid(container, this.te.coolantInUse, guiX + this.left + 1, guiY + this.top + 1 + (this.height - 2 - h), this.width - 2, h)
        }

        container.mc.textureManager.bindTexture(TeslaThingiesMod.MACHINES_TEXTURES)
        container.drawTexturedRect(this.left + 4, this.top + 5,
                31, 49, this.width - 8, this.height - 5)

        if (percent > 0) {
            val h = Math.min(percent, this.height - 5)
            this.drawFluid(container, this.te.fuelInUse, guiX + this.left + 5, guiY + this.top + 4 + (this.height - 5 - h), this.width - 10, h)
        }
        container.mc.textureManager.bindTexture(TeslaThingiesMod.MACHINES_TEXTURES)
        container.drawTexturedRect(this.left + 4, this.top + 5,
                47, 49, this.width - 8, this.height - 5)
    }

    private fun drawFluid(container: BasicTeslaGuiContainer<*>, fluid: Fluid?, x: Int, y: Int, w: Int, h: Int) {
        if (fluid == null) {
            return
        }

        val color = fluid.color
        val still = fluid.flowing //.getStill(stack);
        if (still != null) {
            val sprite = container.mc.textureMapBlocks.getTextureExtry(still.toString())
            container.mc.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            GL11.glColor3ub((color shr 16 and 0xFF).toByte(), (color shr 8 and 0xFF).toByte(), (color and 0xFF).toByte())
            GlStateManager.enableBlend()
            container.drawTexturedModalRect(
                    x, y, sprite!!, w, h)
            GlStateManager.disableBlend()
        }
    }
}
