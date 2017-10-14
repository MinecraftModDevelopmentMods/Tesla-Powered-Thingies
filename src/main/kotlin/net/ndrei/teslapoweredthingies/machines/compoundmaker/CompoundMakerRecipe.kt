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
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistryEntry

class CompoundMakerRecipe(name: ResourceLocation,
                          val output: ItemStack,
                          val left: FluidStack? = null,
                          val top: Array<ItemStack> = arrayOf(),
                          val right: FluidStack? = null,
                          val bottom: Array<ItemStack> = arrayOf())
    : BaseTeslaRegistryEntry<CompoundMakerRecipe>(CompoundMakerRecipe::class.java, name) {

    private fun FluidStack?.matchesFluid(fluid: FluidStack?, ignoreSize: Boolean) =
        (this == null) || (this.amount == 0) // this stack doesn't matter
            || this.isEnough(fluid, ignoreSize)

    fun matchesLeft(fluid: FluidStack?, ignoreSize: Boolean) = this.left.matchesFluid(fluid, ignoreSize)
    fun matchedRight(fluid: FluidStack?, ignoreSize: Boolean) = this.right.matchesFluid(fluid, ignoreSize)

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

    private fun Array<ItemStack>.matchesInventory(holder: IItemHandler, ignoreSize: Boolean): Boolean {
        if (this.isEmpty())
            return true

        val inventory = holder.getCombinedInventory()
        return !this.getCombinedInventory().any { test ->
            inventory.any { it.equalsIgnoreSize(test) && (ignoreSize || (it.count >= test.count)) }
        }
    }

    fun matchesTop(holder: IItemHandler, ignoreSize: Boolean) = this.top.matchesInventory(holder, ignoreSize)
    fun matchesBottom(holder: IItemHandler, ignoreSize: Boolean) = this.bottom.matchesInventory(holder, ignoreSize)

    fun matchesTop(stack: ItemStack, ignoreSize: Boolean) = this.top.isNotEmpty() && this.top.getCombinedInventory().any {
        it.equalsIgnoreSize(stack) && (ignoreSize || (stack.count >= it.count))
    }

    fun matchesBottom(stack: ItemStack, ignoreSize: Boolean) = this.bottom.isNotEmpty() && this.bottom.getCombinedInventory().any {
        it.equalsIgnoreSize(stack) && (ignoreSize || (stack.count >= it.count))
    }

    fun matches(left: IFluidTank, top: IItemHandler, right: IFluidTank, bottom: IItemHandler) =
        this.matchesLeft(left.fluid, false)
            && this.matchedRight(right.fluid, false)
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

        return this.getCombinedInventory().all { handler.extractFromCombinedInventory(it, it.count) == it.count }
    }

    fun processInventories(left: IFluidTank, top: IItemHandler, right: IFluidTank, bottom: IItemHandler): Boolean {
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
