package net.ndrei.teslapoweredthingies.common

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import net.ndrei.teslacorelib.utils.copyWithSize
import java.util.*

/**
 * Created by CF on 2017-06-30.
 */
interface IRecipeOutput {
    fun getOutput(): ItemStack
    fun getPossibleOutput(): ItemStack
}

interface IChancedRecipeOutput {
    val chance: Float
}

open class Output(private val stack: ItemStack)
    : IRecipeOutput {
    override fun getOutput() = this.stack.copy()

    override final fun getPossibleOutput() = this.stack.copy()
}

class SecondaryOutput(override val chance: Float, stack: ItemStack)
    : Output(stack), IChancedRecipeOutput {

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
        return stack?.copyWithSize(this.quantity) ?: ItemStack.EMPTY
    }
}

class SecondaryOreOutput(override val chance: Float, itemName: String, quantity: Int)
    : OreOutput(itemName, quantity), IChancedRecipeOutput {

    override fun getOutput(): ItemStack =
            if (this.chance >= Random().nextFloat())
                super.getOutput()
            else
                ItemStack.EMPTY
}