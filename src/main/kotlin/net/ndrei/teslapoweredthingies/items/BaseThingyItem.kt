package net.ndrei.teslapoweredthingies.items

import net.ndrei.teslacorelib.items.RegisteredItem
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-06-30.
 */
open class BaseThingyItem(registryName: String)
    : RegisteredItem(TeslaThingiesMod.MODID, TeslaThingiesMod.creativeTab, registryName)
