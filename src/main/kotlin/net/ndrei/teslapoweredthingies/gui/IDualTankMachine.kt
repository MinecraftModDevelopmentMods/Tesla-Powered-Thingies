package net.ndrei.teslapoweredthingies.gui

import net.minecraftforge.fluids.Fluid

/**
 * Created by CF on 2017-06-30.
 */
interface IDualTankMachine {
    val leftTankFluid: Fluid
    val leftTankPercent: Float

    val rightTankFluid: Fluid
    val rightTankPercent: Float
}
