package net.ndrei.teslapoweredthingies.machines.fluidburner;

import net.minecraftforge.fluids.FluidStack;

/**
 * Created by CF on 2017-01-09.
 */
public class FluidBurnerCoolant {
    public final FluidBurnerCoolantRecipe recipe;
    public final FluidStack coolant;

    public FluidBurnerCoolant(FluidBurnerCoolantRecipe recipe, FluidStack coolant) {
        this.recipe = recipe;
        this.coolant = coolant;
    }
}
