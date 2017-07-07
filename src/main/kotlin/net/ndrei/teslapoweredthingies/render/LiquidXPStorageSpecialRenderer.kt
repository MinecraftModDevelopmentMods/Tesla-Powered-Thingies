package net.ndrei.teslapoweredthingies.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.ndrei.teslacorelib.render.HudInfoRenderer
import net.ndrei.teslapoweredthingies.fluids.LiquidXPFluid
import net.ndrei.teslapoweredthingies.machines.liquidxpstorage.LiquidXPStorageEntity
import org.lwjgl.opengl.GL11

/**
 * Created by CF on 2017-07-07.
 */
class LiquidXPStorageSpecialRenderer
    : HudInfoRenderer<LiquidXPStorageEntity>() {

    override fun render(entity: LiquidXPStorageEntity, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        val height = Math.max(0.0f, Math.min(1.0f, entity.fillPercent))
        if (height > 0.0f) {
            val top = 1.0 + 14 * height

            val color = LiquidXPFluid.getColor()
            val still = LiquidXPFluid.getStill()
            val flowing = LiquidXPFluid.getFlowing()
            var stillSprite: TextureAtlasSprite =
                    (if (still == null) null else Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(still.toString()))
                            ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
            val flowingSprite = /*(flowing == null) ? null : Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(flowing.toString());
            if (flowingSprite == null) {
                flowingSprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
            }
            flowingSprite =*/ stillSprite

            GlStateManager.pushAttrib()
            GlStateManager.pushMatrix()
            GlStateManager.translate(x.toFloat() + 0.0f, y.toFloat() + 0.0f, z.toFloat() + 0.0f)
            val magicNumber = 1.0f / 16.0f
            GlStateManager.scale(magicNumber, magicNumber, magicNumber)

            val vertexbuffer = Tessellator.getInstance().buffer
            vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

            vertexbuffer.pos(1.0, 1.0,1.0).tex(flowingSprite.maxU.toDouble(), flowingSprite.maxV.toDouble()).endVertex()
            vertexbuffer.pos(1.0, top, 1.0).tex(flowingSprite.maxU.toDouble(), flowingSprite.minV.toDouble()).endVertex()
            vertexbuffer.pos(15.0, top, 1.0).tex(flowingSprite.minU.toDouble(), flowingSprite.minV.toDouble()).endVertex()
            vertexbuffer.pos(15.0, 1.0, 1.0).tex(flowingSprite.minU.toDouble(), flowingSprite.maxV.toDouble()).endVertex()

            vertexbuffer.pos(1.0, 1.0, 15.0).tex(flowingSprite.maxU.toDouble(), flowingSprite.maxV.toDouble()).endVertex()
            vertexbuffer.pos(15.0, 1.0, 15.0).tex(flowingSprite.minU.toDouble(), flowingSprite.maxV.toDouble()).endVertex()
            vertexbuffer.pos(15.0, top, 15.0).tex(flowingSprite.minU.toDouble(), flowingSprite.minV.toDouble()).endVertex()
            vertexbuffer.pos(1.0, top, 15.0).tex(flowingSprite.maxU.toDouble(), flowingSprite.minV.toDouble()).endVertex()

            vertexbuffer.pos(1.0, 1.0, 1.0).tex(flowingSprite.minU.toDouble(), flowingSprite.maxV.toDouble()).endVertex()
            vertexbuffer.pos(1.0, 1.0, 15.0).tex(flowingSprite.maxU.toDouble(), flowingSprite.maxV.toDouble()).endVertex()
            vertexbuffer.pos(1.0, top, 15.0).tex(flowingSprite.maxU.toDouble(), flowingSprite.minV.toDouble()).endVertex()
            vertexbuffer.pos(1.0, top, 1.0).tex(flowingSprite.minU.toDouble(), flowingSprite.minV.toDouble()).endVertex()

            vertexbuffer.pos(15.0, 1.0, 1.0).tex(flowingSprite.minU.toDouble(), flowingSprite.maxV.toDouble()).endVertex()
            vertexbuffer.pos(15.0, top, 1.0).tex(flowingSprite.minU.toDouble(), flowingSprite.minV.toDouble()).endVertex()
            vertexbuffer.pos(15.0, top, 15.0).tex(flowingSprite.maxU.toDouble(), flowingSprite.minV.toDouble()).endVertex()
            vertexbuffer.pos(15.0, 1.0, 15.0).tex(flowingSprite.maxU.toDouble(), flowingSprite.maxV.toDouble()).endVertex()

            vertexbuffer.pos(1.0, top, 1.0).tex(stillSprite.minU.toDouble(), stillSprite.minV.toDouble()).endVertex()
            vertexbuffer.pos(1.0, top, 15.0).tex(stillSprite.minU.toDouble(), stillSprite.maxV.toDouble()).endVertex()
            vertexbuffer.pos(15.0, top, 15.0).tex(stillSprite.maxU.toDouble(), stillSprite.maxV.toDouble()).endVertex()
            vertexbuffer.pos(15.0, top, 1.0).tex(stillSprite.maxU.toDouble(), stillSprite.minV.toDouble()).endVertex()

            super.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            GlStateManager.color((color shr 16 and 0xFF) / 255.0f, (color shr 8 and 0xFF) / 255.0f, (color and 0xFF) / 255.0f)
            GlStateManager.enableBlend()
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
            GlStateManager.disableLighting()
            super.setLightmapDisabled(true)

            Tessellator.getInstance().draw()

            super.setLightmapDisabled(false)
            GlStateManager.enableLighting()
            GlStateManager.disableBlend()
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.popMatrix()
            GlStateManager.popAttrib()
        }

        super.render(entity, x, y, z, partialTicks, destroyStage, alpha)
    }
}
