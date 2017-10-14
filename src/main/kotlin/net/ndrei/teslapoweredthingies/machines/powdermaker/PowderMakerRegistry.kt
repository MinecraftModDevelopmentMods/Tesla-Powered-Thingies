package net.ndrei.teslapoweredthingies.machines.powdermaker

import com.google.gson.JsonElement
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.PowderRegistry
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.items.powders.ColoredPowderItem
import net.ndrei.teslapoweredthingies.common.OreOutput
import net.ndrei.teslapoweredthingies.common.SecondaryOutput
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile
import net.ndrei.teslacorelib.config.readItemStack
import net.ndrei.teslacorelib.config.readItemStacks
import net.ndrei.teslacorelib.utils.copyWithSize
import net.ndrei.teslapoweredthingies.MOD_ID

/**
 * Created by CF on 2017-07-06.
 */
@RegistryHandler
object PowderMakerRegistry : IRegistryHandler {
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
                        val item = object: ColoredPowderItem(material, color, 0.0f, "ingot${material.capitalize()}") { }
                        registry.register(item)
//                        item.registerRecipe { AutoRegisterRecipesHandler.registerRecipe(GameRegistry.findRegistry(IRecipe::class.java), it) }
                        if (TeslaCoreLib.isClientSide) {
                            item.registerRenderer()
                        }
                        item
                    }
                    hasDust = true
                }
            } else {
                hasDust = true
            }

            if (hasDust) {
                // register ingot -> dust
                PowderMakerRecipes.registerRecipe(PowderMakerRecipe(
                    ResourceLocation(MOD_ID, "ore_ingot$it"),
                    OreDictionary.getOres("ingot$it").map { it.copyWithSize(1) },
                    listOf(OreOutput("dust$it", 1))
                ))

                // register ore -> dust
//                OreDictionary.getOres("ore$it").forEach { stack ->
//                    PowderMakerRecipes.registerDefaultOreRecipe(it.decapitalize(), stack, true)
//                }
                PowderMakerRecipes.registerDefaultOreRecipe(it)
            }
        }
    }

    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        // register default recipes
        // stones -> 75% gravel
        listOf("stone", "cobblestone").forEach {
            PowderMakerRecipes.registerRecipe(PowderMakerRecipe(
                ResourceLocation(MOD_ID, "ore_$it"),
                OreDictionary.getOres(it).map { it.copyWithSize(1) },
                listOf(SecondaryOutput(.75f, Blocks.GRAVEL))
            ))
        }
        listOf("stoneGranite", "stoneDiorite", "stoneAndesite").forEach {
            PowderMakerRecipes.registerRecipe(PowderMakerRecipe(
                ResourceLocation(MOD_ID, "ore_$it"),
                OreDictionary.getOres(it).map { it.copyWithSize(1) },
                listOf(SecondaryOutput(.75f, Blocks.GRAVEL))
            ))
            PowderMakerRecipes.registerRecipe(PowderMakerRecipe(
                ResourceLocation(MOD_ID, "ore_${it}Polished"),
                OreDictionary.getOres("${it}Polished").map { it.copyWithSize(1) },
                listOf(SecondaryOutput(.75f, Blocks.GRAVEL))
            ))
        }

//        // gravel -> 75% sand
//        PowderMakerRecipes.registerRecipe(PowderMakerOreRecipe(
//                1, "gravel",
//                SecondaryOutput(.75f, Blocks.SAND)
//        ))
//
//        // sandstone -> 75% sand
//        PowderMakerRecipes.registerRecipe(PowderMakerOreRecipe(
//                1, "sandstone",
//                SecondaryOutput(.75f, Blocks.SAND)
//        ))

        // vanilla default ore recipes
        PowderMakerRecipes.registerDefaultOreRecipe("coal")
        PowderMakerRecipes.registerDefaultOreRecipe("diamond")
        PowderMakerRecipes.registerDefaultOreRecipe("emerald")
        PowderMakerRecipes.registerDefaultOreRecipe("redstone")
        PowderMakerRecipes.registerDefaultOreRecipe("lapis")

        readExtraRecipesFile(PowderMakerBlock.registryName!!.resourcePath) { json ->
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
                        PowderMakerRecipes.registerRecipe(PowderMakerRecipe(
                            ResourceLocation(MOD_ID, it.item.registryName.toString().replace(':', '_')),
                            listOf(it), secondary))
                    }
                }
            }
        }
    }
}
