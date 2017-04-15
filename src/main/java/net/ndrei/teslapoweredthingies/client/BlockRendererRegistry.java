package net.ndrei.teslapoweredthingies.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ndrei.teslapoweredthingies.common.BlocksRegistry;

/**
 * Created by CF on 2017-01-06.
 */
public class BlockRendererRegistry {
    @SideOnly(Side.CLIENT)
    static void registerBlockRenderers() {
        BlocksRegistry.incinerator.registerRenderer();
        BlocksRegistry.fluidBurner.registerRenderer();
        BlocksRegistry.fluidSolidifier.registerRenderer();
        BlocksRegistry.itemLiquefierBlock.registerRenderer();
    }
}
