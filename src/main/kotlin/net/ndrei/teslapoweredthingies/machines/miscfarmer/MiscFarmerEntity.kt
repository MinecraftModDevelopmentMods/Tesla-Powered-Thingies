package net.ndrei.teslapoweredthingies.machines.miscfarmer

import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslapoweredthingies.common.IAdditionalProcessingAddon
import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine

class MiscFarmerEntity
    : ElectricFarmMachine(MiscFarmerEntity::javaClass.name.hashCode()) {

    //#region inventory management

    override val inputSlots: Int get() = 3
    override val inputInventoryColumns: Int get() = 1

    override fun getInputInventoryBounds(columns: Int, rows: Int) =
        BoundingRectangle(52, 25, 18 * columns, 18 * rows)

    override val outputSlots: Int get() = 12
    override val outputInventoryColumns: Int get() = 4

    override fun getOutputInventoryBounds(columns: Int, rows: Int) =
        BoundingRectangle(79, 25, 18 * columns, 18 * rows)

    override val supportedAddonColumns: Int get() = 2

    //#endregion

    override fun performWork(): Float {
        var result = 0.0f

        for (addon in this.addons) {
            if (addon is IAdditionalProcessingAddon) {
                val available = 1.0f - result
                result += Math.min(addon.processAddon(this, available), available)
            }
        }

        return result
    }
}