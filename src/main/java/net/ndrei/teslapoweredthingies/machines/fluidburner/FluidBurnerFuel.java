package net.ndrei.teslapoweredthingies.machines.fluidburner;

import net.minecraftforge.fluids.FluidStack;

/**
 * Created by CF on 2017-01-09.
 */
public class FluidBurnerFuel {
    public final FluidBurnerFuelRecipe recipe;
    public final FluidStack fuel;

    public FluidBurnerFuel(FluidBurnerFuelRecipe recipe, FluidStack fuel) {
        this.recipe = recipe;
        this.fuel = fuel;
    }
}
