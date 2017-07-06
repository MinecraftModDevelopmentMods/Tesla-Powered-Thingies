package net.ndrei.teslapoweredthingies.common

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import java.util.*

/**
 * Created by CF on 2017-06-30.
 */
interface IRecipeOutput {
    fun getOutput(): ItemStack
    fun getPossibleOutput(): ItemStack
}

open class Output(private val stack: ItemStack)
    : IRecipeOutput {
    override fun getOutput() = this.stack

    override final fun getPossibleOutput() = this.stack
}

class SecondaryOutput(val chance: Float, stack: ItemStack)
    : Output(stack) {

    constructor(chance: Float, item: Item)
            : this(chance, ItemStack(item))

    constructor(chance: Float, block: Block)
            : this(chance, ItemStack(block))

    override fun getOutput(): ItemStack =
        if (this.chance >= Random().nextFloat())
            super.getOutput()
        else
            ItemStack.EMPTY
}

open class OreOutput(val itemName: String, val quantity: Int)
    : IRecipeOutput {
    override fun getOutput(): ItemStack
        = this.getPossibleOutput()

    override final fun getPossibleOutput(): ItemStack {
        val stack = OreDictionary.getOres(this.itemName).firstOrNull()
        return if (stack == null)
            ItemStack.EMPTY
        else
            ItemStackUtil.copyWithSize(stack, this.quantity)
    }
}

class SecondaryOreOutput(val chance: Float, itemName: String, quantity: Int)
    : OreOutput(itemName, quantity) {

    override fun getOutput(): ItemStack =
            if (this.chance >= Random().nextFloat())
                super.getOutput()
            else
                ItemStack.EMPTY
}