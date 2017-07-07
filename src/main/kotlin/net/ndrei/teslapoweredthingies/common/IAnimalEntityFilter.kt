package net.ndrei.teslapoweredthingies.common

import net.minecraft.entity.passive.EntityAnimal
import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine

/**
 * Created by CF on 2017-07-07.
 */
interface IAnimalEntityFilter{
    fun canProcess(machine: ElectricFarmMachine, entityIndex: Int, entity: EntityAnimal): Boolean
}