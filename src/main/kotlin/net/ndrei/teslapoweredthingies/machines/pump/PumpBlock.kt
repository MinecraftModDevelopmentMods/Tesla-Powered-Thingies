package net.ndrei.teslapoweredthingies.machines.pump

import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.render.selfrendering.*
import net.ndrei.teslapoweredthingies.client.ThingiesTexture
import net.ndrei.teslapoweredthingies.machines.BaseRunningBlock
import net.ndrei.teslapoweredthingies.machines.portablemultitank.UnlistedFluidProperty

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

    //#endregion

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
}
