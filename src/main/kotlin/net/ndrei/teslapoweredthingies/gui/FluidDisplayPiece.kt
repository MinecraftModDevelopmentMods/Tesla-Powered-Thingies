package net.ndrei.teslapoweredthingies.gui

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslacorelib.gui.BasicContainerGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import org.lwjgl.opengl.GL11

/**
 * Created by CF on 2017-06-30.
 */
class FluidDisplayPiece(left: Int, top: Int, width: Int, height: Int, private val fluidGetter: () -> FluidStack?)
    : BasicContainerGuiPiece(left, top, width, height) {

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        val fluid = this.fluidGetter() ?: return

        this.drawFluid(container, fluid.fluid, guiX + this.left + 1, guiY + this.top + 1 , this.width - 2, this.height - 2)
    }

    private fun drawFluid(container: BasicTeslaGuiContainer<*>, fluid: Fluid?, x: Int, y: Int, w: Int, h: Int) {
        if (fluid == null) {
            return
        }

        val color = fluid.color
        val still = fluid.flowing //.getStill(stack);
        if (still != null) {
            val sprite = container.mc.textureMapBlocks.getTextureExtry(still.toString()) ?: container.mc.textureMapBlocks.missingSprite
            container.mc.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            GL11.glColor3ub((color shr 16 and 0xFF).toByte(), (color shr 8 and 0xFF).toByte(), (color and 0xFF).toByte())
            GlStateManager.enableBlend()
            container.drawTexturedModalRect(
                    x, y, sprite!!, w, h)
            GlStateManager.disableBlend()
        }
    }
}
