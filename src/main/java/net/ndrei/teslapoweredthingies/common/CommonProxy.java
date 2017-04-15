package net.ndrei.teslapoweredthingies.common;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerRecipes;
import net.ndrei.teslapoweredthingies.machines.incinerator.IncineratorRecipes;
import net.ndrei.teslapoweredthingies.machines.itemliquefier.LiquefierRecipes;

/**
 * Created by CF on 2017-01-06.
 */
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {
        TeslaThingiesMod.logger.info("CommonProxy::preInit");

        ItemsRegistry.registerItems();
        BlocksRegistry.registerBlocks();
    }

    public void init(FMLInitializationEvent e) {
        TeslaThingiesMod.logger.info("CommonProxy::init");
    }

    public void postInit(FMLPostInitializationEvent e) {
        TeslaThingiesMod.logger.info("CommonProxy::postInit");

        IncineratorRecipes.registerRecipes();
        FluidBurnerRecipes.registerRecipes();
        LiquefierRecipes.registerRecipes();
    }

    public Side getSide() {
        return Side.SERVER;
    }
}
