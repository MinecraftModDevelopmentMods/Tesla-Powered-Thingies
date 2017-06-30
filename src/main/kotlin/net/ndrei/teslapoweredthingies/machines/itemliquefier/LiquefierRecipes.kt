package net.ndrei.teslapoweredthingies.machines.itemliquefier

import com.google.common.collect.Lists
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraftforge.fluids.FluidRegistry

/**
 * Created by CF on 2017-06-30.
 */
object LiquefierRecipes {
    private val VANILLA_STONE_TO_LAVA_RATE = 5
    private var recipes: MutableList<LiquefierRecipe>? = null

    fun registerRecipes() {
        LiquefierRecipes.recipes = Lists.newArrayList<LiquefierRecipe>()

        // vanilla recipes
        for (b in arrayOf(Blocks.COBBLESTONE, Blocks.STONE, Blocks.STONEBRICK, Blocks.MOSSY_COBBLESTONE, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_STAIRS, Blocks.BRICK_BLOCK, Blocks.BRICK_STAIRS)) {
            recipes!!.add(LiquefierRecipe(b, FluidRegistry.LAVA, VANILLA_STONE_TO_LAVA_RATE))
        }

        for (b in arrayOf(Blocks.NETHERRACK, Blocks.NETHER_BRICK, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART_BLOCK, Blocks.RED_NETHER_BRICK)) {
            recipes!!.add(LiquefierRecipe(b, FluidRegistry.LAVA, VANILLA_STONE_TO_LAVA_RATE * 2))
        }

        for (b in arrayOf(Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.FURNACE, Blocks.OBSIDIAN)) {
            recipes!!.add(LiquefierRecipe(b, FluidRegistry.LAVA, VANILLA_STONE_TO_LAVA_RATE * 4))
        }

        recipes!!.add(LiquefierRecipe(Items.APPLE, 1, FluidRegistry.WATER, 50))
        recipes!!.add(LiquefierRecipe(Items.POTATO, 1, FluidRegistry.WATER, 50))
    }

    fun getRecipe(item: Item): LiquefierRecipe? {
        if (recipes != null) {
            for (recipe in recipes!!) {
                if (recipe.input === item) {
                    return recipe
                }
            }
        }
        return null
    }
}
