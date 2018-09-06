package net.ndrei.teslapoweredthingies.machines.powdermaker

import com.google.gson.JsonElement
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.PowderRegistry
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AutoRegisterRecipesHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.config.readItemStack
import net.ndrei.teslacorelib.config.readItemStacks
import net.ndrei.teslacorelib.items.powders.ColoredPowderItem
import net.ndrei.teslacorelib.utils.copyWithSize
import net.ndrei.teslapoweredthingies.MOD_ID
import net.ndrei.teslapoweredthingies.common.*
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile

/**
 * Created by CF on 2017-07-06.
 */
@RegistryHandler
object PowderMakerRegistry : BaseTeslaRegistry<PowderMakerRecipe>("powder_maker_recipes", PowderMakerRecipe::class.java) {
    private val registeredDustNames = mutableListOf<String>()
    private val registeredDusts = mutableListOf<ColoredPowderItem>()

    override fun registerItems(asm : ASMDataTable, registry: IForgeRegistry<Item>) {
        // get all ores
        val ores = OreDictionary.getOreNames()
                .filter { it.startsWith("ore") }
                .map { it.substring(3) }
                .filter { OreDictionary.doesOreNameExist("ingot$it") }

        // register powder maker recipes
        ores.forEach {
            val dustName = "dust$it"
            var hasDust = false
            if (!OreDictionary.doesOreNameExist(dustName)) {
                // look for an item with color
                val color = MaterialColors.getColor(it)
                if (color != null) {
                    val material = it.decapitalize()
                    PowderRegistry.addMaterial(it) { registry ->
                        val item = object: ColoredPowderItem(material, color, 0.0f, "ingot${material.capitalize()}") {}
                        registry.register(item)
                        item.registerRecipe { AutoRegisterRecipesHandler.registerRecipe(GameRegistry.findRegistry(IRecipe::class.java), it) }
                        if (TeslaCoreLib.isClientSide) {
                            item.registerRenderer()
                            registeredDusts.add(item)
                        }
                        OreDictionary.registerOre("dust${material.capitalize()}", item)
                        item
                    }
                    hasDust = true
                }
            } else {
                hasDust = true
            }

            if (hasDust) {
                this.registeredDustNames.add(it)
            }
        }
    }

    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        // register default recipes
        // stones -> 75% gravel
        listOf("stone", "cobblestone").forEach {
            this.addRecipe(PowderMakerRecipe(
                ResourceLocation(MOD_ID, "ore_$it"),
                OreDictionary.getOres(it).map { it.copyWithSize(1) },
                listOf(SecondaryOutput(.75f, Blocks.GRAVEL))
            ))
        }
        listOf("stoneGranite", "stoneDiorite", "stoneAndesite").forEach {
            this.addRecipe(PowderMakerRecipe(
                ResourceLocation(MOD_ID, "ore_$it"),
                OreDictionary.getOres(it).map { it.copyWithSize(1) },
                listOf(SecondaryOutput(.75f, Blocks.GRAVEL))
            ))
            this.addRecipe(PowderMakerRecipe(
                ResourceLocation(MOD_ID, "ore_${it}Polished"),
                OreDictionary.getOres("${it}Polished").map { it.copyWithSize(1) },
                listOf(SecondaryOutput(.75f, Blocks.GRAVEL))
            ))
        }

        // vanilla default ore recipes
        this.registerDefaultOreRecipe("coal")
        this.registerDefaultOreRecipe("diamond")
        this.registerDefaultOreRecipe("emerald")
        this.registerDefaultOreRecipe("redstone")
        this.registerDefaultOreRecipe("lapis")

        // register dust recipes
        this.registeredDustNames.forEach {
            // register ingot -> dust
            this.addRecipe(PowderMakerRecipe(
                ResourceLocation(MOD_ID, "ore_ingot$it"),
                OreDictionary.getOres("ingot$it").map { it.copyWithSize(1) },
                listOf(OreOutput("dust$it", 1))
            ))

            // register ore -> dust
            this.registerDefaultOreRecipe(it)
        }

        OreDictionary.getOreNames()
            .filter { it.startsWith("ore") }
            .map { it.substring(3) }
            .filter { OreDictionary.doesOreNameExist("dust$it") }
            .filter { key -> !this.hasRecipe { it.getPossibleInputs().any { OreDictionary.getOreIDs(it).contains(OreDictionary.getOreID("ore$key")) } } }
            .forEach {
                this.registerDefaultOreRecipe(it)
            }

