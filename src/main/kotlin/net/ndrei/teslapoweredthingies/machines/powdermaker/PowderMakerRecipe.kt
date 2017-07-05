package net.ndrei.teslapoweredthingies.machines.powdermaker

import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistryEntry
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslapoweredthingies.common.IRecipeOutput

/**
 * Created by CF on 2017-07-05.
 */
interface IPowderMakerRecipe: IForgeRegistryEntry<IPowderMakerRecipe> {
    fun canProcess(stack: ItemStack): Boolean
    fun process(stack: ItemStack) : Array<ItemStack>

    fun getPossibleInputs(): List<ItemStack>
    fun getPossibleOutputs(): List<List<ItemStack>>

    fun getOutputs(): List<IRecipeOutput>
}

abstract class PowderMakerRecipeBase(private var registryName: ResourceLocation? = null)
    : IPowderMakerRecipe {

    override fun getRegistryName(): ResourceLocation? = this.registryName

    override fun setRegistryName(name: ResourceLocation?): IPowderMakerRecipe {
        this.registryName = name
        return this
    }

    override fun getRegistryType(): Class<IPowderMakerRecipe> {
        return this.javaClass
    }
}

//class PowderMakerRecipe(val input: ItemStack, val output: ItemStack, vararg val secondary: SecondaryOutput) {
//
//}

class PowderMakerOreRecipe(val inputCount: Int, val input: String, vararg val output: IRecipeOutput)
    : PowderMakerRecipeBase() {

    override fun canProcess(stack: ItemStack)
            = !stack.isEmpty
            && OreDictionary.getOreIDs(stack).map { OreDictionary.getOreName(it) }.contains(this.input)
            && (this.inputCount <= stack.count)

    override fun process(stack: ItemStack): Array<ItemStack> {
        val list = mutableListOf<ItemStack>()

        if (this.canProcess(stack)) {
            stack.shrink(this.inputCount)

            this.output
                    .map { it.getOutput() }
                    .filter { !it.isEmpty }
                    .mapTo(list) { it }
        }

        return list.toTypedArray()
    }

    override fun getPossibleInputs()
        = OreDictionary.getOres(this.input)
            .map { ItemStackUtil.copyWithSize(it, this.inputCount) }
            .toList()

    override fun getPossibleOutputs()
        = this.output
            .map { it.getOutput() }
            .filter { !it.isEmpty }
            .map { listOf(it) }
            .toList()

    override fun getOutputs()
            = this.output.toList()
}