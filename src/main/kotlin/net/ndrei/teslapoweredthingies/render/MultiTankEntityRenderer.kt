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
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.machines.portablemultitank.MultiTankEntity
import org.lwjgl.opengl.GL11

/**
 * Created by CF on 2017-06-30.
 */
@SideOnly(Side.CLIENT)
object MultiTankEntityRenderer : TileEntitySpecialRenderer<TileEntity>() {
    override fun render(te: TileEntity?, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        val machine = (te as? MultiTankEntity)

        GlStateManager.pushMatrix()

        GlStateManager.translate(x.toFloat() + 0.5f, y.toFloat() + 1.0f, z.toFloat() + 0.5f)
        when (machine?.facing) {
            null -> {
                GlStateManager.rotate(120f, 0.0f, 1.0f, 0.0f)
                GlStateManager.rotate(30f, 0.0f, 0.0f, 1.0f)
            }
            EnumFacing.NORTH -> GlStateManager.rotate(180f, 0.0f, 1.0f, 0.0f)
            EnumFacing.WEST -> GlStateManager.rotate(-90f, 0.0f, 1.0f, 0.0f)
            EnumFacing.EAST -> GlStateManager.rotate(90f, 0.0f, 1.0f, 0.0f)
            else -> { }
        }
        GlStateManager.translate(-0.5, 0.0, -0.50)

        val magicNumber = 0.03125f
        GlStateManager.scale(magicNumber, -magicNumber, magicNumber)

        super.setLightmapDisabled(true)
        // GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.disableLighting()

        this.drawInnerTank(0.0, 0.0, 14.0, 14.0, false)
        this.drawInnerTank(18.0, 0.0, 0.0, 14.0, false)
        this.drawInnerTank(0.0, 18.0, 14.0, 0.0, false)
        this.drawInnerTank(18.0, 18.0, 0.0, 0.0, false)

        this.drawFluid(0.0, 0.0, 0.0, 0.0, FluidRegistry.WATER, .42f)
        this.drawFluid(18.0, 0.0, 14.0, 0.0, FluidRegistry.LAVA, .75f)
        this.drawFluid(0.0, 18.0, 0.0, 14.0, FluidRegistry.LAVA, 1.0f)
        this.drawFluid(18.0, 18.0, 14.0, 14.0, FluidRegistry.WATER, .0f)

        this.drawInnerTank(0.0, 0.0, 0.0, 0.0, true)
        this.drawInnerTank(18.0, 0.0, 14.0, 0.0, true)
        this.drawInnerTank(0.0, 18.0, 0.0, 14.0, true)
        this.drawInnerTank(18.0, 18.0, 14.0, 14.0, true)

//        this.drawTank(10.0, 5.0, null, 0.0f)

//        tanks.forEach {
//            val percent = if (it.fluid == null) 0.0f else Math.min(1f, Math.max(0f, it.fluid.amount.toFloat() / it.capacity.toFloat()))
//            this.drawTank(it.left, it.top, it.fluid?.fluid, percent)
//        }

        GlStateManager.enableLighting()
        GlStateManager.disableBlend()
//        GlStateManager.disableAlpha()
        super.setLightmapDisabled(false)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.popMatrix()
    }

    private fun drawInnerTank(tankX: Double, tankZ: Double, offX: Double, offZ : Double, front: Boolean) {
        super.bindTexture(Textures.MULTI_TANK_SIDE.resource)
        GlStateManager.pushMatrix()
        GlStateManager.translate(tankX, 2.0, tankZ)

        val buffer = Tessellator.getInstance().buffer
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        if (!front) {
            val flag = true
            this.drawTexture(buffer, 0.0, 0.0, 0.0, 14.0, 0.0, 14.0, 16.0, 0.0, 32.0, 16.0, !flag, flag)
            this.drawTexture(buffer, 0.0, 28.0, 0.0, 14.0, 0.0, 14.0, 16.0, 0.0, 32.0, 16.0, flag, !flag)
        }

        this.drawTexture(buffer, 0.0, 0.0, offZ, 14.0, 28.0, 0.0, if (front) 0.0 else 9.0, 1.0, if (front) 7.0 else 16.0, 15.0)
        this.drawTexture(buffer, offX, 0.0, 0.0, 0.0, 28.0, 14.0, if (front) 0.0 else 9.0, 1.0, if (front) 7.0 else 16.0, 15.0)

        Tessellator.getInstance().draw()

        GlStateManager.popMatrix()
    }

