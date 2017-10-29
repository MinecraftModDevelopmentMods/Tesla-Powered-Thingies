package net.ndrei.teslapoweredthingies.machines.compoundmaker

import com.google.common.collect.Lists
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.IItemHandler
import net.ndrei.teslacorelib.utils.equalsIgnoreSize
import net.ndrei.teslacorelib.utils.extractFromCombinedInventory
import net.ndrei.teslacorelib.utils.getCombinedInventory
import net.ndrei.teslacorelib.utils.isEnough
import net.ndrei.teslapoweredthingies.api.compoundmaker.ICompoundMakerRecipe
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistryEntry

class CompoundMakerRecipe(name: ResourceLocation,
                          override val output: ItemStack,
                          val left: FluidStack? = null,
                          val top: Array<ItemStack> = arrayOf(),
                          val right: FluidStack? = null,
                          val bottom: Array<ItemStack> = arrayOf())
    : BaseTeslaRegistryEntry<CompoundMakerRecipe>(CompoundMakerRecipe::class.java, name), ICompoundMakerRecipe<CompoundMakerRecipe> {

    private fun FluidStack?.matchesFluid(fluid: FluidStack?, ignoreSize: Boolean, nullMatches: Boolean) =
        if (nullMatches) {
            (this == null) || (this.amount == 0) // this stack doesn't matter
                || this.isEnough(fluid, ignoreSize)
        } else (this != null) && (this.amount > 0) && this.isEnough(fluid, ignoreSize)

    override fun matchesLeft(fluid: FluidStack?, ignoreSize: Boolean, nullMatches: Boolean) = this.left.matchesFluid(fluid, ignoreSize, nullMatches)
    override fun matchedRight(fluid: FluidStack?, ignoreSize: Boolean, nullMatches: Boolean) = this.right.matchesFluid(fluid, ignoreSize, nullMatches)

    private fun Array<ItemStack>.getCombinedInventory(): List<ItemStack> {
        val list = Lists.newArrayList<ItemStack>()
        for (stack in this) {
            if (stack.isEmpty) {
                continue
            }

            val match: ItemStack? = list.firstOrNull { it.equalsIgnoreSize(stack) }
            if (match == null) {
                list.add(stack.copy())
            } else {
                match.count = match.count + stack.count
            }
        }
        return list
    }

    private fun Array<ItemStack>.matchesInventory(holder: IItemHandler, ignoreSize: Boolean, emptyMatches: Boolean): Boolean {
        if (this.isEmpty())
            return emptyMatches

        val inventory = holder.getCombinedInventory()
        return this.getCombinedInventory().all { test ->
            inventory.any { it.equalsIgnoreSize(test) && (ignoreSize || (it.count >= test.count)) }
        }
    }

    private fun matchesTop(holder: IItemHandler, ignoreSize: Boolean) = this.top.matchesInventory(holder, ignoreSize, true)
    private fun matchesBottom(holder: IItemHandler, ignoreSize: Boolean) = this.bottom.matchesInventory(holder, ignoreSize, true)

    override fun matchesTop(stack: ItemStack, ignoreSize: Boolean, emptyMatcher: Boolean) =
        (emptyMatcher && this.top.isEmpty()) || this.top.getCombinedInventory().any {
            it.equalsIgnoreSize(stack) && (ignoreSize || (stack.count >= it.count))
        }

    override fun matchesBottom(stack: ItemStack, ignoreSize: Boolean, emptyMatcher: Boolean) =
        (emptyMatcher && this.bottom.isEmpty()) || this.bottom.getCombinedInventory().any {
            it.equalsIgnoreSize(stack) && (ignoreSize || (stack.count >= it.count))
        }

    override fun matches(left: IFluidTank, top: IItemHandler, right: IFluidTank, bottom: IItemHandler) =
        this.matchesLeft(left.fluid, false, true)
            && this.matchedRight(right.fluid, false, true)
            && this.matchesTop(top, false)
            && this.matchesBottom(bottom, false)

    private fun FluidStack?.drainFluid(tank: IFluidTank, doDrain: Boolean): Boolean {
        if ((this == null) || (this.amount == 0))
            return true

        return this.isEnough(tank.drain(this.amount, doDrain), false)
    }

    private fun Array<ItemStack>.takeFrom(handler: IItemHandler, doTake: Boolean): Boolean {
        if (this.isEmpty())
            return true

        return this.getCombinedInventory().all { handler.extractFromCombinedInventory(it, it.count, !doTake) == it.count }
    }

    override fun processInventories(left: IFluidTank, top: IItemHandler, right: IFluidTank, bottom: IItemHandler): Boolean {
        if (!this.matches(left, top, right, bottom))
            return false

        if (this.left.drainFluid(left, false) && this.right.drainFluid(right, false) &&
            this.top.takeFrom(top, false) && this.bottom.takeFrom(bottom, false)) {
            this.left.drainFluid(left, true)
            this.top.takeFrom(top, true)
            this.right.drainFluid(right, true)
            this.bottom.takeFrom(bottom, true)

            return true
        }

        return false
    }
}
