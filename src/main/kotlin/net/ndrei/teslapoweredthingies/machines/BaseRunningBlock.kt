package net.ndrei.teslapoweredthingies.machines

import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import net.ndrei.teslacorelib.tileentities.SidedTileEntity

open class BaseRunningBlock<T : SidedTileEntity> : BaseThingyBlock<T> {
    protected constructor(registryName: String, teClass: Class<T>)
        : super(registryName, teClass)

    protected constructor(registryName: String, teClass: Class<T>, material: Material)
        : super(registryName, teClass, material)

    override fun createBlockState() = super.createBlockState().let {
        BlockStateContainer.Builder(this)
            .add(*it.properties.toTypedArray(), IS_RUNNING_PROPERTY)
            .add(
                *((it as? IExtendedBlockState)?.unlistedProperties?.map { it.key } ?: listOf()).toTypedArray(),
                *this.getUnlistedProperties().toTypedArray())
            .build()
    }

    protected open fun getUnlistedProperties(): MutableList<IUnlistedProperty<*>> = mutableListOf()

    override fun getStateFromMeta(meta: Int) =
        super.getStateFromMeta(meta shr 1).withProperty(IS_RUNNING_PROPERTY, (meta and 1) == 1)

    override fun getMetaFromState(state: IBlockState) =
        (super.getMetaFromState(state) shl 1) + (if (state.getValue(IS_RUNNING_PROPERTY)) 1 else 0)

    fun setIsRunning(world: World, pos: BlockPos, value: Boolean): Boolean {
        val state = if (world.isBlockLoaded(pos)) world.getBlockState(pos) else Blocks.AIR.defaultState
        if ((state.block === this) && (state.getValue(BaseRunningBlock.IS_RUNNING_PROPERTY) != value)) {
            val tileEntity = world.getTileEntity(pos)
            val newState = state.withProperty(BaseRunningBlock.IS_RUNNING_PROPERTY, value)
            world.setBlockState(pos, newState)
            if (tileEntity != null) {
                tileEntity.validate()
                world.setTileEntity(pos, tileEntity)
            }
            return true
        }
        return false
    }

    companion object {
        val IS_RUNNING_PROPERTY: PropertyBool = PropertyBool.create("running")
    }
}
