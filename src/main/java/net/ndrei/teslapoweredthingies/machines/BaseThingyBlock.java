package net.ndrei.teslapoweredthingies.machines;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.ndrei.teslacorelib.blocks.OrientedBlock;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;

/**
 * Created by CF on 2017-01-06.
 */
public class BaseThingyBlock<T extends TileEntity> extends OrientedBlock<T> {
    protected BaseThingyBlock(String registryName, Class teClass) {
        super(TeslaThingiesMod.MODID, TeslaThingiesMod.creativeTab, registryName, teClass);
    }

    protected BaseThingyBlock(String registryName, Class teClass, Material material) {
        super(TeslaThingiesMod.MODID, TeslaThingiesMod.creativeTab, registryName, teClass, material);
    }
}
