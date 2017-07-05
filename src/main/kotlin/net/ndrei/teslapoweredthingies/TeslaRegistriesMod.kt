package net.ndrei.teslapoweredthingies

import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary
import net.ndrei.teslacorelib.*
import net.ndrei.teslacorelib.annotations.AutoRegisterRecipesHandler
import net.ndrei.teslacorelib.items.powders.ColoredPowderItem
import net.ndrei.teslapoweredthingies.common.OreOutput
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerOreRecipe
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRecipes

/**
 * Created by CF on 2017-07-05.
 */

@AfterAllModsRegistry
object TeslaRegistriesMod : IAfterAllModsRegistry {
    override fun registerBeforeMaterials(asm : ASMDataTable) { //}
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
                val color = MaterialColors.getColor(it) ?: -1
                if (color != -1) {
                    val material = it.decapitalize()
                    PowderRegistry.addMaterial(it) { registry ->
                        val item = object: ColoredPowderItem(material, color, 0.0f, "ingot${material.capitalize()}") { }
                        registry.register(item)
                        item.registerRecipe { AutoRegisterRecipesHandler.registerRecipe(GameRegistry.findRegistry(IRecipe::class.java), it) }
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
                PowderMakerRecipes.registerRecipe(PowderMakerOreRecipe(1, "ore$it", OreOutput("dust$it", 2)))
            }
        }
    }
}
