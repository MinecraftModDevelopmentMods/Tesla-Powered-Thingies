package net.ndrei.teslapoweredthingies.machines.pump

import net.minecraft.block.Block
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.render.selfrendering.*
import net.ndrei.teslapoweredthingies.client.ThingiesTexture
import net.ndrei.teslapoweredthingies.machines.BaseRunningBlock
import net.ndrei.teslapoweredthingies.machines.portablemultitank.UnlistedFluidProperty
import org.lwjgl.opengl.GL11

@AutoRegisterBlock
@SelfRenderingBlock
object PumpBlock: BaseRunningBlock<PumpEntity>("pump", PumpEntity::class.java), ISelfRenderingBlock {
    lateinit var FLUID_PROP: UnlistedFluidProperty

    //#region Block Overrides

    override fun createBlockState(): BlockStateContainer {
        this.FLUID_PROP = UnlistedFluidProperty("fluid")
        return super.createBlockState()
    }

    override fun getUnlistedProperties() = super.getUnlistedProperties().also {
        it.add(FLUID_PROP)
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
            if (te is PumpEntity) {
                val f = te.getFluid()
                return if (f != null) state.withProperty(PumpBlock.FLUID_PROP, f) else state
            }
        }
        return super.getExtendedState(state, world, pos)
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos)
        if (!worldIn.isRemote) {
            val te = worldIn.getTileEntity(pos) as? PumpEntity
            if (te != null)
                te.neighborChanged(fromPos)
        }
    }

