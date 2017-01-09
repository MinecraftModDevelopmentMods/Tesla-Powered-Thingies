package net.ndrei.teslapoweredthingies.common;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.ndrei.teslapoweredthingies.items.BaseThingyItem;

/**
 * Created by CF on 2017-01-06.
 */
public class ItemsRegistry {
    public static BaseThingyItem ASH;

    static void registerItems() {
        GameRegistry.register(ItemsRegistry.ASH = new BaseThingyItem("ash"));
    }
}
