package net.ndrei.teslapoweredthingies.machines

import net.ndrei.teslapoweredthingies.common.BlockCube
import net.ndrei.teslapoweredthingies.common.BlockPosUtils
import net.ndrei.teslapoweredthingies.common.GuiPieceSide
import net.ndrei.teslapoweredthingies.items.MachineRangeAddonTier1
import net.ndrei.teslapoweredthingies.items.MachineRangeAddonTier2

/**
 * Created by CF on 2017-07-06.
 */
abstract class ElectricFarmMachine protected constructor(typeId: Int) : BaseThingyMachine(typeId) {
    protected var inStackHandler: ItemStackHandler? = null
    protected var filteredInStackHandler: ColoredItemHandler? = null
    protected var outStackHandler: ItemStackHandler? = null

    //#region inventories & gui methods

    override fun initializeInventories() {
        super.initializeInventories()

        this.initializeInputInventory()
        this.initializeOutputInventory()
    }

    protected open val inputSlots: Int
        get() = 3

    protected fun initializeInputInventory() {
        val inputSlots = this.inputSlots
        if (inputSlots > 0) {
            this.inStackHandler = if (this.lockableInputInventory)
                object : LockableItemHandler(Math.max(0, Math.min(3, inputSlots))) {
                    override fun onContentsChanged(slot: Int) {
                        this@ElectricFarmMachine.markDirty()
                    }
                }
            else
                object : ItemStackHandler(Math.max(0, Math.min(3, inputSlots))) {
                    override fun onContentsChanged(slot: Int) {
                        this@ElectricFarmMachine.markDirty()
                    }
                }
            this.filteredInStackHandler = object : ColoredItemHandler(this.inStackHandler!!, EnumDyeColor.GREEN, "Input Items", this.getInputInventoryBounds(this.inStackHandler!!.slots, 1)) {
                override fun canInsertItem(slot: Int, stack: ItemStack)
                    = (if (this.innerHandler is LockableItemHandler) this.innerHandler.canInsertItem(slot, stack) else true)
                        && this@ElectricFarmMachine.acceptsInputStack(slot, stack)

                override fun canExtractItem(slot: Int) = false
            }
            super.addInventory(this.filteredInStackHandler)
            super.addInventoryToStorage(this.inStackHandler!!, "inputs")
        } else {
            this.inStackHandler = null
        }
    }

    protected open val lockableInputInventory: Boolean
        get() = true

    protected open val lockableInputLockPosition: GuiPieceSide
        get() = GuiPieceSide.NONE

    protected open fun getInputInventoryBounds(columns: Int, rows: Int)
        = BoundingRectangle(115 + (3 - columns) * 9, 25, 18 * columns, 18 * rows)

    protected open fun acceptsInputStack(slot: Int, stack: ItemStack) = true

    protected open val outputSlots: Int
        get() = 6

    protected fun initializeOutputInventory() {
        val outputSlots = this.outputSlots
        if (outputSlots > 0) {
            this.outStackHandler = object : ItemStackHandler(Math.max(0, Math.min(6, outputSlots))) {
                override fun onContentsChanged(slot: Int) {
                    this@ElectricFarmMachine.markDirty()
                }
            }
            val columns = Math.min(3, this.outStackHandler!!.slots)
            val rows = Math.min(2, this.outStackHandler!!.slots / columns)
            super.addInventory(object : ColoredItemHandler(this.outStackHandler!!, EnumDyeColor.PURPLE, "Output Items", this.getOutputInventoryBounds(columns, rows)) {
                override fun canInsertItem(slot: Int, stack: ItemStack) =  false

                override fun canExtractItem(slot: Int) = true

//                override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
//                    val slots = super.getSlots(container)
//
//                    val box = this.boundingBox
//                    for (x in 0..2) {
//                        for (y in 0..1) {
//                            val i = y * 3 + x
//                            if (i >= this.handler.getSlots()) {
//                                continue
//                            }
//
//                            slots.add(FilteredSlot(this.itemHandlerForContainer, i,
//                                    box.left + 1 + x * 18, box.top + 1 + y * 18))
//                        }
//                    }
//
//                    return slots
//                }

//                override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
//                    val pieces = super.getGuiContainerPieces(container)
//
//                    val box = this.boundingBox
//                    pieces.add(TiledRenderedGuiPiece(box.left, box.top, 18, 18,
//                            Math.min(3, this.handler.getSlots()),
//                            Math.min(2, this.handler.getSlots() / Math.min(3, this.handler.getSlots())),
//                            BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.PURPLE))
//
//                    return pieces
//                }
            })
            super.addInventoryToStorage(this.outStackHandler!!, "outputs")
        } else {
            this.outStackHandler = null
        }
    }

