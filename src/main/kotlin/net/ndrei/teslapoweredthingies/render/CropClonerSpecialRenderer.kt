package net.ndrei.teslapoweredthingies.render

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslapoweredthingies.machines.cropcloner.CropClonerEntity

/**
 * Created by CF on 2017-07-07.
 */
@SideOnly(Side.CLIENT)
object CropClonerSpecialRenderer
    : TileEntitySpecialRenderer<TileEntity>() {

    override fun render(machine: TileEntity, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        val state = (machine as? CropClonerEntity)?.plantedThing
        if (state != null) {
            GlStateManager.pushMatrix()
            val vertexbuffer = Tessellator.getInstance().buffer
            vertexbuffer.begin(7, DefaultVertexFormats.ITEM)

            GlStateManager.translate(x + .25, y + 8.0 / 16.0, z + .25)
            GlStateManager.scale(0.5, 0.5, 0.5)

            this.renderState(vertexbuffer, state, null)

            Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            Tessellator.getInstance().draw()
            GlStateManager.popMatrix()
        }
    }

    private fun renderState(buffer: BufferBuilder, blockState: IBlockState, side: EnumFacing?) {
        val model = Minecraft.getMinecraft().blockRendererDispatcher.getModelForState(blockState)
        if (model != null) {
            val quads = model.getQuads(blockState, side, 0)

            for (quad in quads) {
                val color = Minecraft.getMinecraft().blockColors.colorMultiplier(blockState, super.getWorld(), null, quad.tintIndex)
                buffer.addVertexData(quad.vertexData)
                buffer.putColorRGB_F4((color shr 16 and 0xFF) / 255.0f, (color shr 8 and 0xFF) / 255.0f, (color and 0xFF) / 255.0f)
            }
        }
    }
}
