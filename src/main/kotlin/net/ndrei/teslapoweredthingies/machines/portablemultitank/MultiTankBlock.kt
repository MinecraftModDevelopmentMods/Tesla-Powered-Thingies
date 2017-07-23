package net.ndrei.teslapoweredthingies.machines.portablemultitank

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock
import net.ndrei.teslapoweredthingies.render.bakery.*

/**
 * Created by CF on 2017-07-16.
 */
@AutoRegisterBlock
@SelfRenderingBlock
object MultiTankBlock
    : BaseThingyBlock<MultiTankEntity>("multi_tank", MultiTankEntity::class.java), ISelfRenderingBlock {

    //#region FLUID_ Properties

    private var _fluid1Prop: UnlistedFluidProperty? = null
    private var _fluid2Prop: UnlistedFluidProperty? = null
    private var _fluid3Prop: UnlistedFluidProperty? = null
    private var _fluid4Prop: UnlistedFluidProperty? = null

    val FLUID_1_PROP: UnlistedFluidProperty
        get() {
            if (this._fluid1Prop == null) {
                this._fluid1Prop = UnlistedFluidProperty("fluid1")
            }
            return this._fluid1Prop!!
        }
    val FLUID_2_PROP: UnlistedFluidProperty
        get() {
            if (this._fluid2Prop == null) {
                this._fluid2Prop = UnlistedFluidProperty("fluid2")
            }
            return this._fluid2Prop!!
        }
    val FLUID_3_PROP: UnlistedFluidProperty
        get() {
            if (this._fluid3Prop == null) {
                this._fluid3Prop = UnlistedFluidProperty("fluid3")
            }
            return this._fluid3Prop!!
        }
    val FLUID_4_PROP: UnlistedFluidProperty
        get() {
            if (this._fluid4Prop == null) {
                this._fluid4Prop = UnlistedFluidProperty("fluid4")
            }
            return this._fluid4Prop!!
        }

    //#endregion
    val FLUID_PROPS get() = arrayOf(FLUID_1_PROP, FLUID_2_PROP, FLUID_3_PROP, FLUID_4_PROP)

    override fun getExtendedState(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): IBlockState {
        // TeslaThingiesMod.logger.info("Getting extended state for $pos")
        if ((state is IExtendedBlockState) && (world != null) && (pos != null)) {
            val te = world.getTileEntity(pos)
            if (te is MultiTankEntity) {
                return (0..3).fold(state) { it, index ->
                    val f = te.getFluid(index)

                    if (f != null)
                        it.withProperty(FLUID_PROPS[index], f)
                    else
                        it
                }
            }
        }
        return super.getExtendedState(state, world, pos)
    }

    //#region Block Overrides

    override fun createBlockState()
            = ExtendedBlockState(this, arrayOf(FACING), arrayOf(FLUID_1_PROP, FLUID_2_PROP, FLUID_3_PROP, FLUID_4_PROP))

    override fun registerItemBlock(registry: IForgeRegistry<Item>) {
        registry.register(MultiTankItem)
    }

    override fun canRenderInLayer(state: IBlockState?, layer: BlockRenderLayer?): Boolean {
        return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT
    }

    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isTranslucent(state: IBlockState?) = true
    override fun doesSideBlockRendering(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = false

    //#endregion

    override fun getTextures(): List<ResourceLocation> {
        return listOf(Textures.MULTI_TANK_SIDE.resource)
    }

    override fun getBakeries(layer: BlockRenderLayer?, state: IBlockState?, stack: ItemStack?, side: EnumFacing?, rand: Long): List<IBakery> {
        val bakeries = mutableListOf<IBakery>()
        // TeslaThingiesMod.logger.info("Building bakeries for '${layer?.toString() ?: "NO LAYER"}'.")

        if ((layer == BlockRenderLayer.SOLID) || (layer == null)) {
            bakeries.add(listOf(
                    RawCube(Vec3d(0.0, 0.0, 0.0), Vec3d(32.0, 2.0, 32.0), Textures.MULTI_TANK_SIDE.sprite)
                            .addFace(EnumFacing.UP).uv(8.0f, 8.0f, 16.0f, 16.0f)
                            .addFace(EnumFacing.DOWN).uv(8.0f, 8.0f, 16.0f, 16.0f)
                            .addFace(EnumFacing.WEST).uv(0.0f, 0.0f, 8.0f, 0.5f)
                            .addFace(EnumFacing.NORTH).uv(0.0f, 0.0f, 8.0f, 0.5f)
                            .addFace(EnumFacing.EAST).uv(0.0f, 0.0f, 8.0f, 0.5f)
                            .addFace(EnumFacing.SOUTH).uv(0.0f, 0.0f, 8.0f, 0.5f),
                    RawCube(Vec3d(1.0, 2.0, 1.0), Vec3d(15.0, 30.0, 15.0), Textures.MULTI_TANK_SIDE.sprite)
                            .dualSide()
                            .addFace(EnumFacing.SOUTH).uv(4.5f, 0.5f, 8.0f, 7.5f)
                            .addFace(EnumFacing.EAST).uv(4.5f, 0.5f, 8.0f, 7.5f),
                    RawCube(Vec3d(17.0, 2.0, 1.0), Vec3d(31.0, 30.0, 15.0), Textures.MULTI_TANK_SIDE.sprite)
                            .dualSide()
                            .addFace(EnumFacing.SOUTH).uv(4.5f, 0.5f, 8.0f, 7.5f)
                            .addFace(EnumFacing.WEST).uv(4.5f, 0.5f, 8.0f, 7.5f),
                    RawCube(Vec3d(17.0, 2.0, 17.0), Vec3d(31.0, 30.0, 31.0), Textures.MULTI_TANK_SIDE.sprite)
                            .dualSide()
                            .addFace(EnumFacing.NORTH).uv(4.5f, 0.5f, 8.0f, 7.5f)
                            .addFace(EnumFacing.WEST).uv(4.5f, 0.5f, 8.0f, 7.5f),
                    RawCube(Vec3d(1.0, 2.0, 17.0), Vec3d(15.0, 30.0, 31.0), Textures.MULTI_TANK_SIDE.sprite)
                            .dualSide()
                            .addFace(EnumFacing.NORTH).uv(4.5f, 0.5f, 8.0f, 7.5f)
                            .addFace(EnumFacing.EAST).uv(4.5f, 0.5f, 8.0f, 7.5f),
                    RawCube(Vec3d(0.0, 30.0, 0.0), Vec3d(32.0, 32.0, 32.0), Textures.MULTI_TANK_SIDE.sprite)
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
                    RawCube(Vec3d(1.0, 2.0, 1.0), Vec3d(15.0, 30.0, 15.0), Textures.MULTI_TANK_SIDE.sprite)
                            .dualSide()
                            .addFace(EnumFacing.NORTH).uv(0.0f, 8.5f, 3.5f, 15.5f)
                            .addFace(EnumFacing.WEST).uv(0.0f, 8.5f, 3.5f, 15.5f),
                    RawCube(Vec3d(17.0, 2.0, 1.0), Vec3d(31.0, 30.0, 15.0), Textures.MULTI_TANK_SIDE.sprite)
                            .dualSide()
                            .addFace(EnumFacing.NORTH).uv(0.0f, 8.5f, 3.5f, 15.5f)
                            .addFace(EnumFacing.EAST).uv(0.0f, 8.5f, 3.5f, 15.5f),
                    RawCube(Vec3d(17.0, 2.0, 17.0), Vec3d(31.0, 30.0, 31.0), Textures.MULTI_TANK_SIDE.sprite)
                            .dualSide()
                            .addFace(EnumFacing.SOUTH).uv(0.0f, 8.5f, 3.5f, 15.5f)
                            .addFace(EnumFacing.EAST).uv(0.0f, 8.5f, 3.5f, 15.5f),
                    RawCube(Vec3d(1.0, 2.0, 17.0), Vec3d(15.0, 30.0, 31.0), Textures.MULTI_TANK_SIDE.sprite)
                            .dualSide()
                            .addFace(EnumFacing.SOUTH).uv(0.0f, 8.5f, 3.5f, 15.5f)
                            .addFace(EnumFacing.WEST).uv(0.0f, 8.5f, 3.5f, 15.5f)
            ).combine().static())
        }
        if ((layer == BlockRenderLayer.TRANSLUCENT) || (layer == null)) {
            val xs = arrayOf(1.1, 1.1, 17.1, 17.1)
            val zs = arrayOf(1.1, 17.1, 17.1, 1.1)
            val f1s = arrayOf(EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.SOUTH, EnumFacing.NORTH)
            val f2s = arrayOf(EnumFacing.WEST, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.EAST)
            (0..3).mapTo(bakeries) {
                CachedBakery({ _state, _stack, _side, _vertexFormat ->
                    val info = this@MultiTankBlock.getFluidStackInfo(_state, _stack, it)
                    if (info != null) {
                        val resource = info.fluid.still
                        val sprite = (if (resource != null) Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(resource.toString()) else null)
                            ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
                        mutableListOf<BakedQuad>().also { list ->
                            RawCube(Vec3d(xs[it], 2.1, zs[it]), Vec3d(xs[it] + 13.8, 2.0 + 28.0 * (info.amount.toDouble() / 64.0) - 0.1, zs[it] + 13.8), sprite)
                                    .autoUV()
                                    .addFace(f1s[it]).color(info.fluid.color)
                                    .addFace(f2s[it]).color(info.fluid.color)
                                    .addFace(EnumFacing.UP).color(info.fluid.color)
                                    .bake(list, _vertexFormat)
                        }
                    }
                    else {
                        mutableListOf<BakedQuad>()
                    }
                }).also { cache ->
                    cache.keyGetter = { _state, _stack, _ ->
                        val info = this.getFluidStackInfo(_state, _stack, it)
                        if (info == null) {
                            "no info"
                        }
                        else {
                            "${info.fluid.name}::${info.amount}"
                        }
                    }
                }
            }
        }
        return bakeries.toList()
    }

    private fun getFluidStackInfo(state: IBlockState?, stack: ItemStack?, index: Int): FluidStackInfo? {
        val extended = (state as? IExtendedBlockState)
        if (extended != null) {
            val fluid = extended.getValue(MultiTankBlock.FLUID_PROPS[index])
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
                        if (tNBT.tagCount() > index) {
                            val tank = tNBT.getCompoundTagAt(index)
                            val fluid = FluidStack.loadFluidStackFromNBT(tank)
                            if (fluid != null) {
                                return FluidStackInfo(fluid.fluid, Math.round(fluid.amount.toFloat() / 93.75f))
                            }
                        }
                    }
                }
            }
        }

        return null
    }

    class FluidStackInfo(val fluid: Fluid, val amount: Int)
}
