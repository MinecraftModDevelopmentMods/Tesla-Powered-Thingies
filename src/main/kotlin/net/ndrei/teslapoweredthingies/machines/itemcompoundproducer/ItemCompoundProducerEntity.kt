package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import net.ndrei.teslacorelib.tileentities.ElectricMachine

/**
 * Created by CF on 2017-07-13.
 */
class ItemCompoundProducerEntity
    : ElectricMachine(ItemCompoundProducerEntity::class.java.name.hashCode()) {

    //#region inventory and gui methods

    override fun initializeInventories() {
        super.initializeInventories()
    }

    //#endregion

    override fun performWork(): Float {
        return 0.0f
    }
}