//#endregion

    //#region Rendering

    @SideOnly(Side.CLIENT)
    override fun getTextures(): List<ResourceLocation> {
        return listOf(ThingiesTexture.PUMP_SIDE.resource)
    }

    @SideOnly(Side.CLIENT)
    override fun getBakeries(layer: BlockRenderLayer?, state: IBlockState?, stack: ItemStack?, side: EnumFacing?, rand: Long, transform: TRSRTransformation): List<IBakery> {
        val bakeries = mutableListOf<IBakery>()

        if ((layer == BlockRenderLayer.SOLID) || (layer == null)) {
            bakeries.add(listOf(
                RawCube(Vec3d(0.0, 0.0, 0.0), Vec3d(32.0, 8.0, 32.0), ThingiesTexture.PUMP_SIDE.sprite)
                    .addFace(EnumFacing.UP).uv(8.0f, 8.0f, 16.0f, 16.0f)
                    .addFace(EnumFacing.DOWN).uv(8.0f, 8.0f, 16.0f, 16.0f)
                    .addFace(EnumFacing.WEST).uv(0.0f, 6.0f, 8.0f, 8.0f)
                    .addFace(EnumFacing.NORTH).uv(0.0f, 6.0f, 8.0f, 8.0f)
                    .addFace(EnumFacing.EAST).uv(0.0f, 6.0f, 8.0f, 8.0f)
                    .addFace(EnumFacing.SOUTH).uv(0.0f, 6.0f, 8.0f, 8.0f),
                RawCube(Vec3d(0.0, 30.0, 0.0), Vec3d(32.0, 32.0, 32.0), ThingiesTexture.PUMP_SIDE.sprite)
                    .addFace(EnumFacing.WEST).uv(0.0f, 0.0f, 8.0f, 0.5f)
                    .addFace(EnumFacing.NORTH).uv(0.0f, 0.0f, 8.0f, 0.5f)
                    .addFace(EnumFacing.EAST).uv(0.0f, 0.0f, 8.0f, 0.5f)
                    .addFace(EnumFacing.SOUTH).uv(0.0f, 0.0f, 8.0f, 0.5f)
                    .addFace(EnumFacing.UP).uv(8.0f, 0.0f, 16.0f, 8.0f)
                    .addFace(EnumFacing.DOWN).uv(8.0f, 8.0f, 16.0f, 16.0f),
                RawCube(Vec3d(10.0, 8.0, 10.0), Vec3d(22.0, 30.0, 22.0), ThingiesTexture.PUMP_SIDE.sprite)
                    .addFace(EnumFacing.WEST).uv(2.5f, 0.5f, 5.5f, 6.0f)
                    .addFace(EnumFacing.NORTH).uv(2.5f, 0.5f, 5.5f, 6.0f)
                    .addFace(EnumFacing.EAST).uv(2.5f, 0.5f, 5.5f, 6.0f)
                    .addFace(EnumFacing.SOUTH).uv(2.5f, 0.5f, 5.5f, 6.0f)
            ).combine().static())
        }
        if ((layer == BlockRenderLayer.CUTOUT) || (layer == null)) {
            bakeries.add(listOf(
                RawCube(Vec3d(1.0, 8.0, 1.0), Vec3d(31.0, 30.0, 31.0), ThingiesTexture.PUMP_SIDE.sprite)
                    .dualSide()
                    .addFace(EnumFacing.SOUTH).uv(0.5f, 8.5f, 7.5f, 14f)
                    .addFace(EnumFacing.EAST).uv(0.5f, 8.5f, 7.5f, 14f)
                    .addFace(EnumFacing.NORTH).uv(0.5f, 8.5f, 7.5f, 14f)
                    .addFace(EnumFacing.WEST).uv(0.5f, 8.5f, 7.5f, 14f)
            ).combine().static())
        }
        if (/*(layer == BlockRenderLayer.TRANSLUCENT) || */(layer == null)) {
//            bakeries.add(
//                CachedBakery({ _state, _stack, _side, _vertexFormat, _transform ->
//                    val info = this@SimpleTankBlock.getFluidStackInfo(_state, _stack)
//                    if (info != null) {
//                        val resource = info.fluid.still
//                        val sprite = (if (resource != null) Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(resource.toString()) else null)
//                            ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
//                        mutableListOf<BakedQuad>().also { list ->
//                            RawCube(Vec3d(1.1, 2.1, 1.1), Vec3d(30.9, 2.0 + 28.0 * (info.amount.toDouble() / 64.0) - 0.1, 30.9), sprite)
//                                .autoUV()
//                                .addFace(EnumFacing.EAST).color(info.fluid.color)
//                                .addFace(EnumFacing.SOUTH).color(info.fluid.color)
//                                .addFace(EnumFacing.WEST).color(info.fluid.color)
//                                .addFace(EnumFacing.NORTH).color(info.fluid.color)
//                                .addFace(EnumFacing.UP).color(info.fluid.color)
//                                .bake(list, _vertexFormat, _transform)
//                        }
//                    } else {
//                        mutableListOf()
//                    }
//                }).also { cache ->
//                    cache.keyGetter = { _state, _stack, _ ->
//                        val info = this.getFluidStackInfo(_state, _stack)
//                        if (info == null) {
//                            "no info"
//                        }
//                        else {
//                            "${info.fluid.name}::${info.amount}"
//                        }
//                    }
//                }
//            )
        }
        return bakeries.toList()
    }

    @SideOnly(Side.CLIENT)
    override fun renderTESR(proxy: TESRProxy, te: TileEntity, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)

        proxy.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        val fluid = (te as? PumpEntity)?.getFluid()
        if ((fluid != null) && (fluid.fluid != null) && (fluid.amount > 0)) {
            val amount = Math.round(fluid.amount.toFloat() / 375f * 4f) / 64.0f
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
                    val height = 22 * fluidPercent
                    val color = fluid.color
                    GlStateManager.color((color shr 16 and 0xFF) / 255.0f, (color shr 8 and 0xFF) / 255.0f, (color and 0xFF) / 255.0f, (color ushr 24 and 0xFF) / 255.0f)

                    val stillSprite = Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(still.toString())
                        ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                    if (stillSprite != null) {
                        this.drawSprite(
                            Vec3d(0.0, 22.0 - height, 0.0),
                            Vec3d(29.8, 22.0, 0.0),
                            stillSprite, false, true)
                        this.drawSprite(
                            Vec3d(0.0, 22.0 - height, 0.0),
                            Vec3d(0.0, 22.0, 29.8),
                            stillSprite, true, false)
                        this.drawSprite(
                            Vec3d(0.0, 22.0 - height, 29.8),
                            Vec3d(29.8, 22.0, 29.8),
                            stillSprite, true, false)
                        this.drawSprite(
                            Vec3d(29.8, 22.0 - height, 0.0),
                            Vec3d(29.8, 22.0, 29.8),
                            stillSprite, false, true)
                        this.drawSprite(
                            Vec3d(0.0, 22.0 - height, 29.8),
                            Vec3d(29.8, 22.0 - height, 0.0),
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

    //#endregion
}
