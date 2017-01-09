package net.ndrei.teslapoweredthingies.items;

import net.ndrei.teslacorelib.items.RegisteredItem;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;

/**
 * Created by CF on 2017-01-07.
 */
public class BaseThingyItem extends RegisteredItem {
    public BaseThingyItem(String registryName) {
        super(TeslaThingiesMod.MODID, TeslaThingiesMod.creativeTab, registryName);
    }
}
