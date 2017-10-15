package net.ndrei.teslapoweredthingies.machines.poweredkiln

import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.FilteredSlot
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.LockableItemHandler
import net.ndrei.teslacorelib.inventory.SyncItemHandler
import net.ndrei.teslapoweredthingies.client.ThingiesTexture
import net.ndrei.teslapoweredthingies.common.gui.IWorkItemProvider
import net.ndrei.teslapoweredthingies.common.gui.ItemStackPiece
import net.ndrei.teslapoweredthingies.machines.BaseThingyMachine

/**
 * Created by CF on 2017-07-06.
 */
class PoweredKilnEntity
    : BaseThingyMachine(PoweredKilnEntity::class.java.name.hashCode()) {

    private lateinit var inputs: ItemStackHandler
    private lateinit var outputs: IItemHandler
    private lateinit var currentItems: IItemHandlerModifiable

    //#region Inventory and GUI stuff

    override fun initializeInventories() {
        super.initializeInventories()

        this.inputs = LockableItemHandler(3)
        super.addInventory(object : ColoredItemHandler(this.inputs, EnumDyeColor.GREEN, "Input Items", BoundingRectangle(57, 25, 62, 18)) {
            override fun canExtractItem(slot: Int) = false

            override fun canInsertItem(slot: Int, stack: ItemStack)
                = super.canInsertItem(slot, stack) && !stack.isEmpty && PoweredKilnRegistry.hasRecipe(stack)

            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                val result = mutableListOf<Slot>()

                val box = this.boundingBox
                if (!box.isEmpty) {
                    val columns = box.width / 18
                    (0 until this.innerHandler.slots).mapTo(result) {
                        FilteredSlot(this.itemHandlerForContainer, it,
                                box.left + 1 + (it % columns) * (18 + 4),
                                box.top  + 1
                        )
                    }
                }

                return result
            }
        })
        super.addInventoryToStorage(this.inputs, "inv_inputs")

        this.outputs = this.addSimpleInventory(6, "inv_outputs", EnumDyeColor.PURPLE, "Output Items",
            BoundingRectangle.slots(133, 25, 2, 3),
            { _, _ -> false})

        this.currentItems = SyncItemHandler(3)
        super.addInventoryToStorage(this.currentItems as SyncItemHandler, "inv_current")
    }

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = super.getGuiContainerPieces(container)

        list.add(TiledRenderedGuiPiece(55, 44, 22, 22, 3, 1,
                ThingiesTexture.MACHINES_TEXTURES.resource, 119, 4, null))

        list.add(ItemStackPiece(55, 44, 22, 22, object: IWorkItemProvider {
            override val workItem: ItemStack
                get() = this@PoweredKilnEntity.currentItems.getStackInSlot(0)
        }))
        list.add(ItemStackPiece(77, 44, 22, 22,  object: IWorkItemProvider {
            override val workItem: ItemStack
                get() = this@PoweredKilnEntity.currentItems.getStackInSlot(1)
        }))
        list.add(ItemStackPiece(99, 44, 22, 22,  object: IWorkItemProvider {
            override val workItem: ItemStack
                get() = this@PoweredKilnEntity.currentItems.getStackInSlot(2)
        }))

        list.add(FurnaceBurnPiece(59, 67, { !this.currentItems.getStackInSlot(0).isEmpty }))
        list.add(FurnaceBurnPiece(81, 67, { !this.currentItems.getStackInSlot(1).isEmpty }))
        list.add(FurnaceBurnPiece(103, 67, { !this.currentItems.getStackInSlot(2).isEmpty }))

        return list
    }

    //#endregion

    override fun processImmediateInventories() {
        super.processImmediateInventories()

        for (slot in 0..(this.currentItems.slots - 1)) {
            if (this.currentItems.getStackInSlot(slot).isEmpty) {
                for (input in 0..(this.inputs.slots - 1)) {
                    val stack = this.inputs.getStackInSlot(input)
                    if (!stack.isEmpty) {
                        val recipe = PoweredKilnRegistry.findRecipe(stack) ?: continue
                        this.currentItems.setStackInSlot(slot,
                                this.inputs.extractItem(input, recipe.input.count, false))
                        if (!this.currentItems.getStackInSlot(slot).isEmpty) {
                            break
                        }
                    }
                }
            }
        }
    }

    override fun performWork(): Float {
        var result = 0.0f

        for (slot in 0..(this.currentItems.slots - 1)) {
            val stack = this.currentItems.getStackInSlot(slot)
            if (!stack.isEmpty) {
                val recipe = PoweredKilnRegistry.findRecipe(stack) ?: continue
                if (ItemStackUtil.insertItems(this.outputs, recipe.output.copy(), true).isEmpty) {
                    ItemStackUtil.insertItems(this.outputs, recipe.output.copy(), false)
                    this.currentItems.setStackInSlot(slot, ItemStack.EMPTY)
                    result = 1.0f
                }
            }
        }

        return result
    }
}
