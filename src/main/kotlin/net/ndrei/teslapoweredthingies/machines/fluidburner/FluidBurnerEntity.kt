package net.ndrei.teslapoweredthingies.machines.fluidburner

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.FilteredSlot
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.FluidTankPiece
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.*
import net.ndrei.teslacorelib.utils.FluidUtils
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.gui.FluidBurnerTankPiece
import net.ndrei.teslapoweredthingies.gui.GeneratorBurnPiece
import net.ndrei.teslapoweredthingies.gui.IMultiTankMachine
import net.ndrei.teslapoweredthingies.gui.TankInfo
import net.ndrei.teslapoweredthingies.machines.BaseThingyGenerator
import net.ndrei.teslapoweredthingies.render.DualTankEntityRenderer
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Created by CF on 2017-06-30.
 */
class FluidBurnerEntity : BaseThingyGenerator(FluidBurnerEntity::class.java.name.hashCode()), IMultiTankMachine {
    private lateinit var coolantTank: IFluidTank
    private lateinit var fuelTank: IFluidTank
    private lateinit var coolantItems: ItemStackHandler
    private lateinit var fuelItems: ItemStackHandler

    init {
        super.registerSyncTagPart(SYNC_CURRENT_FUEL, Consumer {
            this.fuelInUse = if (it.hasKey("fuelInUse", Constants.NBT.TAG_STRING)) {
                FluidRegistry.getFluid(it.getString("fuelInUse"))
            } else null

            this.coolantInUse = if (it.hasKey("coolantInUse", Constants.NBT.TAG_STRING)) {
                FluidRegistry.getFluid(it.getString("coolantInUse"))
            } else null
        }, Supplier {
            NBTTagCompound().also {
                if (this.fuelInUse != null) {
                    it.setString("fuelInUse", FluidRegistry.getFluidName(this.fuelInUse))
                }

                if (this.coolantInUse != null) {
                    it.setString("coolantInUse", FluidRegistry.getFluidName(this.coolantInUse))
                }
            }
        }, SyncProviderLevel.GUI)
    }

    var coolantInUse: Fluid? = null
        private set
    var fuelInUse: Fluid? = null
        private set

    override fun initializeInventories() {
        super.initializeInventories()

        this.coolantItems = SyncItemHandler(2)
        super.addInventory(object : ColoredItemHandler(this.coolantItems, EnumDyeColor.MAGENTA, "Coolant Containers", BoundingRectangle(61, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return slot == 0 && FluidUtils.canFillFrom(this@FluidBurnerEntity.coolantTank, stack)
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
                        Textures.MACHINES_TEXTURES.resource, 6, 44))

                return pieces
            }
        })
        super.addInventoryToStorage(this.coolantItems, "inv_coolant")

//        this.coolantTank = object: FluidTank(5000) {
//            override fun canFillFluidType(fluid: FluidStack?)
//                    = if (fluid != null) FluidBurnerRecipes.isCoolant(fluid) else false
//
//            override fun onContentsChanged() {
//                this@FluidBurnerEntity.markDirty()
//            }
//        }
//        super.addFluidTank(
//                object : ColoredFluidHandler(this.coolantTank,
//                        EnumDyeColor.BLUE,
//                        "Coolant Tank",
//                        BoundingRectangle(79, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
////                    override fun acceptsFluid(fluid: FluidStack): Boolean {
////                        return FluidBurnerRecipes.isCoolant(fluid)
////                    }
////
//                    override fun canDrain() = false
//                }, null
//        )
        this.coolantTank = this.addSimpleFluidTank(5000, "Coolant Tank", EnumDyeColor.BLUE,
            79, 25, FluidTankType.INPUT, { FluidBurnerRecipes.isCoolant(it) })

