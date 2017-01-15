package net.ndrei.teslapoweredthingies.gui;

import net.minecraftforge.fluids.Fluid;

/**
 * Created by CF on 2017-01-11.
 */
public interface IDualTankMachine {
    Fluid getLeftTankFluid();
    float getLeftTankPercent();

    Fluid getRightTankFluid();
    float getRightTankPercent();
}
