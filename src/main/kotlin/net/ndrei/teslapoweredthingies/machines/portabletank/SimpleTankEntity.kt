package net.ndrei.teslapoweredthingies.machines.portabletank

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.EnumDyeColor
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslapoweredthingies.render.bakery.SelfRenderingTESR

/**
 * Created by CF on 2017-07-16.
 */
class SimpleTankEntity
    : SidedTileEntity(SimpleTankEntity::class.java.name.hashCode()) {

    private lateinit var tank: IFluidTank

    override fun initializeInventories() {
        super.initializeInventories()

        this.tank = this.addSimpleFluidTank(24000, "Fluid Tank", EnumDyeColor.BLUE, 20, 24)
    }

    override fun supportsAddons() = false
    override fun canBePaused() = false
    override val allowRedstoneControl: Boolean
        get() = false

    override fun getRenderers(): MutableList<TileEntitySpecialRenderer<TileEntity>> {
        return super.getRenderers().also { it.add(SelfRenderingTESR) }
    }

    fun getFluid(): FluidStack? = this.tank.fluid

    override fun innerUpdate() {
        // TODO: maybe blow up if the liquid is too hot or something? :S
    }
}