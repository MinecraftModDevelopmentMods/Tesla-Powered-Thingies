package net.ndrei.teslapoweredthingies.common

import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine

/**
 * Created by CF on 2017-07-07.
 */
interface IAdditionalProcessingAddon {
    fun processAddon(machine: ElectricFarmMachine, availableProcessing: Float): Float
}
