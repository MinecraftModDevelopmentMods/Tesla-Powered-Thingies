package net.ndrei.teslapoweredthingies.machines.fluidburner

import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.ndrei.teslacorelib.utils.isEmpty

/**
 * Created by CF on 2017-06-30.
 */
object FluidBurnerRecipes {
    fun isCoolant(fluid: FluidStack) = FluidBurnerCoolantRegistry.hasRecipe(fluid)

    fun isFuel(fuel: FluidStack) = FluidBurnerFuelRegistry.hasRecipe(fuel)

    fun drainCoolant(tank: IFluidTank, doDrain: Boolean): FluidBurnerCoolant? {
        val existing = tank.fluid
        if (!existing.isEmpty) {
            val recipe = FluidBurnerCoolantRegistry.findRecipe(existing!!)
            if (recipe != null) {
                return FluidBurnerCoolant(recipe, tank.drain(recipe.fluid.amount, doDrain)!!)
            }
        }
        return null
    }

    fun drainFuel(tank: IFluidTank, doDrain: Boolean): FluidBurnerFuel? {
        val existing = tank.fluid
        if (!existing.isEmpty) {
            val recipe = FluidBurnerFuelRegistry.findRecipe(existing!!)
            if (recipe != null) {
                return FluidBurnerFuel(recipe, tank.drain(recipe.fluid.amount, doDrain)!!)
            }
        }
        return null
    }

    val fuels get() = FluidBurnerFuelRegistry.getAllRecipes()

    val coolants get() = FluidBurnerCoolantRegistry.getAllRecipes()
}
