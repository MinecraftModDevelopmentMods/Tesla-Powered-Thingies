package net.ndrei.teslapoweredthingies.machines.poweredkiln

import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.FilteredSlot
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.LockableItemHandler
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.machines.BaseThingyMachine

/**
 * Created by CF on 2017-07-06.
 */
class PoweredKilnEntity
    : BaseThingyMachine(PoweredKilnEntity::class.java.name.hashCode()) {

    private lateinit var inputs: LockableItemHandler
    private lateinit var outputs: ItemStackHandler

    //#region Inventory and GUI stuff

    override fun initializeInventories() {
        super.initializeInventories()

        this.inputs = object : LockableItemHandler(3) {
            override fun onContentsChanged(slot: Int) {
                this@PoweredKilnEntity.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.inputs, EnumDyeColor.GREEN, "Input Items", BoundingRectangle(59, 25, 62, 18)) {
            override fun canExtractItem(slot: Int) = false

            override fun canInsertItem(slot: Int, stack: ItemStack)
                = super.canInsertItem(slot, stack) && !stack.isEmpty && PoweredKilnRecipes.hasRecipe(stack)

            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                val result = mutableListOf<Slot>()

                val box = this.boundingBox
                if (!box.isEmpty) {
                    val columns = box.width / 18
                    (0..this.innerHandler.slots - 1).mapTo(result) {
                        FilteredSlot(this.itemHandlerForContainer, it,
                                box.left + 1 + (it % columns) * (18 + 4),
                                box.top  + 1 + (it / columns) * 18
                        )
                    }
                }

                return result
            }
        })
        super.addInventoryToStorage(this.inputs, "inv_inputs")

        this.outputs = object : ItemStackHandler(6) {
            override fun onContentsChanged(slot: Int) {
                this@PoweredKilnEntity.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.outputs, EnumDyeColor.PURPLE, "Output Items", BoundingRectangle(133, 25, 36, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack) = false
        })
        super.addInventoryToStorage(this.outputs, "inv_outputs")
    }

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = super.getGuiContainerPieces(container)

        list.add(TiledRenderedGuiPiece(57, 44, 22, 22, 3, 1,
                Textures.MACHINES_TEXTURES.resource, 119, 4, null))

        // TODO: add processing item stacks
        // list.add(ItemStackPiece(104, 41, 22, 22, this))

        return list
    }

    //#endregion

    override fun performWork(): Float {
        return 0.0f
    }
}
