package net.ndrei.teslapoweredthingies.machines.powdermaker

import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.LockableItemHandler
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
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
                = (!stack.isEmpty && PowderMakerRecipes.hasRecipe(stack))
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
    }

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = super.getGuiContainerPieces(container)

        list.add(BasicRenderedGuiPiece(102, 24, 22, 56,
                TeslaThingiesMod.MACHINES_TEXTURES, 119, 35))

        list.add(ItemStackPiece(104, 41, 22, 22, this))

        return list
    }

    //#endregion

    override fun performWork(): Float {
        return 0.0f
    }

    override val workItem: ItemStack
        get() = ItemStack.EMPTY
}
