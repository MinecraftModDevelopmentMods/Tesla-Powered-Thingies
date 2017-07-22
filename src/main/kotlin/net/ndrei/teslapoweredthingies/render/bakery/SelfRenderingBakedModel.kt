package net.ndrei.teslapoweredthingies.render.bakery

import com.google.common.cache.CacheBuilder
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.MinecraftForgeClient
import java.util.concurrent.TimeUnit

open class SelfRenderingBakedModel(val renderer: ISelfRenderingBlock, val format: VertexFormat) : IBakedModel {
    override fun getParticleTexture(): TextureAtlasSprite {
        val rl = this.renderer.getParticleTexture()
        return (if (rl == null) null else Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(rl.toString()))
                ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
    }

    private val cache = CacheBuilder.newBuilder().expireAfterAccess(42, TimeUnit.SECONDS).build<String, List<IBakery>>()

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        Minecraft.getMinecraft().mcProfiler.startSection("SelfRenderingBakedModel")
        try {
            val layer = MinecraftForgeClient.getRenderLayer()
            val stack = this.itemStack
            val key = layer?.toString() ?: "NO LAYER"

            if (layer == BlockRenderLayer.TRANSLUCENT) {
                GlStateManager.enableAlpha()
                GlStateManager.enableBlend()
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
            }

            return this.cache.get(key, {
                // TeslaThingiesMod.logger.info("Getting bakeries for '$key'.")
                this.renderer.getBakeries(layer, state, stack, side, rand)
            })
                    .fold(mutableListOf<BakedQuad>()) { list, bakery -> list.also { it.addAll(bakery.getQuads(state, stack, side, this.format)) } }
        } finally {
            Minecraft.getMinecraft().mcProfiler.endSection()
        }
    }

    protected open val itemStack: ItemStack?
        get() = null

    override fun isBuiltInRenderer() = false
    override fun isAmbientOcclusion() = false
    override fun isGui3d() = false
    override fun getOverrides() = ItemOverrideList.NONE!!
}