package net.ndrei.teslapoweredthingies.items

import net.minecraft.item.ItemStack
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.items.BaseAddon
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslapoweredthingies.MOD_ID
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.machines.liquidxpstorage.LiquidXPStorageEntity

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterItem
object XPTankAddonItem : BaseAddon(MOD_ID, TeslaThingiesMod.creativeTab, "xp_tank_addon") {
    override fun canBeAddedTo(machine: SidedTileEntity) = machine is LiquidXPStorageEntity

    override fun onAdded(addon: ItemStack, machine: SidedTileEntity) {
        super.onAdded(addon, machine)

        BasicTeslaGuiContainer.refreshParts(machine.world)
    }

    override fun onRemoved(addon: ItemStack, machine: SidedTileEntity) {
        super.onRemoved(addon, machine)

        BasicTeslaGuiContainer.refreshParts(machine.world)
    }
}