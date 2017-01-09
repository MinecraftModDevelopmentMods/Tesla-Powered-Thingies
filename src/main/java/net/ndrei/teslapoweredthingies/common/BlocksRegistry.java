package net.ndrei.teslapoweredthingies.common;

import net.ndrei.teslapoweredthingies.machines.FluidBurnerBlock;
import net.ndrei.teslapoweredthingies.machines.IncineratorBlock;

/**
 * Created by CF on 2017-01-06.
 */
public class BlocksRegistry {
    public static IncineratorBlock incinerator;
    public static FluidBurnerBlock fluidBurner;

    static void registerBlocks() {
        (BlocksRegistry.incinerator = new IncineratorBlock()).register();
        (BlocksRegistry.fluidBurner = new FluidBurnerBlock()).register();
    }
}
