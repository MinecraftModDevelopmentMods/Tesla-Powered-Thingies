package net.ndrei.teslapoweredthingies.items

import net.ndrei.teslacorelib.items.BaseAddon
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.IAnimalAgeFilterAcceptor
import net.ndrei.teslapoweredthingies.common.IAnimalEntityFilter

/**
 * Created by CF on 2017-07-07.
 */
abstract class BaseAnimalFilterItem(registryName: String)
    : BaseAddon(TeslaThingiesMod.MODID, TeslaThingiesMod.creativeTab, registryName), IAnimalEntityFilter {

    override fun canBeAddedTo(machine: SidedTileEntity)
            = (machine is IAnimalAgeFilterAcceptor) && machine.acceptsFilter(this)
}