    protected open fun getOutputInventoryBounds(columns: Int, rows: Int)
        = BoundingRectangle(115, 43, 18 * columns, 18 * rows)

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = super.getGuiContainerPieces(container)

        if (this.lockableInputInventory && (this.lockableInputLockPosition != GuiPieceSide.NONE)
                && (this.filteredInStackHandler != null)) {
            val box = this.filteredInStackHandler!!.boundingBox
            if (!box.isEmpty)
            list.add(LockedInventoryTogglePiece(
                    when (this.lockableInputLockPosition) {
                        GuiPieceSide.LEFT -> box.left - 16
                        else -> box.right + 2 // assume RIGHT
                    }, box.top + 2, this, this.filteredInStackHandler!!.color)
            )
        }

        return list
    }

    //#endregion
    //#region write/read/sync   methods

//    override fun readFromNBT(compound: NBTTagCompound) {
//        super.readFromNBT(compound)
//        if (compound.hasKey("income")) {
//            this.inStackHandler!!.deserializeNBT(compound.getCompoundTag("income"))
//        }
//        if (compound.hasKey("outcome")) {
//            this.outStackHandler!!.deserializeNBT(compound.getCompoundTag("outcome"))
//        }
//    }
//
//    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
//        var compound = compound
//        compound = super.writeToNBT(compound)
//
//        if (this.inStackHandler != null) {
//            compound.setTag("income", this.inStackHandler!!.serializeNBT())
//        }
//        if (this.outStackHandler != null) {
//            compound.setTag("outcome", this.outStackHandler!!.serializeNBT())
//        }
//
//        return compound
//    }

    //#endregion

    open fun supportsRangeAddons() = true

    protected val range: Int
        get() = this.getRange(3, 3)

    protected fun getRange(base: Int, perTier: Int): Int {
        val tier1 = if (this.hasAddon(MachineRangeAddonTier1::class.java)) 1 else 0
        val tier2 = tier1 * if (this.hasAddon(MachineRangeAddonTier2::class.java)) 1 else 0

        return base + (tier1 + tier2) * perTier
    }

    protected fun getWorkArea(facing: EnumFacing, height: Int): BlockCube {
        return BlockPosUtils.getCube(this.getPos(), facing, this.range, height)
    }

    val groundArea: BlockCube
        get() = this.getWorkArea(this.facing.opposite, 1)

    protected fun spawnOverloadedItem(stack: ItemStack): Boolean {
        // TODO: readd config option for this
        // if (MekfarmMod.config.allowMachinesToSpawnItems()) {
            return null != super.spawnItemFromFrontSide(stack)
        // }
        // return false
    }

    fun outputItems(loot: ItemStack)
        = loot.isEmpty || this.outputItems(listOf(loot))

    fun outputItems(loot: List<ItemStack>): Boolean {
        if (loot.isNotEmpty()) {
            for (lootStack in loot) {
                var remaining = if (this.filteredInStackHandler == null)
                    ItemStackUtil.insertItemInExistingStacks(this.inStackHandler, lootStack, false)
                else
                    ItemHandlerHelper.insertItemStacked(this.filteredInStackHandler, lootStack, false)
                if (!ItemStackUtil.isEmpty(remaining)) {
                    remaining = ItemHandlerHelper.insertItem(this.outStackHandler, lootStack, false)
                }
                if (!ItemStackUtil.isEmpty(remaining)) {
                    return this.spawnOverloadedItem(remaining)
                }
            }
        }
        return true
    }
}