//        this.fuelTank = object: FluidTank(5000) {
//            override fun canFillFluidType(fluid: FluidStack?)
//                    = if (fluid != null) FluidBurnerRecipes.isFuel(fluid) else false
//
//            override fun onContentsChanged() {
//                this@FluidBurnerEntity.markDirty()
//            }
//
//        }
//        super.addFluidTank(
//                object : ColoredFluidHandler(this.fuelTank,
//                        EnumDyeColor.RED,
//                        "Fuel Tank",
//                        BoundingRectangle(97, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
////                    override fun acceptsFluid(fluid: FluidStack): Boolean {
////                        return FluidBurnerRecipes.isFuel(fluid)
////                    }
//
//                    override fun canDrain() = false
//                }, null
//        )
        this.fuelTank = this.addSimpleFluidTank(5000, "Fuel Tank", EnumDyeColor.RED,
            97, 25, FluidTankType.INPUT, { FluidBurnerRecipes.isFuel(it) })

        this.fuelItems = SyncItemHandler(2)
        super.addInventory(object : ColoredItemHandler(this.fuelItems, EnumDyeColor.PURPLE, "Fuel Containers", BoundingRectangle(115, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return slot == 0 && FluidUtils.canFillFrom(this@FluidBurnerEntity.fuelTank, stack)
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
        super.addInventoryToStorage(this.fuelItems, "inv_fuel")
    }

    override fun shouldAddFluidItemsInventory(): Boolean = false

    override fun processImmediateInventories() {
        super.processImmediateInventories()

        this.processFluidItems(this.coolantItems, this.coolantTank)
        this.processFluidItems(this.fuelItems, this.fuelTank)
    }

    private fun processFluidItems(handler: ItemStackHandler, tank: IFluidTank) {
        val stack = handler.getStackInSlot(0)
        if (!stack.isEmpty && FluidUtils.canFillFrom(tank, stack)) {
            val result = FluidUtils.fillFluidFrom(tank, stack)
            if (!ItemStack.areItemStacksEqual(stack, result)) {
                handler.setStackInSlot(0, result)
                this.discardUsedFluidItem(handler)
            }
        } else if (!stack.isEmpty) {
            this.discardUsedFluidItem(handler)
        }
    }

    private fun discardUsedFluidItem(handler: ItemStackHandler) {
        val source = handler.getStackInSlot(0)
        val result = handler.insertItem(1, source, false)
        handler.setStackInSlot(0, result)
    }

    override fun consumeFuel(): Long {
        val fuel = FluidBurnerRecipes.drainFuel(this.fuelTank, true)
        if (fuel != null) {
            var power = fuel.recipe.baseTicks.toLong()
            this.fuelInUse = fuel.fuel.fluid

            val coolant = FluidBurnerRecipes.drainCoolant(this.coolantTank, true)
            if (coolant != null) {
                power *= coolant.recipe.timeMultiplier.toLong()
                this.coolantInUse = coolant.coolant.fluid
            }

            this.partialSync(SYNC_CURRENT_FUEL)
            power *= this.energyFillRate
            return power
        }

        return 0
    }

    override fun fuelConsumed() {
        this.fuelInUse = null
        this.coolantInUse = null
        this.partialSync(SYNC_CURRENT_FUEL)
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

    override fun getRenderers(): MutableList<TileEntitySpecialRenderer<in TileEntity>> {
        val list = super.getRenderers()
        list.add(DualTankEntityRenderer)
        return list
    }

//    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
//        var compound = compound
//        compound = super.writeToNBT(compound)
//
//        if (this.fuelInUse != null) {
//            compound.setString("fuelInUse", FluidRegistry.getFluidName(this.fuelInUse))
//        }
//
//        if (this.coolantInUse != null) {
//            compound.setString("coolantInUse", FluidRegistry.getFluidName(this.coolantInUse))
//        }
//
//        return compound
//    }

//    override fun readFromNBT(compound: NBTTagCompound) {
//        super.readFromNBT(compound)
//
//        if (compound.hasKey("fuelInUse")) {
//            this.fuelInUse = FluidRegistry.getFluid(compound.getString("fuelInUse"))
//        }
//
//        if (compound.hasKey("coolantInUse")) {
//            this.coolantInUse = FluidRegistry.getFluid(compound.getString("coolantInUse"))
//        }
//    }

    override fun getTanks()
        = listOf(
            TankInfo(6.0, 6.0, this.coolantTank.fluid, this.coolantTank.capacity),
            TankInfo(20.0, 6.0, this.fuelTank.fluid, this.fuelTank.capacity)
    )

//
//    override val leftTankPercent: Float
//        get() = Math.min(1f, Math.max(0f, this.coolantTank.fluidAmount.toFloat() / this.coolantTank.capacity.toFloat()))
//
//    override val rightTankPercent: Float
//        get() = Math.min(1f, Math.max(0f, this.fuelTank.fluidAmount.toFloat() / this.fuelTank.capacity.toFloat()))
//
//    override val leftTankFluid: Fluid?
//        get() = this.coolantTank.fluid?.fluid
//
//    override val rightTankFluid: Fluid?
//        get() = this.fuelTank.fluid?.fluid

    companion object {
        const val SYNC_CURRENT_FUEL = "current_fuel"
    }
}
