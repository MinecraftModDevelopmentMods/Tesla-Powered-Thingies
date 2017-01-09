package net.ndrei.teslapoweredthingies.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ndrei.teslapoweredthingies.common.ItemsRegistry;

/**
 * Created by CF on 2017-01-06.
 */
public class ItemRenderersRegistry  {
    @SideOnly(Side.CLIENT)
    static void registerItemRenderers() {
        ItemsRegistry.ASH.registerRenderer();
    }
}
