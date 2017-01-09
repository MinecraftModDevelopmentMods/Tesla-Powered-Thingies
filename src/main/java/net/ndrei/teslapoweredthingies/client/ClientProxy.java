package net.ndrei.teslapoweredthingies.client;

import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;
import net.ndrei.teslapoweredthingies.common.CommonProxy;

/**
 * Created by CF on 2017-01-06.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        TeslaThingiesMod.logger.info("ClientProxy::preInit");

        OBJLoader.INSTANCE.addDomain(TeslaThingiesMod.MODID);

        // Typically initialization of models and such goes here:
        BlockRendererRegistry.registerBlockRenderers();
        ItemRenderersRegistry.registerItemRenderers();
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }
}
