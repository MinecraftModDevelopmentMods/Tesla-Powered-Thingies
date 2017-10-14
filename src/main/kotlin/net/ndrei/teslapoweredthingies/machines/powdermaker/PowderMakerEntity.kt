package net.ndrei.teslapoweredthingies.machines.powdermaker

import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.LockableItemHandler
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.gui.IWorkItemProvider
import net.ndrei.teslapoweredthingies.gui.ItemStackPiece
import net.ndrei.teslapoweredthingies.machines.BaseThingyMachine

/**
 * Created by CF on 2017-07-04.
 */
class PowderMakerEntity
    : BaseThingyMachine(PowderMakerEntity::class.java.name.hashCode()), IWorkItemProvider {

    private lateinit var inputs: LockableItemHandler
    private lateinit var outputs: ItemStackHandler
    private lateinit var currentItem: ItemStackHandler

    //#region Inventory and GUI stuff

    override fun initializeInventories() {
        super.initializeInventories()

        this.inputs = object : LockableItemHandler(3) {
            override fun onContentsChanged(slot: Int) {
                this@PowderMakerEntity.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.inputs, EnumDyeColor.GREEN, "Input Items", BoundingRectangle(79, 25, 18, 54)) {
            override fun canExtractItem(slot: Int) = false

            override fun canInsertItem(slot: Int, stack: ItemStack)
                = (!stack.isEmpty && PowderMakerRegistry.hasRecipe(stack))
        })
        super.addInventoryToStorage(this.inputs, "inv_inputs")

        this.outputs = object : ItemStackHandler(6) {
            override fun onContentsChanged(slot: Int) {
                this@PowderMakerEntity.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.outputs, EnumDyeColor.PURPLE, "Output Items", BoundingRectangle(133, 25, 36, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return false
            }
        })
        super.addInventoryToStorage(this.outputs, "inv_outputs")

        this.currentItem = object : ItemStackHandler(1) {
            override fun onContentsChanged(slot: Int) {
                this@PowderMakerEntity.markDirty()
            }
        }
        super.addInventoryToStorage(this.currentItem, "inv_current")
    }

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = super.getGuiContainerPieces(container)

        list.add(BasicRenderedGuiPiece(104, 24, 22, 56,
                Textures.MACHINES_TEXTURES.resource, 119, 35))

        list.add(ItemStackPiece(104, 41, 22, 22, this))

        return list
    }

    override val workItem: ItemStack
        get() = this.currentItem.getStackInSlot(0)

    //#endregion

    override fun processImmediateInventories() {
        super.processImmediateInventories()

        if (this.currentItem.getStackInSlot(0).isEmpty) {
            for(slot in 0..(this.inputs.slots-1)) {
                val stack = this.inputs.getStackInSlot(slot)
                if (!stack.isEmpty) {
                    val recipe = PowderMakerRegistry.findRecipe(stack) ?: continue
                    this.currentItem.setStackInSlot(0,
                            this.inputs.extractItem(slot, recipe.getInputCount(stack), false))
                    if (!this.currentItem.getStackInSlot(0).isEmpty) {
                        break
                    }
                }
            }
        }
    }

    override fun performWork(): Float {
        val stack = this.currentItem.getStackInSlot(0)
        if (!stack.isEmpty) {
            val recipe = PowderMakerRegistry.findRecipe(stack)
            val result = recipe?.process(stack)
            if (result != null) {
                // see if we can output all primaries
                if (result.primary.all {
                    ItemStackUtil.insertItems(this.outputs, it, true).isEmpty
                }) {
                    // yup... we can insert all primaries, don't care about secondaries
                    result.primary.forEach {
                        ItemStackUtil.insertItems(this.outputs, it, false)
                    }
                    result.secondary.forEach {
                        ItemStackUtil.insertItems(this.outputs, it, false)
                    }
                    this.currentItem.setStackInSlot(0, result.remaining)
                    return 1.0f
                }
            }
        }
        return 0.0f
    }
}
