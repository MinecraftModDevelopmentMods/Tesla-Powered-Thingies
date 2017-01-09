package net.ndrei.teslapoweredthingies.machines.fluidburner;

import net.minecraftforge.fluids.Fluid;

/**
 * Created by CF on 2017-01-09.
 */
public class FluidBurnerCoolantRecipe {
    public final Fluid fluid;
    public final int amount;
    public final float timeMultiplier;

    public FluidBurnerCoolantRecipe(Fluid fluid, int amount, float timeMultiplier) {
        this.fluid = fluid;
        this.amount = amount;
        this.timeMultiplier = timeMultiplier;
    }
}
