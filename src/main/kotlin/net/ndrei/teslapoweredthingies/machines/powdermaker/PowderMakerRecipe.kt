package net.ndrei.teslapoweredthingies.machines.powdermaker

import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.ndrei.teslacorelib.utils.equalsIgnoreSize
import net.ndrei.teslacorelib.utils.equalsIgnoreSizeAndNBT
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistryEntry
import net.ndrei.teslapoweredthingies.common.IChancedRecipeOutput
import net.ndrei.teslapoweredthingies.common.IRecipeOutput

class PowderMakerRecipe(name: ResourceLocation, private var input: List<ItemStack>, private var output: List<IRecipeOutput>)
    : BaseTeslaRegistryEntry<PowderMakerRecipe>(PowderMakerRecipe::class.java, name) {

    fun canProcess(stack: ItemStack)=
        this.input.any { it.equalsIgnoreSizeAndNBT(stack) && (stack.count >= it.count) }

    fun getInputCount(stack: ItemStack) =
        this.input.first { it.equalsIgnoreSizeAndNBT(stack) && (stack.count >= it.count) }.count

    fun process(stack: ItemStack): PowderMakerRecipeResult {
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

    fun getPossibleInputs() = this.input

    fun getPossibleOutputs() =
        this.output
            .map { it.getPossibleOutput() }
            .filter { !it.isEmpty }
            .map { listOf(it) }
            .filter { !it.isEmpty() }
            .toList()

    fun getOutputs()
        = this.output.toList()

    fun ensureValidOutputs(): List<List<ItemStack>> {
        this.output = this.output.filter {
            !it.getPossibleOutput().isEmpty
        }

        return this.getPossibleOutputs()
    }
}
