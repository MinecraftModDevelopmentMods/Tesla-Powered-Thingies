package net.ndrei.teslapoweredthingies.render.bakery

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.BaseAnnotationHandler
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import java.util.function.Function

@Suppress("unused")
@RegistryHandler
object SelfRenderingBlocksRegistry : IRegistryHandler {
    override fun preInit(asm: ASMDataTable) {
        if (TeslaCoreLib.isClientSide) {
            SelfRenderingModelLoader.preInit(asm)
        }
    }

    @SideOnly(Side.CLIENT)
    object SelfRenderingModelLoader: ICustomModelLoader {
        private lateinit var blocks: List<ISelfRenderingBlock>
        private val models = mutableMapOf<String, IModel>()

        fun preInit(asm: ASMDataTable) {
            MinecraftForge.EVENT_BUS.register(this)
            ModelLoaderRegistry.registerLoader(this)

            val blocks = mutableListOf<ISelfRenderingBlock>()
            object: BaseAnnotationHandler<ISelfRenderingBlock>({ it, _, _ ->
                blocks.add(it)
            }, SelfRenderingBlock::class) {}.process(asm, Loader.instance().activeModContainer())
            this.blocks = blocks.toList()
        }

        @SubscribeEvent
        fun stitchEvent(ev: TextureStitchEvent) {
            val stuff = mutableListOf<String>()
            if (ev.map == Minecraft.getMinecraft().textureMapBlocks) {
                // ev.map.registerSprite(Textures.MULTI_TANK_SIDE.resource)
                this.blocks.forEach {
                    it.getTextures().forEach {
                        if (!stuff.contains(it.toString())) {
                            // try to avoid double registering same resource
                            ev.map.registerSprite(it)
                            stuff.add(it.toString())
                        }
                    }
                }
            }
            // TODO: rebake all the thing!... maybe?... I don't know...
        }

        override fun loadModel(modelLocation: ResourceLocation?): IModel {
            // TODO: maybe throw an error if the location is not accepted?
            val block = this.blocks.first { it.getRegistryName() == modelLocation }
            return this.models.getOrPut(modelLocation.toString()) {
                if (modelLocation is ModelResourceLocation) {
                    if (modelLocation.variant == "inventory") {
                        return SelfRenderingInventoryModel(block)
                    }
                    return SelfRenderingModel(block)
                }
                else return SelfRenderingInventoryModel(block)
            }
        }

        override fun accepts(modelLocation: ResourceLocation?): Boolean {
            val rl = modelLocation ?: return false
            return this.blocks.any { it.getRegistryName() == rl }
        }

        override fun onResourceManagerReload(resourceManager: IResourceManager?) {
            // TODO: rebake all the thing!
        }
    }

    @SideOnly(Side.CLIENT)
    class SelfRenderingModel(val block: ISelfRenderingBlock): IModel {
        override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
            return SelfRenderingBakedModel(this.block, format)
        }
    }

    @SideOnly(Side.CLIENT)
    class SelfRenderingInventoryModel(val block: ISelfRenderingBlock): IModel {
        override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
            return SelfRenderingInventoryBakedModel(this.block, format)
        }
    }
}
