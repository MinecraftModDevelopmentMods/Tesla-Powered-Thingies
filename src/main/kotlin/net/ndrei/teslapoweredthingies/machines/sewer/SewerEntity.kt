package net.ndrei.teslapoweredthingies.machines.sewer

import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.init.Items
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.utils.BlockCube
import net.ndrei.teslacorelib.utils.BlockPosUtils
import net.ndrei.teslapoweredthingies.fluids.SewageFluid
import net.ndrei.teslapoweredthingies.machines.BaseXPCollectingMachine
import net.ndrei.teslapoweredthingies.machines.SEWER_FARM_WORK_AREA_COLOR

/**
 * Created by CF on 2017-07-07.
 */
class SewerEntity : BaseXPCollectingMachine(SewerEntity::class.java.name.hashCode()) {
    private var sewageTank: IFluidTank? = null

    //#region inventory management

    override fun initializeInventories() {
        super.initializeInventories()

        this.sewageTank = super.addFluidTank(SewageFluid, 10000, EnumDyeColor.BROWN, "Sewage Tank",
                BoundingRectangle(43, 25, 18, 54))
        // this.sewageTank!!.fill(FluidStack(SewageFluid, 2000), true)
    }

    override val inputSlots: Int
        get() = 0

    override val outputSlots: Int
        get() = 0

    override fun getWorkAreaColor(): Int = SEWER_FARM_WORK_AREA_COLOR

    override fun acceptsFluidItem(stack: ItemStack): Boolean {
        if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            val handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
            if (handler != null) {
                if (1 == handler.fill(FluidStack(SewageFluid, 1), false)) {
                    return true
                }
            }
        }

        return stack.item === Items.BUCKET
    }

    override fun processFluidItems(fluidItems: ItemStackHandler) {
        val stack = fluidItems.getStackInSlot(0)
        val outputStack = fluidItems.getStackInSlot(1)
        if (!ItemStackUtil.isEmpty(stack) && ItemStackUtil.isEmpty(outputStack)) {
            val input = ItemStackUtil.copyWithSize(stack, 1)
            val result = FluidUtil.tryFillContainer(input, this.fluidHandler, Fluid.BUCKET_VOLUME, null, true)
            if (result.isSuccess) {
                ItemStackUtil.shrink(stack, 1)
                if (stack.isEmpty) {
                    fluidItems.setStackInSlot(0, ItemStackUtil.emptyStack)
                }
                fluidItems.setStackInSlot(1, result.getResult())
            }
        }
    }

    //#endregion

    override val energyForWork: Int
        get() = 20

    override val energyForWorkRate: Int
        get() = 1

    override fun getWorkArea() = this.getWorkArea(EnumFacing.UP, 2)

    override fun performWorkInternal(): Float {
        val cube = this.getWorkArea() // EnumFacing.UP, 2)
        var sewage = 0
        for (animal in this.getWorld().getEntitiesWithinAABB(EntityAnimal::class.java, cube.boundingBox)) {
            sewage += Math.round(animal.getMaxHealth())
        }
        if (sewage > 0) {
            this.sewageTank!!.fill(FluidStack(SewageFluid, sewage), true)
        }
        return 1.0f
    }

    override val xpOrbLookupCube: BlockCube
        get() = BlockPosUtils.getCube(this.getPos(), EnumFacing.UP, 4, 3)
}
