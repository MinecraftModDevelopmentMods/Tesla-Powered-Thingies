package net.ndrei.teslapoweredthingies.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.tileentities.ElectricTileEntity
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.gui.IMultiTankMachine
import org.lwjgl.opengl.GL11

/**
 * Created by CF on 2017-06-30.
 */
@SideOnly(Side.CLIENT)
object DualTankEntityRenderer : TileEntitySpecialRenderer<TileEntity>() {
    override fun render(te: TileEntity, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        val machine = (te as? ElectricTileEntity) ?: return
        val tanks = (te as? IMultiTankMachine)?.getTanks() ?: return

        GlStateManager.pushMatrix()

        GlStateManager.translate(x.toFloat() + 0.5f, y.toFloat() + 1.0f, z.toFloat() + 0.5f)
        when (machine.facing) {
            EnumFacing.NORTH -> GlStateManager.rotate(180f, 0.0f, 1.0f, 0.0f)
            EnumFacing.WEST -> GlStateManager.rotate(-90f, 0.0f, 1.0f, 0.0f)
            EnumFacing.EAST -> GlStateManager.rotate(90f, 0.0f, 1.0f, 0.0f)
            else -> { }
        }
        GlStateManager.translate(-0.5, 0.0, 0.501)

        val magicNumber = 0.03125f
        GlStateManager.scale(magicNumber, -magicNumber, magicNumber)

        super.setLightmapDisabled(true)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.disableLighting()

        tanks.forEach {
            val percent = if (it.fluid == null) 0.0f else Math.min(1f, Math.max(0f, it.fluid.amount.toFloat() / it.capacity.toFloat()))
            this.drawTank(it.left, it.top, it.fluid?.fluid, percent)
        }

        GlStateManager.enableLighting()
        GlStateManager.disableBlend()
        super.setLightmapDisabled(false)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.popMatrix()
    }

    private fun drawTank(tankX: Double, tankY: Double, fluid: Fluid?, fluidPercent: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(tankX, tankY, 0.0)

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

        val tankHeight = 14.0

        super.bindTexture(Textures.INSIDE_TANK.resource)
        val buffer = Tessellator.getInstance().buffer
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        this.drawTexture(buffer, 0.0, 0.0, -6.0, 6.0, tankHeight, 0.0, 1.0, 1.0, 7.0, 15.0) // back
        this.drawTexture(buffer, 0.0, 0.0, 0.0, 6.0, 0.0, -6.0, 9.0, 1.0, 15.0, 7.0) // top
        this.drawTexture(buffer, 0.0, tankHeight, -6.0, 6.0, 0.0, 6.0, 9.0, 1.0, 15.0, 7.0) // bottom
        this.drawTexture(buffer, 0.0, 0.0, 0.0, 0.0, tankHeight, -6.0, 1.0, 1.0, 6.0, 15.0) // left
        this.drawTexture(buffer, 6.0, 0.0, -6.0, 0.0, tankHeight, 6.0, 1.0, 1.0, 7.0, 15.0) // right

        Tessellator.getInstance().draw()

        if ((fluidPercent > 0.0f) && (fluid != null)) {
            if (fluidPercent > 0) {
                val flowing = fluid.flowing
                val still = fluid.still
                if ((flowing != null) || (still != null)) {
                    super.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                    val height = tankHeight * fluidPercent
                    val color = fluid.color
                    GlStateManager.color((color shr 16 and 0xFF) / 255.0f, (color shr 8 and 0xFF) / 255.0f, (color and 0xFF) / 255.0f)

                    val flowingSprite = if (flowing != null) Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(flowing.toString()) else null
                        ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    if (flowingSprite != null) {
                        this.drawSprite(
                                Vec3d(0.0, tankHeight - height, -0.1),
                                Vec3d(6.0, tankHeight, -0.1),
                                flowingSprite)
                    }

                    val stillSprite = if (still != null) Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(still.toString()) else null
                            ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    if (stillSprite != null) {
                        this.drawSprite(
                                Vec3d(0.0, tankHeight - height, -6.0),
                                Vec3d(6.0, tankHeight - height, -0.1),
                                stillSprite)
                    }
                }
            }
        }

        GlStateManager.popMatrix()
    }

    private fun drawSprite(start: Vec3d, end: Vec3d, sprite: TextureAtlasSprite) {
        val buffer = Tessellator.getInstance().buffer
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        this.drawTexture(buffer, start, end, sprite.minU.toDouble(), sprite.minV.toDouble(), sprite.maxU.toDouble(), sprite.maxV.toDouble())

        Tessellator.getInstance().draw()
    }

    private fun drawTexture(buffer: BufferBuilder, x: Double, y: Double, z: Double, sx: Double, sy: Double, sz: Double, minU: Double, minV: Double, maxU: Double, maxV: Double) {
        val of = 0.02
        val ofx = if (sx == 0.0) 0.0 else if (sx > 0) of else -of
        val ofy = if (sy == 0.0) 0.0 else if (sy > 0) of else -of
        val ofz = if (sz == 0.0) 0.0 else if (sz > 0) of else -of
        this.drawTexture(buffer,
                Vec3d(x - ofx, y - ofy, z - ofz),
                Vec3d(x + sx + ofx, y + sy + ofy, z + sz + ofz),
                minU / 16.0, minV / 16.0, maxU / 16.0, maxV / 16.0)
    }

    private fun drawTexture(buffer: BufferBuilder, start: Vec3d, end: Vec3d, minU: Double, minV: Double, maxU: Double, maxV: Double) {
        buffer.pos(start.x, start.y, start.z).tex(minU, minV).endVertex()
        buffer.pos(start.x, end.y, if (start.x == end.x) start.z else end.z).tex(minU, maxV).endVertex()
        buffer.pos(end.x, end.y, end.z).tex(maxU, maxV).endVertex()
        buffer.pos(end.x, start.y, if (start.x == end.x) end.z else start.z).tex(maxU, minV).endVertex()
    }
}
