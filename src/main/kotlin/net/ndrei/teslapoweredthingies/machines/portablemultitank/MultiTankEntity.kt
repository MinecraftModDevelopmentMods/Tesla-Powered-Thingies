package net.ndrei.teslapoweredthingies.machines.portablemultitank

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.EnumDyeColor
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.ndrei.teslacorelib.gui.FluidTankPiece
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslapoweredthingies.render.bakery.SelfRenderingTESR

/**
 * Created by CF on 2017-07-16.
 */
class MultiTankEntity
    : SidedTileEntity(MultiTankEntity::class.java.name.hashCode()) {

    private lateinit var tanks: MutableList<IFluidTank>

    override fun initializeInventories() {
        super.initializeInventories()

        this.tanks = mutableListOf()
        arrayOf(EnumDyeColor.BLUE, EnumDyeColor.GREEN, EnumDyeColor.ORANGE, EnumDyeColor.RED).forEachIndexed { index, it ->
            this.tanks.add(this.addSimpleFluidTank(TANK_CAPACITY, "Tank $index", it,
                20 + FluidTankPiece.WIDTH * index, 24))
        }
    }

    override fun supportsAddons() = false
    override fun canBePaused() = false
    override val allowRedstoneControl: Boolean
        get() = false

    override fun setWorld(worldIn: World?) {
        super.setWorld(worldIn)
        this.getWorld().markBlockRangeForRenderUpdate(this.getPos(), this.getPos())
    }

    override fun getRenderers(): MutableList<TileEntitySpecialRenderer<TileEntity>> {
        return super.getRenderers().also { it.add(SelfRenderingTESR) }
    }

    fun getFluid(tankIndex: Int): FluidStack? = this.tanks[tankIndex].fluid

    override fun readFromNBT(compound: NBTTagCompound) {
        val initialFluids = this.tanks.map { it.fluid }
        super.readFromNBT(compound)
        val finalFluids = this.tanks.map { it.fluid }

        if (this.hasWorld () && this.getWorld().isRemote && (0..3).any {
            if (initialFluids[it] == null)
                (finalFluids[it] != null)
            else
                !initialFluids[it]!!.isFluidStackIdentical(finalFluids[it])
        }) {
            this.getWorld().markBlockRangeForRenderUpdate(this.pos, this.pos)
        }
    }

    override fun innerUpdate() {
        // TODO: maybe blow up if the liquid is too hot or something? :S
    }

    companion object {
        const val TANK_CAPACITY = 6000
    }
}