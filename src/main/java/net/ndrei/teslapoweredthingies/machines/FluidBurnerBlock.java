package net.ndrei.teslapoweredthingies.machines;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ndrei.teslapoweredthingies.gui.DualTankEntityRenderer;

/**
 * Created by CF on 2017-01-09.
 */
public class FluidBurnerBlock extends BaseThingyBlock<FluidBurnerEntity> {
    public FluidBurnerBlock() {
        super("fluid_burner", FluidBurnerEntity.class);
    }

    @SideOnly(Side.CLIENT)
    public void registerRenderer() {
        super.registerRenderer();

        ClientRegistry.bindTileEntitySpecialRenderer(FluidBurnerEntity.class, new DualTankEntityRenderer());
    }
}