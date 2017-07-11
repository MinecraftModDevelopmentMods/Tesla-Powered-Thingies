package net.ndrei.teslapoweredthingies.machines.cropcloner

import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.GearRegistry
import net.ndrei.teslacorelib.MATERIAL_IRON
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.items.MachineCaseItem
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock
import net.ndrei.teslapoweredthingies.render.CropClonerSpecialRenderer

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterBlock
object CropClonerBlock
    : BaseThingyBlock<CropClonerEntity>("crop_cloner", CropClonerEntity::class.java) {

    private var _state: PropertyInteger? = null

    val STATE: PropertyInteger
        get() {
            if (_state == null)
                _state = PropertyInteger.create("state", 0, 1)!!
            return _state!!
        }

    init {
        super.setDefaultState(super.getDefaultState().withProperty(STATE, 0))
    }

    fun setState(newState: IBlockState, worldIn: World, pos: BlockPos) {
        val tileEntity = worldIn.getTileEntity(pos)
        worldIn.setBlockState(pos, newState)
        if (tileEntity != null) {
            tileEntity.validate()
            worldIn.setTileEntity(pos, tileEntity)
            worldIn.notifyNeighborsOfStateChange(pos, this, true)
        }
    }

    override val recipe: IRecipe
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "rhr",
                "dcd",
                "wgw",
                'r', "blockRedstone",
                'h', Items.DIAMOND_HOE,
                'd', "dirt",
                'c', MachineCaseItem,
                'w', "plankWood",
                'g', GearRegistry.getMaterial(MATERIAL_IRON)?.oreDictName ?: "gearIron"
        )

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, STATE)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val state = meta and 1
        var enumfacing = EnumFacing.getFront(meta shr 1)
        if (enumfacing.axis == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH
        }
        return this.getDefaultState()
                .withProperty(FACING, enumfacing)
                .withProperty(STATE, state)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        var meta = state.getValue(FACING).index
        meta = meta shl 1
        meta += state.getValue(STATE)
        return meta
    }

    override val specialRenderer: TileEntitySpecialRenderer<CropClonerEntity>
        @SideOnly(Side.CLIENT)
        get() = CropClonerSpecialRenderer()

    // TODO: find out what this should be replaced with
    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return false
    }

    // TODO: find out what this should be replaced with
    override fun isBlockNormalCube(state: IBlockState): Boolean {
        return false
    }

    // TODO: find out what this should be replaced with
    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    // TODO: find out what this should be replaced with
    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }
}
