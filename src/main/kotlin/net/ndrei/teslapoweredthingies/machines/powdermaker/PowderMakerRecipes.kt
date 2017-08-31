package net.ndrei.teslapoweredthingies.machines.powdermaker

import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import net.ndrei.teslapoweredthingies.common.*

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

        if (recipe.ensureValidOutputs().isNotEmpty()) {
            this.registerRecipe(recipe)
        }
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
        this.addDefaultOreRecipe(key, { it, ore ->
            PowderMakerRecipe(it,
                    OreOutput(primaryOutput, primaryOutputCount),
                    *if (ore) arrayOf(SecondaryOutput(0.15f, Blocks.COBBLESTONE), *output)
                    else output
            )
        })
    }

    private fun addDefaultOreRecipe(key: String, builder: (input: ItemStack, isOre: Boolean) -> IPowderMakerRecipe) {
        this.defaultOreRecipes[key] = builder
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

        this.addDefaultOreRecipe("coal", { it, ore ->
            PowderMakerRecipe(it, *mutableListOf<IRecipeOutput>(Output(ItemStack(Items.COAL))).also { when (ore) {
                true -> it.add(SecondaryOutput(0.15f, Blocks.COBBLESTONE))
            }}.toTypedArray())
        })
        this.addDefaultOreRecipe("diamond", "dustDiamond", 3)
        this.addDefaultOreRecipe("emerald", "dustEmerald", 3)
        this.addDefaultOreRecipe("redstone", "dustRedstone", 6)
        this.addDefaultOreRecipe("lapis", "gemLapis", 5)
    }
}