        readExtraRecipesFile(PowderMakerBlock.registryName!!.path) { json ->
            val inputs = json.readItemStacks("input_stack")
            if (inputs.isNotEmpty() && json.has("outputs")) {
                val secondary = json.getAsJsonArray("outputs").mapNotNull<JsonElement, SecondaryOutput> {
                    if (it.isJsonObject) {
                        val stack = it.asJsonObject.readItemStack() ?: return@mapNotNull null
                        val chance = JsonUtils.getFloat(it.asJsonObject, "chance", 1.0f)
                        if (chance > 0.0f) {
                            return@mapNotNull SecondaryOutput(Math.min(chance, 1.0f), stack)
                        }
                    }
                    return@mapNotNull null
                }
                if (secondary.isNotEmpty()) {
                    inputs.forEach {
                        this.addRecipe(PowderMakerRecipe(
                            ResourceLocation(MOD_ID, it.item.registryName.toString().replace(':', '_')),
                            listOf(it), secondary))
                    }
                }
            }
        }
    }

    override fun postInit(asm: ASMDataTable) {
        if (this.registeredDusts.isNotEmpty() && TeslaCoreLib.isClientSide) {
            this.registeredDusts.forEach {
                Minecraft.getMinecraft().itemColors.registerItemColorHandler({ s: ItemStack, t: Int -> it.getColorFromItemStack(s, t) }, arrayOf<Item>(it))
            }
            this.registeredDusts.clear()
        }
    }

    //#region default ore recipes

    private val defaultOreRecipes = mutableMapOf<String, (input: ItemStack, isOre: Boolean) -> PowderMakerRecipe>()

    private fun findDefaultOreRecipe(oreKey: String, input: ItemStack, isOre: Boolean): PowderMakerRecipe? {
        return if (this.defaultOreRecipes.containsKey(oreKey)) {
            this.defaultOreRecipes[oreKey]?.invoke(input, isOre)
        }
        else null
    }

    fun registerDefaultOreRecipe(oreKey: String, input: ItemStack, isOre: Boolean) {
        val recipe = this.findDefaultOreRecipe(oreKey, input, isOre)
        if ((recipe != null) && recipe.ensureValidOutputs().isNotEmpty()) {
            this.addRecipe(recipe)
        }
        else if (recipe == null) {
            this.addRecipe(PowderMakerRecipe(
                ResourceLocation(MOD_ID, input.item.registryName.toString().replace(':', '_')),
                listOf(input), mutableListOf<IRecipeOutput>().also { list ->
                list.add(OreOutput("dust${oreKey.capitalize()}", 2))
                if (isOre) {
                    list.add(SecondaryOutput(0.15f, Blocks.COBBLESTONE))
                }
            }))
        }
    }

    private fun registerDefaultOreRecipe(oreKey: String) {
        OreDictionary.getOres("ore${oreKey.capitalize()}").forEach { stack ->
            this.registerDefaultOreRecipe(oreKey.decapitalize(), stack, true)
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
            PowderMakerRecipe(
                ResourceLocation(MOD_ID, it.item.registryName.toString().replace(':', '_')),
                listOf(it), mutableListOf<IRecipeOutput>().also { list ->
                list.add(OreOutput(primaryOutput, primaryOutputCount))
                if (ore) {
                    list.add(SecondaryOutput(0.15f, Blocks.COBBLESTONE))
                }
                if (output.isNotEmpty()) {
                    list.addAll(output)
                }
            })
        })
    }

    private fun addDefaultOreRecipe(key: String, builder: (input: ItemStack, isOre: Boolean) -> PowderMakerRecipe) {
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
            PowderMakerRecipe(
                ResourceLocation(MOD_ID, it.item.registryName.toString().replace(':', '_')),
                listOf(it), mutableListOf<IRecipeOutput>().also { list ->
                list.add(Output(ItemStack(Items.COAL, 5)))
                if (ore) {
                    list.add(SecondaryOutput(0.15f, Blocks.COBBLESTONE))
                }
            })
        })
        this.addDefaultOreRecipe("diamond", "dustDiamond", 3)
        this.addDefaultOreRecipe("emerald", "dustEmerald", 3)
        this.addDefaultOreRecipe("redstone", "dustRedstone", 6)
        this.addDefaultOreRecipe("lapis", "gemLapis", 5)
    }

    //#endregion

    fun hasRecipe(stack: ItemStack) = this.hasRecipe { it.canProcess(stack) }
    fun findRecipe(stack: ItemStack): PowderMakerRecipe? = this.findRecipe { it.canProcess(stack) }
}
