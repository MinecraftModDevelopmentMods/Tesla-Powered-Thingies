package net.ndrei.teslapoweredthingies.machines

import net.minecraft.init.Items
import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.*
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.FilteredSlot
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.FluidTankPiece
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.FluidStorage
import net.ndrei.teslacorelib.tileentities.ElectricMachine
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.gui.IWorkItemProvider
import net.ndrei.teslapoweredthingies.gui.ItemStackPiece
import net.ndrei.teslapoweredthingies.machines.itemliquefier.LiquefierRecipe
import net.ndrei.teslapoweredthingies.machines.itemliquefier.LiquefierRecipes

/**
 * Created by CF on 2017-06-30.
 */
class ItemLiquefierEntity : BaseThingyMachine(ItemLiquefierEntity::class.java.name.hashCode()), IWorkItemProvider {
    private var lavaTank: IFluidTank? = null
    private var inputs: ItemStackHandler? = null
    private var fluidOutputs: ItemStackHandler? = null

    private var currentRecipe: LiquefierRecipe? = null

    //region Inventory and GUI stuff

    override fun initializeInventories() {
        super.initializeInventories()

        super.ensureFluidItems()
        this.lavaTank = super.addFluidTank(FluidRegistry.LAVA, 5000, EnumDyeColor.RED, "Lava Tank",
                BoundingRectangle(133, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT))

        this.inputs = object : ItemStackHandler(3) {
            override fun onContentsChanged(slot: Int) {
                this@ItemLiquefierEntity.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.inputs!!, EnumDyeColor.GREEN, "Input Items", BoundingRectangle(61, 25, 18, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return !ItemStackUtil.isEmpty(stack) && LiquefierRecipes.getRecipe(stack.item) != null
            }

            override fun canExtractItem(slot: Int): Boolean {
                return false
            }
//
//            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
//                val slots = super.getSlots(container)
//
//                val box = this.boundingBox
//                for (y in 0..2) {
//                    slots.add(FilteredSlot(this.itemHandlerForContainer, y,
//                            box.left + 1, box.top + 1 + y * 18))
//                }
//
//                return slots
//            }
//
//            override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
//                val pieces = super.getGuiContainerPieces(container)
//
//                val box = this.boundingBox
//                pieces.add(TiledRenderedGuiPiece(box.left, box.top, 18, 18,
//                        1, 3,
//                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.PURPLE))
//
//                return pieces
//            }
        })
        super.addInventoryToStorage(this.inputs!!, "inv_inputs")

        val box = BoundingRectangle(151, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)
        this.fluidOutputs = object : ItemStackHandler(2) {
            override fun getSlotLimit(slot: Int): Int {
                return if (slot == 0) 1 else super.getSlotLimit(slot)
            }

            override fun onContentsChanged(slot: Int) {
                this@ItemLiquefierEntity.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.fluidOutputs!!, EnumDyeColor.SILVER, "Fluid Output", box) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return slot == 0 && this@ItemLiquefierEntity.isValidFluidContainer(stack)
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
                pieces.add(BasicRenderedGuiPiece(box.left, box.top, box.width, box.height,
                        TeslaThingiesMod.MACHINES_TEXTURES, 98, 36))

                return pieces
            }
        })
        super.addInventoryToStorage(this.fluidOutputs!!, "inv_fluid_outputs")
    }

    private fun isValidFluidContainer(stack: ItemStack): Boolean {
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
                                if (content == null || content.amount < tank.capacity && content.fluid === FluidRegistry.LAVA) {
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

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val pieces = super.getGuiContainerPieces(container)

        pieces.add(BasicRenderedGuiPiece(79, 41, 54, 22,
                TeslaThingiesMod.MACHINES_TEXTURES, 24, 4))

        pieces.add(BasicRenderedGuiPiece(99, 64, 14, 14,
                TeslaThingiesMod.MACHINES_TEXTURES, 44, 27))

        pieces.add(ItemStackPiece(96, 42, 20, 20, this))

        return pieces
    }

    //endregion

    //region serialization

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        if (compound.hasKey("currentRecipe")) {
            this.currentRecipe = LiquefierRecipe.deserializeNBT(compound.getCompoundTag("currentRecipe"))
        } else {
            this.currentRecipe = null
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        var compound = compound
        compound = super.writeToNBT(compound)

        if (this.currentRecipe != null) {
            compound.setTag("currentRecipe", this.currentRecipe!!.serializeNBT())
        }

        return compound
    }

    //endregion

    override val workItem: ItemStack
        get() {
            if (this.currentRecipe != null) {
                return ItemStack(this.currentRecipe!!.input, this.currentRecipe!!.inputStackSize)
            }
            return ItemStackUtil.emptyStack
        }

    override val energyForWork: Int
        get() = 6000

    override fun performWork(): Float {
        var result = 0.0f
        if (this.currentRecipe != null) {
            val fluid = FluidStack(this.currentRecipe!!.output, this.currentRecipe!!.outputQuantity)
            if (this.lavaTank!!.fill(fluid, false) == this.currentRecipe!!.outputQuantity) {
                this.lavaTank!!.fill(fluid, true)
                this.currentRecipe = null
                result = 1.0f
            }
        }

        return result
    }

    override fun processImmediateInventories() {
        super.processImmediateInventories()

        var stack = this.fluidOutputs!!.getStackInSlot(0)
        val maxDrain = this.lavaTank!!.fluidAmount
        if (!ItemStackUtil.isEmpty(stack) && maxDrain > 0) {
            if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                val toFill = Math.max(maxDrain, Fluid.BUCKET_VOLUME)
                val dummy = FluidStorage()
                dummy.addTank(this.lavaTank!!)
                val result = FluidUtil.tryFillContainer(stack, dummy, toFill, null, true)
                if (result.isSuccess) {
                    stack = result.getResult()
                    this.fluidOutputs!!.setStackInSlot(0, stack)
                }

                if (!ItemStackUtil.isEmpty(stack) && !this.isEmptyFluidContainer(stack)) {
                    this.fluidOutputs!!.setStackInSlot(0, this.fluidOutputs!!.insertItem(1, stack, false))
                }
            }
        }

        if (this.currentRecipe == null) {
            for (input in ItemStackUtil.getCombinedInventory(this.inputs!!)) {
                val recipe = LiquefierRecipes.getRecipe(input.item)
                if (recipe != null && recipe.inputStackSize <= ItemStackUtil.getSize(input)) {
                    val fluid = FluidStack(recipe.output, recipe.outputQuantity)
                    if (this.lavaTank!!.fill(fluid, false) == recipe.outputQuantity) {
                        ItemStackUtil.extractFromCombinedInventory(this.inputs!!, input, recipe.inputStackSize)
                        this.currentRecipe = recipe
                        break
                    }
                }
            }
        }
    }

    private fun isEmptyFluidContainer(stack: ItemStack): Boolean {
        val fluid = FluidUtil.getFluidContained(stack)
        return fluid == null || fluid.amount == 0
    }
}
