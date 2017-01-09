package net.ndrei.teslapoweredthingies.machines.fluidburner;

import net.minecraftforge.fluids.Fluid;

/**
 * Created by CF on 2017-01-09.
 */
public class FluidBurnerFuelRecipe {
    public final Fluid fluid;
    public final int amount;
    public final int baseTicks;

    public FluidBurnerFuelRecipe(Fluid fluid, int amount, int baseTicks) {
        this.fluid = fluid;
        this.amount = amount;
        this.baseTicks = baseTicks;
    }
}
