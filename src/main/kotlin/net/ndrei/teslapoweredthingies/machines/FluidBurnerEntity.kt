package net.ndrei.teslapoweredthingies.machines

import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.FilteredSlot
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.FluidTankPiece
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredFluidHandler
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.FluidTank
import net.ndrei.teslacorelib.tileentities.ElectricGenerator
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.FluidUtils
import net.ndrei.teslapoweredthingies.gui.FluidBurnerTankPiece
import net.ndrei.teslapoweredthingies.gui.GeneratorBurnPiece
import net.ndrei.teslapoweredthingies.gui.IDualTankMachine
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerRecipes

/**
 * Created by CF on 2017-06-30.
 */
class FluidBurnerEntity : BaseThingyGenerator(FluidBurnerEntity::class.java.name.hashCode()), IDualTankMachine {
    private var coolantTank: FluidTank? = null
    private var fuelTank: FluidTank? = null
    private var coolantItems: ItemStackHandler? = null
    private var fuelItems: ItemStackHandler? = null

    var coolantInUse: Fluid? = null
        private set
    var fuelInUse: Fluid? = null
        private set

    override fun initializeInventories() {
        super.initializeInventories()

        this.coolantItems = ItemStackHandler(2)
        super.addInventory(object : ColoredItemHandler(this.coolantItems!!, EnumDyeColor.MAGENTA, "Coolant Containers", BoundingRectangle(61, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return slot == 0 && FluidUtils.canFillFrom(this@FluidBurnerEntity.coolantTank!!, stack)
            }

            override fun canExtractItem(slot: Int): Boolean {
                return slot != 0
            }

            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                val slots = mutableListOf<Slot>()

                val box = this.boundingBox
                slots.add(FilteredSlot(this.itemHandlerForContainer, 0, box.left + 1, box.top + 1))
                slots.add(FilteredSlot(this.itemHandlerForContainer, 1, box.left + 1, box.top + 1 + 36))

                return slots
            }

            override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
                val pieces = mutableListOf<IGuiContainerPiece>()

                val box = this.boundingBox
                pieces.add(BasicRenderedGuiPiece(box.left, box.top, 18, 54,
                        TeslaThingiesMod.MACHINES_TEXTURES, 6, 44))

                return pieces
            }
        })
        super.addInventoryToStorage(this.coolantItems!!, "inv_coolant")

        this.coolantTank = FluidTank(5000)
        super.addFluidTank(
                object : ColoredFluidHandler(this.coolantTank!!,
                        EnumDyeColor.BLUE,
                        "Coolant Tank",
                        BoundingRectangle(79, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
                    override fun acceptsFluid(fluid: FluidStack): Boolean {
                        return FluidBurnerRecipes.isCoolant(fluid)
                    }

                    override fun canDrain(): Boolean {
                        return false
                    }
                }, null
        )

        this.fuelTank = FluidTank(5000)
        super.addFluidTank(
                object : ColoredFluidHandler(this.fuelTank!!,
                        EnumDyeColor.RED,
                        "Fuel Tank",
                        BoundingRectangle(97, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
                    override fun acceptsFluid(fluid: FluidStack): Boolean {
                        return FluidBurnerRecipes.isFuel(fluid)
                    }

                    override fun canDrain(): Boolean {
                        return false
                    }
                }, null
        )

        this.fuelItems = ItemStackHandler(2)
        super.addInventory(object : ColoredItemHandler(this.fuelItems!!, EnumDyeColor.PURPLE, "Fuel Containers", BoundingRectangle(115, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return slot == 0 && FluidUtils.canFillFrom(this@FluidBurnerEntity.fuelTank!!, stack)
            }

            override fun canExtractItem(slot: Int): Boolean {
                return slot != 0
            }

            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                val slots = mutableListOf<Slot>()

                val box = this.boundingBox
                slots.add(FilteredSlot(this.itemHandlerForContainer, 0, box.left + 1, box.top + 1))
                slots.add(FilteredSlot(this.itemHandlerForContainer, 1, box.left + 1, box.top + 1 + 36))

                return slots
            }

            override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
                val pieces = mutableListOf<IGuiContainerPiece>()

                val box = this.boundingBox
                pieces.add(BasicRenderedGuiPiece(box.left, box.top, 18, 54,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 78, 189))

                return pieces
            }
        })
        super.addInventoryToStorage(this.fuelItems!!, "inv_fuel")
    }

    override fun shouldAddFluidItemsInventory(): Boolean = false

    override fun processImmediateInventories() {
        super.processImmediateInventories()

        this.processFluidItems(this.coolantItems!!, this.coolantTank!!)
        this.processFluidItems(this.fuelItems!!, this.fuelTank!!)
    }

    private fun processFluidItems(handler: ItemStackHandler, tank: IFluidTank) {
        val stack = handler.getStackInSlot(0)
        if (!ItemStackUtil.isEmpty(stack) && FluidUtils.canFillFrom(tank, stack)) {
            val result = FluidUtils.fillFluidFrom(tank, stack)
            if (!ItemStack.areItemStacksEqual(stack, result)) {
                handler.setStackInSlot(0, result)
                this.discardUsedFluidItem(handler)
            }
        } else if (!ItemStackUtil.isEmpty(stack)) {
            this.discardUsedFluidItem(handler)
        }
    }

    private fun discardUsedFluidItem(handler: ItemStackHandler) {
        val source = handler.getStackInSlot(0)
        val result = handler.insertItem(1, source, false)
        handler.setStackInSlot(0, result)
    }

    override fun consumeFuel(): Long {
        val fuel = FluidBurnerRecipes.drainFuel(this.fuelTank!!, true)
        if (fuel != null) {
            var power = fuel.recipe.baseTicks.toLong()
            this.fuelInUse = fuel.fuel.fluid

            val coolant = FluidBurnerRecipes.drainCoolant(this.coolantTank!!, true)
            if (coolant != null) {
                power *= coolant.recipe.timeMultiplier.toLong()
                this.coolantInUse = coolant.coolant.fluid
            }

            power *= this.energyFillRate
            return power
        }

        return 0
    }

    override val energyOutputRate: Long
        get() = 80

    override val energyFillRate: Long
        get() = 80

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val pieces = super.getGuiContainerPieces(container)

        pieces.add(GeneratorBurnPiece(144, 63, this))

        pieces.add(FluidBurnerTankPiece(142, 27, this))

        return pieces
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        var compound = compound
        compound = super.writeToNBT(compound)

        if (this.fuelInUse != null) {
            compound.setString("fuelInUse", FluidRegistry.getFluidName(this.fuelInUse))
        }

        if (this.coolantInUse != null) {
            compound.setString("coolantInUse", FluidRegistry.getFluidName(this.coolantInUse))
        }

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        if (compound.hasKey("fuelInUse")) {
            this.fuelInUse = FluidRegistry.getFluid(compound.getString("fuelInUse"))
        }

        if (compound.hasKey("coolantInUse")) {
            this.coolantInUse = FluidRegistry.getFluid(compound.getString("coolantInUse"))
        }
    }

    override val leftTankPercent: Float
        get() = Math.min(1f, Math.max(0f, this.coolantTank!!.fluidAmount.toFloat() / this.coolantTank!!.capacity.toFloat()))

    override val rightTankPercent: Float
        get() = Math.min(1f, Math.max(0f, this.fuelTank!!.fluidAmount.toFloat() / this.fuelTank!!.capacity.toFloat()))

    override val leftTankFluid: Fluid
        get() {
            val stack = this.coolantTank!!.fluid
            return stack?.fluid!!
        }

    override val rightTankFluid: Fluid
        get() {
            val stack = this.fuelTank!!.fluid
            return stack?.fluid!!
        }
}