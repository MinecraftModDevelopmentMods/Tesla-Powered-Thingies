package net.ndrei.teslapoweredthingies.api.compoundmaker

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.IItemHandler
import net.ndrei.teslapoweredthingies.api.IPoweredRegistry

interface ICompoundMakerRegistry<R: ICompoundMakerRecipe<R>>: IPoweredRegistry<R> {
    fun acceptsLeft(fluid: FluidStack) = this.hasRecipe { it.matchesLeft(fluid, true, false) }
    fun acceptsRight(fluid: FluidStack) = this.hasRecipe { it.matchedRight(fluid, true, false) }
    fun acceptsTop(stack: ItemStack) = this.hasRecipe { it.matchesTop(stack, true, false) }
    fun acceptsBottom(stack: ItemStack) = this.hasRecipe { it.matchesBottom(stack, true, false) }

    fun findRecipes(left: IFluidTank, top: IItemHandler, right: IFluidTank, bottom: IItemHandler) =
        this.findRecipes { it.matches(left, top, right, bottom) }

    fun registerRecipe(output: ItemStack,
                       left: FluidStack? = null,
                       top: Array<ItemStack> = arrayOf(),
                       right: FluidStack? = null,
                       bottom: Array<ItemStack> = arrayOf()): ICompoundMakerRegistry<R>
}
