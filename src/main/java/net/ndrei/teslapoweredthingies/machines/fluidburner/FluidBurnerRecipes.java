package net.ndrei.teslapoweredthingies.machines.fluidburner;

import com.google.common.collect.Lists;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.List;

/**
 * Created by CF on 2017-01-09.
 */
public final class FluidBurnerRecipes {
    private static List<FluidBurnerCoolantRecipe> coolantRecipes;
    private static List<FluidBurnerFuelRecipe> fuelRecipes;

    public static void registerRecipes() {
        coolantRecipes = Lists.newArrayList();
        fuelRecipes = Lists.newArrayList();

        // register vanilla fluids
        coolantRecipes.add(new FluidBurnerCoolantRecipe(FluidRegistry.WATER, 100, 1.2f));
        fuelRecipes.add(new FluidBurnerFuelRecipe(FluidRegistry.LAVA, 100, 20 * 30));
    }

    public static boolean isCoolant(FluidStack stack) {
        return (stack.getFluid() == FluidRegistry.WATER);
    }

    public static boolean isFuel(FluidStack stack) {
        return (stack.getFluid() == FluidRegistry.LAVA);
    }

    public static FluidBurnerCoolant drainCoolant(IFluidTank tank, boolean doDrain) {
        FluidStack existing = tank.getFluid();
        if ((existing != null) && (existing.amount > 0)) {
            for(FluidBurnerCoolantRecipe recipe : coolantRecipes) {
                if (recipe.fluid.equals(existing.getFluid()) && (recipe.amount <= existing.amount)) {
                    return new FluidBurnerCoolant(recipe, tank.drain(recipe.amount, doDrain));
                }
            }
        }
        return null;
    }

    public static FluidBurnerFuel drainFuel(IFluidTank tank, boolean doDrain) {
        FluidStack existing = tank.getFluid();
        if ((existing != null) && (existing.amount > 0)) {
            for(FluidBurnerFuelRecipe recipe : fuelRecipes) {
                if (recipe.fluid.equals(existing.getFluid()) && (recipe.amount <= existing.amount)) {
                    return new FluidBurnerFuel(recipe, tank.drain(recipe.amount, doDrain));
                }
            }
        }
        return null;
    }
}
