package net.ndrei.teslapoweredthingies.machines.liquidxpstorage

import net.minecraft.init.Items
import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.FilteredSlot
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.FilteredFluidTank
import net.ndrei.teslacorelib.inventory.FluidTank
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.fluids.LiquidXPFluid

/**
 * Created by CF on 2017-07-07.
 */
class LiquidXPStorageEntity : SidedTileEntity(LiquidXPStorageEntity::class.java.name.hashCode()) {
    private lateinit var inputItems: ItemStackHandler
    private lateinit var outputItems: ItemStackHandler
    private lateinit var xpTank: FilteredFluidTank

    //#region inventories

    override fun initializeInventories() {
        super.initializeInventories()

        this.inputItems = object : ItemStackHandler(2) {
            override fun getStackLimit(slot: Int, stack: ItemStack): Int {
                if (slot == 0) {
                    return 1
                }
                return super.getStackLimit(slot, stack)
            }

            override fun onContentsChanged(slot: Int) {
                this@LiquidXPStorageEntity.markDirty()
            }
        }
        this.outputItems = object : ItemStackHandler(2) {
//            override fun getStackLimit(slot: Int, stack: ItemStack): Int {
//                if (slot == 1) {
//                    return 1
//                }
//                return super.getStackLimit(slot, stack)
//            }

            override fun onContentsChanged(slot: Int) {
                this@LiquidXPStorageEntity.markDirty()
            }
        }
        this.xpTank = FilteredFluidTank(LiquidXPFluid, object : FluidTank(1500) {
            override fun onContentsChanged() {
                this@LiquidXPStorageEntity.markDirty()
            }
        })

        super.addInventory(object : ColoredItemHandler(this.inputItems, EnumDyeColor.GREEN,
                "Input Liquid Containers", BoundingRectangle(56, 25, 18, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return slot == 0 && this@LiquidXPStorageEntity.isValidInContainer(stack)
            }

            override fun canExtractItem(slot: Int): Boolean {
                return slot == 1
            }

            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                val slots = mutableListOf<Slot>()
                val box = this.boundingBox
                if (!box.isEmpty) {
                    slots.add(FilteredSlot(this.itemHandlerForContainer, 0, box.left + 1, box.top + 1))
                    slots.add(FilteredSlot(this.itemHandlerForContainer, 1, box.left + 1, box.top + 1 + 36))
                }
                return slots
            }

            override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> = mutableListOf()
        })
        super.addInventoryToStorage(this.inputItems, "income")

        super.addFluidTank(this.xpTank, EnumDyeColor.LIME, "Liquid XP",
                BoundingRectangle(79, 25, 18, 54))

