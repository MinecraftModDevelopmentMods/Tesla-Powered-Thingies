package net.ndrei.teslapoweredthingies.machines;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ndrei.teslapoweredthingies.gui.DualTankEntityRenderer;

/**
 * Created by CF on 2017-01-11.
 */
public class FluidSolidifierBlock extends BaseThingyBlock<FluidSolidifierEntity> {
    public FluidSolidifierBlock() {
        super("fluid_solidifier", FluidSolidifierEntity.class);
    }

    @SideOnly(Side.CLIENT)
    public void registerRenderer() {
        super.registerRenderer();

        ClientRegistry.bindTileEntitySpecialRenderer(FluidSolidifierEntity.class, new DualTankEntityRenderer());
    }
}