    private fun drawFluid(tankX: Double, tankZ: Double, offX: Double, offZ : Double, fluid: Fluid?, fluidPercent: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(tankX, 2.0, tankZ)

        if ((fluidPercent > 0.0f) && (fluid != null)) {
            if (fluidPercent > 0) {
                val still = fluid.still
                if (still != null) {
                    super.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                    val height = 28 * fluidPercent
                    val color = fluid.color
                    GlStateManager.color((color shr 16 and 0xFF) / 255.0f, (color shr 8 and 0xFF) / 255.0f, (color and 0xFF) / 255.0f)

                    val stillSprite = Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(still.toString())
                            ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    if (stillSprite != null) {
                        val xStage = (offX > 0)
                        val zStage = (offZ > 0)
                        this.drawSprite(
                                Vec3d(0.1, 28.0 - height, if (offZ > 0) offZ - 0.1 else 0.1),
                                Vec3d(13.99, 28.0, if (offZ > 0) offZ - 0.1 else 0.1),
                                stillSprite, zStage, !zStage)
                        this.drawSprite(
                                Vec3d(if (offX > 0) offX - 0.1 else 0.1, 28.0 - height, 0.1),
                                Vec3d(if (offX > 0) offX - 0.1 else 0.1, 28.0, 13.99),
                                stillSprite, !xStage, xStage)
                        this.drawSprite(
                                Vec3d(0.1, 28.0 - height, 13.99),
                                Vec3d(13.99, 28.0 - height, 0.1),
                                stillSprite)
                    }
                }
            }
        }

        GlStateManager.popMatrix()
    }

    private fun drawSprite(start: Vec3d, end: Vec3d, sprite: TextureAtlasSprite, draw1: Boolean = true, draw2: Boolean = true) {
        val buffer = Tessellator.getInstance().buffer
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        val width = Math.abs(if (end.x == start.x) end.z - start.z else end.x - start.x)
        val height = Math.abs(if (end.y == start.y) end.z - start.z else end.y - start.y)

        val texW = sprite.maxU - sprite.minU
        val texH = sprite.maxV - sprite.minV

        val finalW = texW * width / 32.0
        val finalH = texH * height / 32.0

        // this.drawTexture(buffer, start, end, sprite.minU.toDouble(), sprite.minV.toDouble(), sprite.maxU.toDouble(), sprite.maxV.toDouble())
        this.drawTexture(buffer, start, end, sprite.minU.toDouble(), sprite.minV.toDouble(), sprite.minU + finalW, sprite.minV + finalH, draw1, draw2)
        Tessellator.getInstance().draw()
    }

    private fun drawTexture(buffer: BufferBuilder, x: Double, y: Double, z: Double, sx: Double, sy: Double, sz: Double, minU: Double, minV: Double, maxU: Double, maxV: Double, draw1: Boolean = true, draw2: Boolean = true) {
        val of = 0.02
        val ofx = if (sx == 0.0) 0.0 else if (sx > 0) of else -of
        val ofy = if (sy == 0.0) 0.0 else if (sy > 0) of else -of
        val ofz = if (sz == 0.0) 0.0 else if (sz > 0) of else -of
        this.drawTexture(buffer,
                Vec3d(x - ofx, y - ofy, z - ofz),
                Vec3d(x + sx + ofx, y + sy + ofy, z + sz + ofz),
                minU / 32.0, minV / 32.0, maxU / 32.0, maxV / 32.0,
                draw1, draw2)
    }

    private fun drawTexture(buffer: BufferBuilder, start: Vec3d, end: Vec3d, minU: Double, minV: Double, maxU: Double, maxV: Double, draw1: Boolean = true, draw2: Boolean = true) {
        if (draw1) {
            buffer.pos(start.x, start.y, start.z).tex(minU, minV).endVertex()
            buffer.pos(start.x, end.y, if (start.x == end.x) start.z else end.z).tex(minU, maxV).endVertex()
            buffer.pos(end.x, end.y, end.z).tex(maxU, maxV).endVertex()
            buffer.pos(end.x, start.y, if (start.x == end.x) end.z else start.z).tex(maxU, minV).endVertex()
        }

        if (draw2) {
            buffer.pos(start.x, start.y, start.z).tex(minU, minV).endVertex()
            buffer.pos(end.x, start.y, if (start.x == end.x) end.z else start.z).tex(maxU, minV).endVertex()
            buffer.pos(end.x, end.y, end.z).tex(maxU, maxV).endVertex()
            buffer.pos(start.x, end.y, if (start.x == end.x) start.z else end.z).tex(minU, maxV).endVertex()
        }
    }
}
