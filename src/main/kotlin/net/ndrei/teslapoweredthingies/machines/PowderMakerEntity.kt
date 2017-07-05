package net.ndrei.teslapoweredthingies.machines

import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.LockableItemHandler
import net.ndrei.teslacorelib.tileentities.ElectricMachine

/**
 * Created by CF on 2017-07-04.
 */
class PowderMakerEntity
    : ElectricMachine(PowderMakerEntity::class.java.name.hashCode()) {

    private lateinit var inputs: LockableItemHandler
    private lateinit var outputs: ItemStackHandler

    //#region inventories

    override fun initializeInventories() {
        super.initializeInventories()


        this.inputs = object : LockableItemHandler(3) {
            override fun onContentsChanged(slot: Int) {
                this@PowderMakerEntity.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.inputs, EnumDyeColor.GREEN, "Input Items", BoundingRectangle(79, 25, 18, 54)) {
            override fun canExtractItem(slot: Int): Boolean {
                return false
            }

            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                if (stack.isEmpty) {
                    return false
                }

                return true
            }
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

    //#endregion
    override fun performWork(): Float {
        return 0.0f
    }
}
