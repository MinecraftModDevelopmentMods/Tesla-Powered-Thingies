package net.ndrei.teslapoweredthingies.machines.powdermaker

import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.ndrei.teslacorelib.utils.equalsIgnoreSize
import net.ndrei.teslacorelib.utils.equalsIgnoreSizeAndNBT
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistryEntry
import net.ndrei.teslapoweredthingies.common.IChancedRecipeOutput
import net.ndrei.teslapoweredthingies.common.IRecipeOutput

/**
 * Created by CF on 2017-07-05.
 */
interface IPowderMakerRecipe {
    fun canProcess(stack: ItemStack): Boolean
    fun process(stack: ItemStack) : PowderMakerRecipeResult

    fun getInputCount(stack: ItemStack): Int
    fun getPossibleInputs(): List<ItemStack>
    fun getPossibleOutputs(): List<List<ItemStack>>
    fun ensureValidOutputs(): List<List<ItemStack>>

    fun getOutputs(): List<IRecipeOutput>
}

class PowderMakerRecipeResult(val remaining: ItemStack, val primary: Array<ItemStack>, val secondary: Array<ItemStack>)

class PowderMakerRecipe(name: ResourceLocation, private var input: List<ItemStack>, private var output: List<IRecipeOutput>)
    : BaseTeslaRegistryEntry<PowderMakerRecipe>(PowderMakerRecipe::class.java, name), IPowderMakerRecipe {

    override fun canProcess(stack: ItemStack)=
        this.input.any { it.equalsIgnoreSizeAndNBT(stack) && (stack.count >= it.count) }

    override fun getInputCount(stack: ItemStack) =
        this.input.first { it.equalsIgnoreSizeAndNBT(stack) && (stack.count >= it.count) }.count

    override fun process(stack: ItemStack): PowderMakerRecipeResult {
        val primary = mutableListOf<ItemStack>()
        val secondary = mutableListOf<ItemStack>()
        val remaining = stack.copy()

        val matchingInput = this.input.firstOrNull { it.equalsIgnoreSize(remaining) && (remaining.count >= it.count) }

        if (/*this.canProcess(remaining)*/ matchingInput != null) {
            remaining.shrink(matchingInput.count)

            this.output
                .filter { it !is IChancedRecipeOutput }
                .map { it.getOutput() }
                .filterTo(primary) { !it.isEmpty }

            this.output
                .filter { it is IChancedRecipeOutput }
                .map { it.getOutput() }
                .filterTo(secondary) { !it.isEmpty }
        }

        return PowderMakerRecipeResult(remaining, primary.toTypedArray(), secondary.toTypedArray())
    }

    override fun getPossibleInputs() = this.input

    override fun getPossibleOutputs() =
        this.output
            .map { it.getPossibleOutput() }
            .filter { !it.isEmpty }
            .map { listOf(it) }
            .filter { !it.isEmpty() }
            .toList()

    override fun getOutputs()
        = this.output.toList()

    override fun ensureValidOutputs(): List<List<ItemStack>> {
        this.output = this.output.filter {
            !it.getPossibleOutput().isEmpty
        }

        return this.getPossibleOutputs()
    }
}
//
//class PowderMakerRecipe(val input: ItemStack, vararg output: IRecipeOutput)
//    : PowderMakerRecipeBase(*output) {
//    override fun canProcess(stack: ItemStack)
//        = this.input.equalsIgnoreSizeAndNBT(stack) && (stack.count >= this.input.count)
//
//    override fun getInputCount() = this.input.count
//    override fun getPossibleInputs() = listOf(this.input.copy())
//}
//
//class PowderMakerOreRecipe(val inputSize: Int, val input: String, vararg output: IRecipeOutput)
//    : PowderMakerRecipeBase(*output) {
//
//    override fun canProcess(stack: ItemStack)
//            = !stack.isEmpty
//            && OreDictionary.getOreIDs(stack).map { OreDictionary.getOreName(it) }.contains(this.input)
//            && (this.inputSize <= stack.count)
//
//    override fun getInputCount() = this.inputSize
//
//    override fun getPossibleInputs()
//        = OreDictionary.getOres(this.input)
//            .map { it.copyWithSize(this.inputSize) }
//            .toList()
//}
