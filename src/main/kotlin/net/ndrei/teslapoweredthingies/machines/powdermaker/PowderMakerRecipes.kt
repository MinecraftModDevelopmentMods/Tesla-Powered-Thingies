package net.ndrei.teslapoweredthingies.machines.powdermaker

import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import net.ndrei.teslapoweredthingies.common.IRecipeOutput
import net.ndrei.teslapoweredthingies.common.OreOutput
import net.ndrei.teslapoweredthingies.common.SecondaryOreOutput
import net.ndrei.teslapoweredthingies.common.SecondaryOutput

/**
 * Created by CF on 2017-07-05.
 */
object PowderMakerRecipes {
    private val recipes = mutableListOf<IPowderMakerRecipe>()
    private val defaultOreRecipes = mutableMapOf<String, (input: ItemStack, isOre: Boolean) -> IPowderMakerRecipe>()

    fun registerRecipe(recipe: IPowderMakerRecipe) {
        this.recipes.add(recipe)
    }

    fun getRecipes() = this.recipes.toList()

    fun findRecipe(stack: ItemStack): IPowderMakerRecipe?
            = this.recipes.firstOrNull { it.canProcess(stack) }

    fun hasRecipe(stack: ItemStack) = (this.findRecipe(stack) != null)

    fun findDefaultOreRecipe(oreKey: String, input: ItemStack, isOre: Boolean): IPowderMakerRecipe? {
        return if (this.defaultOreRecipes.containsKey(oreKey)) {
            this.defaultOreRecipes[oreKey]?.invoke(input, isOre)
        }
        else null
    }

    fun registerDefaultOreRecipe(oreKey: String, input: ItemStack, isOre: Boolean) {
        val recipe = this.findDefaultOreRecipe(oreKey, input, isOre) ?: return
        this.registerRecipe(recipe)
    }

    fun registerDefaultOreRecipe(oreKey: String) {
        OreDictionary.getOres("ore${oreKey.capitalize()}").forEach { stack ->
            PowderMakerRecipes.registerDefaultOreRecipe(oreKey.decapitalize(), stack, true)
        }
    }

    private fun addDefaultOreRecipe(key: String, vararg output: IRecipeOutput) {
        this.addDefaultOreRecipe(key, "dust${key.capitalize()}", *output)
    }

    private fun addDefaultOreRecipe(key: String, primaryOutput: String, vararg output: IRecipeOutput) {
        this.addDefaultOreRecipe(key, primaryOutput, 2, *output)
    }

    private fun addDefaultOreRecipe(key: String, primaryOutput: String, primaryOutputCount: Int, vararg output: IRecipeOutput) {
        this.defaultOreRecipes[key] = { it, ore ->
            PowderMakerRecipe(it,
                    OreOutput(primaryOutput, primaryOutputCount),
                    *if (ore) arrayOf(SecondaryOutput(0.15f, Blocks.COBBLESTONE), *output)
                    else output
            )
        }
    }

    init {
        this.addDefaultOreRecipe("iron",
                SecondaryOreOutput(0.05f, "dustTin", 1),
                SecondaryOreOutput(0.1f, "dustNickel", 1))
        this.addDefaultOreRecipe("gold")
        this.addDefaultOreRecipe("copper",
                SecondaryOreOutput(0.125f, "dustGold", 1))
        this.addDefaultOreRecipe("tin")
        this.addDefaultOreRecipe("silver",
                SecondaryOreOutput(0.1f, "dustLead", 1))
        this.addDefaultOreRecipe("lead",
                SecondaryOreOutput(0.1f, "dustSilver", 1))
        this.addDefaultOreRecipe("aluminum")
        this.addDefaultOreRecipe("nickel",
                SecondaryOreOutput(0.1f, "dustPlatinum", 1))
        this.addDefaultOreRecipe("platinum")
        this.addDefaultOreRecipe("iridium")

        this.addDefaultOreRecipe("coal", "coal", 6)
        this.addDefaultOreRecipe("diamond", "gemDiamond", 4)
        this.addDefaultOreRecipe("emerald", "gemEmerald", 4)
        this.addDefaultOreRecipe("redstone", "dustRedstone", 6)
        this.addDefaultOreRecipe("lapis", "gemLapis", 6)
    }
}