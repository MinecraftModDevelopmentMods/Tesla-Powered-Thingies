package net.ndrei.teslapoweredthingies.machines.fluidburner

import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank

/**
 * Created by CF on 2017-06-30.
 */
object FluidBurnerRecipes {
    private val coolantRecipes: MutableList<FluidBurnerCoolantRecipe> = mutableListOf()
    private val fuelRecipes: MutableList<FluidBurnerFuelRecipe> = mutableListOf()

    fun registerRecipes() {
        coolantRecipes.clear()
        fuelRecipes.clear()

        // register vanilla fluids
        coolantRecipes!!.add(FluidBurnerCoolantRecipe(FluidRegistry.WATER, 100, 1.2f))
        fuelRecipes!!.add(FluidBurnerFuelRecipe(FluidRegistry.LAVA, 100, 20 * 30))
    }

    fun isCoolant(stack: FluidStack): Boolean {
        return stack.fluid === FluidRegistry.WATER
    }

    fun isFuel(stack: FluidStack): Boolean {
        return stack.fluid === FluidRegistry.LAVA
    }

    fun drainCoolant(tank: IFluidTank, doDrain: Boolean): FluidBurnerCoolant? {
        val existing = tank.fluid
        if (existing != null && existing.amount > 0) {
            for (recipe in coolantRecipes!!) {
                if (recipe.fluid == existing.fluid && recipe.amount <= existing.amount) {
                    return FluidBurnerCoolant(recipe, tank.drain(recipe.amount, doDrain)!!)
                }
            }
        }
        return null
    }

    fun drainFuel(tank: IFluidTank, doDrain: Boolean): FluidBurnerFuel? {
        val existing = tank.fluid
        if (existing != null && existing.amount > 0) {
            for (recipe in fuelRecipes!!) {
                if (recipe.fluid == existing.fluid && recipe.amount <= existing.amount) {
                    return FluidBurnerFuel(recipe, tank.drain(recipe.amount, doDrain)!!)
                }
            }
        }
        return null
    }

    val fuels: List<FluidBurnerFuelRecipe>
        get() = fuelRecipes.toList()

    val coolants: List<FluidBurnerCoolantRecipe>
        get() = coolantRecipes.toList()
}
