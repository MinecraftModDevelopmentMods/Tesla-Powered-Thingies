package net.ndrei.teslapoweredthingies.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.Fluid
import net.ndrei.teslacorelib.tileentities.ElectricTileEntity
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.gui.IDualTankMachine
import org.lwjgl.opengl.GL11

/**
 * Created by CF on 2017-06-30.
 */
class DualTankEntityRenderer<T : ElectricTileEntity> : TileEntitySpecialRenderer<T>() {
    fun renderTileEntityAt(te: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        val machine = te as IDualTankMachine

        GlStateManager.pushMatrix()

        GlStateManager.translate(x.toFloat() + 0.5f, y.toFloat() + 1.0f, z.toFloat() + 0.5f)
        when (te.facing) {
            EnumFacing.NORTH -> GlStateManager.rotate(180f, 0.0f, 1.0f, 0.0f)
            EnumFacing.WEST -> GlStateManager.rotate(-90f, 0.0f, 1.0f, 0.0f)
            EnumFacing.EAST -> GlStateManager.rotate(90f, 0.0f, 1.0f, 0.0f)
            else -> { }
        }
        GlStateManager.translate(-0.5, 0.0, 0.501)

        super.setLightmapDisabled(true)

        val magicNumber = 0.03125f
        GlStateManager.scale(magicNumber, -magicNumber, magicNumber)
        // GlStateManager.glNormal3f(0.0F, 0.0F, 1.0F);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

        this.drawTank(5.0f, machine.leftTankFluid, machine.leftTankPercent)
        this.drawTank(19.0f, machine.rightTankFluid, machine.rightTankPercent)

        super.setLightmapDisabled(false)
        GlStateManager.popMatrix()
    }

    private fun drawTank(tankX: Float, fluid: Fluid?, fluidPercent: Float) {
        if (fluidPercent == 0.0f) {
            return
        }

        GlStateManager.pushAttrib()
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.disableLighting()

        if (fluid != null) {
            if (fluidPercent > 0) {
                val fluidTexture = fluid.flowing
                if (fluidTexture != null) {
                    val fluidSprite = Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(fluidTexture.toString())

                    val color = fluid.color
                    GlStateManager.color((color shr 16 and 0xFF) / 255.0f, (color shr 8 and 0xFF) / 255.0f, (color and 0xFF) / 255.0f)
                    super.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                    val height = 14.02f * fluidPercent
                    this.drawRectangle(
                            tankX + 0.99f, 5.99f + 14.02f - height, 6.02f, height,
                            fluidSprite!!.minU, fluidSprite.minV, fluidSprite.maxU, fluidSprite.maxV)
                }
            }
        }

        GlStateManager.translate(0.0f, 0f, 0.001f)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        super.bindTexture(Textures.MACHINES_TEXTURES.resource)

        GlStateManager.enableLighting()
        GlStateManager.disableBlend()
        drawRectangle(
                tankX, 5f, 8f, 16f,
                (23.0f + tankX) / 256.0f, 81.0f / 256.0f, (31.0f + tankX) / 256.0f, 97.0f / 256.0f)

        GlStateManager.translate(0.0f, 0f, -0.001f)

        GlStateManager.popMatrix()
        GlStateManager.popAttrib()
    }

    private fun drawRectangle(x: Float, y: Float, width: Float, height: Float, minU: Float, minV: Float, maxU: Float, maxV: Float) {
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        buffer.pos(x.toDouble(), y.toDouble(), 0.0).tex(minU.toDouble(), minV.toDouble()).endVertex()
        buffer.pos(x.toDouble(), (y + height).toDouble(), 0.0).tex(minU.toDouble(), maxV.toDouble()).endVertex()
        buffer.pos((x + width).toDouble(), (y + height).toDouble(), 0.0).tex(maxU.toDouble(), maxV.toDouble()).endVertex()
        buffer.pos((x + width).toDouble(), y.toDouble(), 0.0).tex(maxU.toDouble(), minV.toDouble()).endVertex()

        tessellator.draw()
    }
}
