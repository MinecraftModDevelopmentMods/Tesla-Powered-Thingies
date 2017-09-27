package net.ndrei.teslapoweredthingies.items

import net.ndrei.bushmaster.api.BushMasterApi
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.items.BaseAddon
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.IAdditionalProcessingAddon
import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine
import net.ndrei.teslapoweredthingies.machines.miscfarmer.MiscFarmerEntity

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterItem("mod-exists:bushmastercore")
object BushMasterAddon
    : BaseAddon(TeslaThingiesMod.MODID, TeslaThingiesMod.creativeTab, "bush_master_addon")
        , IAdditionalProcessingAddon {
    const val PICK_ENERGY = .033f

    override fun canBeAddedTo(machine: SidedTileEntity)
        = machine is MiscFarmerEntity

    override fun processAddon(machine: ElectricFarmMachine, availableProcessing: Float): Float {
        var energyUsed = 0.0f
        if (availableProcessing >= PICK_ENERGY) {
            for (pos in machine.getWorkArea()) {
                val harvestable = BushMasterApi.harvestableFactory.getHarvestable(machine.world, pos)
                if (harvestable != null) {
                    val state = machine.world.getBlockState(pos)
                    if (harvestable.canBeHarvested(machine.world, pos, state)) {
                        val loot = harvestable.harvest(machine.world, pos, state, true)
                        machine.outputItems(loot)
                        energyUsed += PICK_ENERGY
                        if ((availableProcessing - energyUsed) < PICK_ENERGY) {
                            break
                        }
                    }
                }
            }
        }
        return energyUsed
    }
}
