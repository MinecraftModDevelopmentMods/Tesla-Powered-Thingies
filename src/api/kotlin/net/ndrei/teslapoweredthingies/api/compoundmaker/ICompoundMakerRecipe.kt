package net.ndrei.teslapoweredthingies.api.compoundmaker

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.IItemHandler
import net.ndrei.teslapoweredthingies.api.IPoweredRecipe

interface ICompoundMakerRecipe<R: ICompoundMakerRecipe<R>>: IPoweredRecipe<R> {
    val output: ItemStack

    fun matchesLeft(fluid: FluidStack?, ignoreSize: Boolean, nullMatches: Boolean): Boolean
    fun matchedRight(fluid: FluidStack?, ignoreSize: Boolean, nullMatches: Boolean): Boolean
    fun matchesTop(stack: ItemStack, ignoreSize: Boolean, emptyMatcher: Boolean): Boolean
    fun matchesBottom(stack: ItemStack, ignoreSize: Boolean, emptyMatcher: Boolean): Boolean

    fun matches(left: IFluidTank, top: IItemHandler, right: IFluidTank, bottom: IItemHandler): Boolean

    fun processInventories(left: IFluidTank, top: IItemHandler, right: IFluidTank, bottom: IItemHandler): Boolean
}
