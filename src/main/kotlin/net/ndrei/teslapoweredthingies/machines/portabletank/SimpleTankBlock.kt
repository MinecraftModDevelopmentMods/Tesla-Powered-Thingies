package net.ndrei.teslapoweredthingies.machines.portabletank

import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.render.selfrendering.*
import net.ndrei.teslapoweredthingies.client.ThingiesTexture
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock
import net.ndrei.teslapoweredthingies.machines.portablemultitank.UnlistedFluidProperty
import org.lwjgl.opengl.GL11

/**
 * Created by CF on 2017-07-16.
 */
@AutoRegisterBlock
@SelfRenderingBlock
object SimpleTankBlock
    : BaseThingyBlock<SimpleTankEntity>("simple_tank", SimpleTankEntity::class.java), ISelfRenderingBlock {

    lateinit var FLUID_PROP: UnlistedFluidProperty

    override fun registerItem(registry: IForgeRegistry<Item>) {
        registry.register(SimpleTankItem)
    }

    //#region Block Overrides

    override fun createBlockState(): BlockStateContainer {
        this.FLUID_PROP = UnlistedFluidProperty("fluid")
        return ExtendedBlockState(this, arrayOf(FACING), arrayOf(this.FLUID_PROP))
    }

    override fun canRenderInLayer(state: IBlockState?, layer: BlockRenderLayer?): Boolean {
        return /*layer == BlockRenderLayer.TRANSLUCENT ||*/ layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT
    }

    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isTranslucent(state: IBlockState?) = true
    override fun doesSideBlockRendering(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = false

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        if (state is IExtendedBlockState) {
            val te = world.getTileEntity(pos)
            if (te is SimpleTankEntity) {
                val f = te.getFluid()
                return if (f != null) state.withProperty(FLUID_PROP, f) else state
            }
        }
        return super.getExtendedState(state, world, pos)
    }

    //#endregion

    @SideOnly(Side.CLIENT)
    override fun getTextures(): List<ResourceLocation> {
        return listOf(ThingiesTexture.SIMPLE_TANK_SIDE.resource)
    }

    @SideOnly(Side.CLIENT)
    override fun getBakeries(layer: BlockRenderLayer?, state: IBlockState?, stack: ItemStack?, side: EnumFacing?, rand: Long, transform: TRSRTransformation): List<IBakery> {
        val bakeries = mutableListOf<IBakery>()
        // TeslaThingiesMod.logger.info("Building bakeries for '${layer?.toString() ?: "NO LAYER"}'.")

        if ((layer == BlockRenderLayer.SOLID) || (layer == null)) {
            bakeries.add(listOf(
                RawCube(Vec3d(0.0, 0.0, 0.0), Vec3d(32.0, 2.0, 32.0), ThingiesTexture.SIMPLE_TANK_SIDE.sprite)
                    .addFace(EnumFacing.UP).uv(8.0f, 8.0f, 16.0f, 16.0f)
                    .addFace(EnumFacing.DOWN).uv(8.0f, 8.0f, 16.0f, 16.0f)
                    .addFace(EnumFacing.WEST).uv(0.0f, 0.0f, 8.0f, 0.5f)
                    .addFace(EnumFacing.NORTH).uv(0.0f, 0.0f, 8.0f, 0.5f)
                    .addFace(EnumFacing.EAST).uv(0.0f, 0.0f, 8.0f, 0.5f)
                    .addFace(EnumFacing.SOUTH).uv(0.0f, 0.0f, 8.0f, 0.5f),
                RawCube(Vec3d(0.0, 30.0, 0.0), Vec3d(32.0, 32.0, 32.0), ThingiesTexture.SIMPLE_TANK_SIDE.sprite)
                    .addFace(EnumFacing.WEST).uv(0.0f, 0.0f, 8.0f, 0.5f)
                    .addFace(EnumFacing.NORTH).uv(0.0f, 0.0f, 8.0f, 0.5f)
                    .addFace(EnumFacing.EAST).uv(0.0f, 0.0f, 8.0f, 0.5f)
                    .addFace(EnumFacing.SOUTH).uv(0.0f, 0.0f, 8.0f, 0.5f)
                    .addFace(EnumFacing.UP).uv(8.0f, 0.0f, 16.0f, 8.0f)
                    .addFace(EnumFacing.DOWN).uv(8.0f, 8.0f, 16.0f, 16.0f)
            ).combine().static())
        }
        if ((layer == BlockRenderLayer.CUTOUT) || (layer == null)) {
            bakeries.add(listOf(
                RawCube(Vec3d(1.0, 2.0, 1.0), Vec3d(31.0, 30.0, 31.0), ThingiesTexture.SIMPLE_TANK_SIDE.sprite)
                    .dualSide()
                    .addFace(EnumFacing.SOUTH).uv(0.5f, 8.5f, 7.5f, 15f)
                    .addFace(EnumFacing.EAST).uv(0.5f, 8.5f, 7.5f, 15f)
                    .addFace(EnumFacing.NORTH).uv(0.5f, 8.5f, 7.5f, 15f)
                    .addFace(EnumFacing.WEST).uv(0.5f, 8.5f, 7.5f, 15f)
            ).combine().static())
        }
        if (/*(layer == BlockRenderLayer.TRANSLUCENT) || */(layer == null)) {
            bakeries.add(
                CachedBakery({ _state, _stack, _side, _vertexFormat, _transform ->
                    val info = this@SimpleTankBlock.getFluidStackInfo(_state, _stack)
                    if (info != null) {
                        val resource = info.fluid.still
                        val sprite = (if (resource != null) Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(resource.toString()) else null)
                            ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                        mutableListOf<BakedQuad>().also { list ->
                            RawCube(Vec3d(1.1, 2.1, 1.1), Vec3d(30.9, 2.0 + 28.0 * (info.amount.toDouble() / 64.0) - 0.1, 30.9), sprite)
                                .autoUV()
                                .addFace(EnumFacing.EAST).color(info.fluid.color)
                                .addFace(EnumFacing.SOUTH).color(info.fluid.color)
                                .addFace(EnumFacing.WEST).color(info.fluid.color)
                                .addFace(EnumFacing.NORTH).color(info.fluid.color)
                                .addFace(EnumFacing.UP).color(info.fluid.color)
                                .bake(list, _vertexFormat, _transform)
                        }
                    } else {
                        mutableListOf()
                    }
                }).also { cache ->
                    cache.keyGetter = { _state, _stack, _ ->
                        val info = this.getFluidStackInfo(_state, _stack)
                        if (info == null) {
                            "no info"
                        }
                        else {
                            "${info.fluid.name}::${info.amount}"
                        }
                    }
                }
            )
        }
        return bakeries.toList()
    }

    private fun getFluidStackInfo(state: IBlockState?, stack: ItemStack?): FluidStackInfo? {
        val extended = (state as? IExtendedBlockState)
        if (extended != null) {
            val fluid = extended.getValue(FLUID_PROP)
            if ((fluid != null) && (fluid.fluid != null) && (fluid.amount > 0)) {
                return FluidStackInfo(fluid.fluid, Math.round(fluid.amount.toFloat() / 93.75f))
            }
        }

        if ((stack != null) && !stack.isEmpty && stack.hasTagCompound()) {
            val nbt = stack.tagCompound
            if ((nbt != null) && nbt.hasKey("tileentity", Constants.NBT.TAG_COMPOUND)) {
                val teNBT = nbt.getCompoundTag("tileentity")
                if (teNBT.hasKey("fluids", Constants.NBT.TAG_COMPOUND)) {
                    val flNBT = teNBT.getCompoundTag("fluids")
                    // fluids -> tanks -> (FluidName, Amount)
                    if (flNBT.hasKey("tanks", Constants.NBT.TAG_LIST)) {
                        val tNBT = flNBT.getTagList("tanks", Constants.NBT.TAG_COMPOUND)
                        if (tNBT.tagCount() > 0) {
                            val tank = tNBT.getCompoundTagAt(0)
                            val fluid = FluidStack.loadFluidStackFromNBT(tank)
                            if (fluid != null) {
                                return FluidStackInfo(fluid.fluid, Math.round(fluid.amount.toFloat() / 375f))
                            }
                        }
                    }
                }
            }
        }

        return null
    }

    class FluidStackInfo(val fluid: Fluid, val amount: Int)

    @SideOnly(Side.CLIENT)
    override fun renderTESR(proxy: TESRProxy, te: TileEntity, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)

        proxy.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        val fluid = (te as? SimpleTankEntity)?.getFluid()
        if ((fluid != null) && (fluid.fluid != null) && (fluid.amount > 0)) {
            val amount = Math.round(fluid.amount.toFloat() / 375f) / 64.0f
            this.drawFluid(fluid.fluid, amount)
        }

        GlStateManager.disableBlend()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    }

    @SideOnly(Side.CLIENT)
    private fun drawFluid(fluid: Fluid?, fluidPercent: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(1.1, 2.0, 1.1)

        if ((fluidPercent > 0.0f) && (fluid != null)) {
            if (fluidPercent > 0) {
                val still = fluid.still
                if (still != null) {
                    val height = 28 * fluidPercent
                    val color = fluid.color
                    GlStateManager.color((color shr 16 and 0xFF) / 255.0f, (color shr 8 and 0xFF) / 255.0f, (color and 0xFF) / 255.0f, (color ushr 24 and 0xFF) / 255.0f)

                    val stillSprite = Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(still.toString())
                            ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    if (stillSprite != null) {
                        this.drawSprite(
                            Vec3d(0.0, 28.0 - height, 0.0),
                            Vec3d(29.8, 28.0, 0.0),
                            stillSprite, false, true)
                        this.drawSprite(
                            Vec3d(0.0, 28.0 - height, 0.0),
                            Vec3d(0.0, 28.0, 29.8),
                            stillSprite, true, false)
                        this.drawSprite(
                            Vec3d(0.0, 28.0 - height, 29.8),
                            Vec3d(29.8, 28.0, 29.8),
                            stillSprite, true, false)
                        this.drawSprite(
                            Vec3d(29.8, 28.0 - height, 0.0),
                            Vec3d(29.8, 28.0, 29.8),
                            stillSprite, false, true)
                        this.drawSprite(
                            Vec3d(0.0, 28.0 - height, 29.8),
                            Vec3d(29.8, 28.0 - height, 0.0),
                            stillSprite)
                    }
                }
            }
        }

        GlStateManager.popMatrix()
    }

    @SideOnly(Side.CLIENT)
    private fun drawSprite(start: Vec3d, end: Vec3d, sprite: TextureAtlasSprite, draw1: Boolean = true, draw2: Boolean = true) {
        val buffer = Tessellator.getInstance().buffer
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        val width = Math.abs(if (end.x == start.x) end.z - start.z else end.x - start.x)
        val height = Math.abs(if (end.y == start.y) end.z - start.z else end.y - start.y)

        val texW = sprite.maxU - sprite.minU
        val texH = sprite.maxV - sprite.minV

        val finalW = texW * width / 32.0
        val finalH = texH * height / 32.0

        this.drawTexture(buffer, start, end, sprite.minU.toDouble(), sprite.minV.toDouble(), sprite.minU + finalW, sprite.minV + finalH, draw1, draw2)
        Tessellator.getInstance().draw()
    }

    @SideOnly(Side.CLIENT)
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