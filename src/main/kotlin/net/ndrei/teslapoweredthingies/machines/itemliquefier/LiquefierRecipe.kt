package net.ndrei.teslapoweredthingies.machines.itemliquefier

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-06-30.
 */
class LiquefierRecipe(val input: ItemStack, val output: FluidStack) {
    constructor(input: Block, output: Fluid, outputQuantity: Int)
            : this(input, 1, output, outputQuantity)

    constructor(input: Block, inputStackSize: Int, output: Fluid, outputQuantity: Int)
            : this(ItemStack(Item.getItemFromBlock(input), inputStackSize), FluidStack(output, outputQuantity))

    constructor(input: Item, output: Fluid, outputQuantity: Int)
            : this(input, 1, output, outputQuantity)

    constructor(input: Item, inputStackSize: Int, output: Fluid, outputQuantity: Int)
            : this(ItemStack(input, inputStackSize), FluidStack(output, outputQuantity))

    fun serializeNBT(): NBTTagCompound {
        val nbt = NBTTagCompound()

        nbt.setTag("input", this.input.serializeNBT())
        nbt.setTag("output", this.output.writeToNBT(NBTTagCompound()))

        return nbt
    }

    companion object {
        fun deserializeNBT(nbt: NBTTagCompound?): LiquefierRecipe? {
            if (nbt == null || !nbt.hasKey("input", Constants.NBT.TAG_COMPOUND) || !nbt.hasKey("output", Constants.NBT.TAG_COMPOUND)) {
                TeslaThingiesMod.logger.error("Incorrect NBT storage for Liquefier Recipe.", nbt)
                return null
            } else {
                val input = ItemStack(nbt.getCompoundTag("input"))
                val output = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("output"))
                return LiquefierRecipe(input, output!!)
            }
        }
    }
}
