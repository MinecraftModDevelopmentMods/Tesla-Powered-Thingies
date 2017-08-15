package net.ndrei.teslapoweredthingies.machines.itemliquefier

import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidRegistry
import net.ndrei.teslacorelib.utils.equalsIgnoreSize
import net.ndrei.teslapoweredthingies.fluids.MoltenTeslaFluid
import net.ndrei.teslapoweredthingies.items.TeslaPlantSeeds

/**
 * Created by CF on 2017-06-30.
 */
object LiquefierRecipes {
    val VANILLA_STONE_TO_LAVA_RATE = 100
    val recipes = mutableListOf<LiquefierRecipe>()

    fun registerRecipes() {
        recipes.clear() // reset recipes every time this method is called... should only happen once

        // vanilla recipes
        for (b in arrayOf(Blocks.COBBLESTONE, Blocks.STONE, Blocks.STONEBRICK, Blocks.MOSSY_COBBLESTONE, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_STAIRS, Blocks.BRICK_BLOCK, Blocks.BRICK_STAIRS)) {
            recipes.add(LiquefierRecipe(b, FluidRegistry.LAVA, VANILLA_STONE_TO_LAVA_RATE))
        }

        for (b in arrayOf(Blocks.NETHERRACK, Blocks.NETHER_BRICK, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART_BLOCK, Blocks.RED_NETHER_BRICK)) {
            recipes.add(LiquefierRecipe(b, FluidRegistry.LAVA, VANILLA_STONE_TO_LAVA_RATE * 2))
        }

        for (b in arrayOf(Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.FURNACE, Blocks.OBSIDIAN)) {
            recipes.add(LiquefierRecipe(b, FluidRegistry.LAVA, VANILLA_STONE_TO_LAVA_RATE * 4))
        }

        recipes.add(LiquefierRecipe(Items.APPLE, 1, FluidRegistry.WATER, 50))
        recipes.add(LiquefierRecipe(Items.POTATO, 1, FluidRegistry.WATER, 50))

        // tesla thingies recipes
        recipes.add(LiquefierRecipe(TeslaPlantSeeds, 1, MoltenTeslaFluid, 25))
    }

    fun getRecipe(input: ItemStack) = recipes.firstOrNull { it.input.equalsIgnoreSize(input) && (it.input.count <= input.count) }

    fun getRecipesList() = this.recipes.toList()
}
