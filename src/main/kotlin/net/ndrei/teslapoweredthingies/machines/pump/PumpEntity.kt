package net.ndrei.teslapoweredthingies.machines.pump

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.EnumDyeColor
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids.BlockFluidBase
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.ndrei.teslacorelib.tileentities.ElectricMachine
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslapoweredthingies.render.bakery.SelfRenderingTESR

class PumpEntity: ElectricMachine(SidedTileEntity::class.java.name.hashCode()) {
    private lateinit var tank: IFluidTank

    override fun initializeInventories() {
        super.initializeInventories()

        this.tank = this.addSimpleFluidTank(6000, "Fluid Tank", EnumDyeColor.BLUE, 100, 24)
    }

    override fun supportsAddons() = false
    override fun canBePaused() = false
    override val allowRedstoneControl: Boolean
        get() = false

    override fun getRenderers(): MutableList<TileEntitySpecialRenderer<TileEntity>> {
        return super.getRenderers().also { it.add(SelfRenderingTESR) }
    }

    fun getFluid(): FluidStack? = this.tank.fluid

    override fun performWork(): Float {
        if (this.pos.y > 0) {
            val under = this.getWorld().getBlockState(pos.down())
            val fluidBlock = under.block as? BlockFluidBase
            if (fluidBlock != null) {
                val fluid = fluidBlock.fluid
                this.tank.fill(FluidStack(fluid, Fluid.BUCKET_VOLUME), true)
                return 1.0f
            }
        }

        return 0.0f
    }
}
