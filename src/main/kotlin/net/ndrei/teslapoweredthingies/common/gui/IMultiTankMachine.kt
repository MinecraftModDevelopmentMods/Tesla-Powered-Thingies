package net.ndrei.teslapoweredthingies.common.gui

import net.minecraftforge.fluids.FluidStack

/**
 * Created by CF on 2017-06-30.
 */
interface IMultiTankMachine {
    fun getTanks(): List<TankInfo>
}

class TankInfo(val left: Double, val top: Double, val fluid: FluidStack?, val capacity: Int)