        super.addInventory(object : ColoredItemHandler(this.outputItems, EnumDyeColor.PURPLE,
                "Output Liquid Containers", BoundingRectangle(102, 25, 18, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return slot == 0 && this@LiquidXPStorageEntity.isValidOutContainer(stack)
            }

            override fun canExtractItem(slot: Int): Boolean {
                return slot == 1
            }

            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                val slots = mutableListOf<Slot>()
                val box = this.boundingBox
                if (!box.isEmpty) {
                    slots.add(FilteredSlot(this.itemHandlerForContainer, 0, box.left + 1, box.top + 1))
                    slots.add(FilteredSlot(this.itemHandlerForContainer, 1, box.left + 1, box.top + 1 + 36))
                }
                return slots
            }

            override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> = mutableListOf()
        })
        super.addInventoryToStorage(this.outputItems, "outcome")
    }

    private fun isValidInContainer(stack: ItemStack): Boolean {
        if (!ItemStackUtil.isEmpty(stack)) {
            if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                val handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
                if (handler != null) {
                    val tanks = handler.tankProperties
                    if (tanks != null && tanks.size > 0) {
                        for (tank in tanks) {
                            if (tank.canDrain()) {
                                val content = tank.contents
                                if (content != null && content.amount > 0 && content.fluid === LiquidXPFluid) {
                                    return true
                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    private fun isValidOutContainer(stack: ItemStack): Boolean {
        if (!ItemStackUtil.isEmpty(stack)) {
            val item = stack.item
            if (item === Items.GLASS_BOTTLE || item === Items.BUCKET) {
                return true
            }

            if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                val handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
                if (handler != null) {
                    val tanks = handler.tankProperties
                    if (tanks != null && tanks.size > 0) {
                        for (tank in tanks) {
                            if (tank.canFill()) {
                                val content = tank.contents
                                if (content == null || content.amount < tank.capacity && content.fluid === LiquidXPFluid) {
                                    return true
                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    override fun shouldAddFluidItemsInventory(): Boolean {
        return false
    }

    //#endregion
    //#region gui

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = super.getGuiContainerPieces(container)

        list.add(BasicRenderedGuiPiece(56, 25, 64, 54,
                Textures.FARM_TEXTURES.resource, 65, 1))

        return list
    }

    //#endregion

    private var delay = 0

    override fun innerUpdate() {
        if (this.getWorld().isRemote) {
            return
        }

        if (--this.delay > 0) {
            return
        }

        var transferred = 0

        //#region process inputs

        val i_stack = this.inputItems.getStackInSlot(0)
        val capacity = this.xpTank.capacity - this.xpTank.fluidAmount
        if (!i_stack.isEmpty && (capacity > 0)) {
            if (i_stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                val initial = this.xpTank.fluidAmount
                val result = FluidUtil.tryEmptyContainer(i_stack, this.fluidHandler, Fluid.BUCKET_VOLUME, null, true)
                if (result.isSuccess && !ItemStack.areItemStacksEqual(result.getResult(), i_stack)) {
                    val r_stack = result.getResult()
                    this.inputItems.setStackInSlot(0, r_stack)
                    if (!r_stack.isEmpty && this.isEmptyFluidContainer(r_stack)) {
                        this.inputItems.setStackInSlot(0, this.inputItems.insertItem(1, r_stack, false))
                    }

                    transferred += this.xpTank.fluidAmount - initial
                }
            }
        }

        //#endregion

        //#region process outputs

        val o_stack = this.outputItems.getStackInSlot(0)
        val maxDrain = this.xpTank.fluidAmount
        if (!o_stack.isEmpty && (maxDrain > 0)) {
            if (o_stack.item === Items.GLASS_BOTTLE) {
                //#region glass bottle -> experience bottle

                if (maxDrain >= 15) {
                    val existing = this.outputItems.getStackInSlot(1)
                    var result = ItemStack.EMPTY
                    if (existing.isEmpty) {
                        result = ItemStack(Items.EXPERIENCE_BOTTLE, 1)
                    } else if (existing.count < existing.maxStackSize) {
                        result = ItemStackUtil.copyWithSize(existing, ItemStackUtil.getSize(existing) + 1)
                    }

                    if (!result.isEmpty) {
                        this.outputItems.setStackInSlot(1, result)
                        this.xpTank.drain(15, true)

                        i_stack.shrink(1)
                        if (i_stack.count == 0) {
                            this.outputItems.setStackInSlot(0, ItemStack.EMPTY)
                        }

                        transferred += 15
                    }
                }

                //#endregion
            } else if (o_stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
                    && this.outputItems.getStackInSlot(1).isEmpty) {
                val toFill = Math.max(maxDrain, Fluid.BUCKET_VOLUME)
                val initial = this.xpTank.fluidAmount
                val bucket = ItemStackUtil.copyWithSize(o_stack, 1)
                val result = FluidUtil.tryFillContainer(bucket, this.fluidHandler, toFill, null, true)
                if (result.isSuccess) {
                    // stack = result.getResult()
                    // this.outputItems.insertItem(1, stack, false)
                    this.outputItems.setStackInSlot(1, result.getResult())
                    this.outputItems.getStackInSlot(0).shrink(1)

                    transferred += initial - this.xpTank.fluidAmount
                }
            }
        }

        //#endregion

        if (transferred > 0) {
            this.delay = Math.max(5, transferred / 50)
            this.forceSync()
        } else if (this.delay < 0) {
            this.delay = 0
        }
    }

    private fun isEmptyFluidContainer(stack: ItemStack): Boolean {
        val fluid = FluidUtil.getFluidContained(stack)
        return fluid == null || fluid.amount == 0
    }

    val fillPercent: Float
        get() = this.xpTank.fluidAmount.toFloat() / this.xpTank.capacity.toFloat()
}
