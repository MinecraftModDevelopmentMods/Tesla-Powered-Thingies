package net.ndrei.teslapoweredthingies.machines.itemliquefier;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.List;

/**
 * Created by CF on 2017-04-13.
 */
public class LiquefierRecipes {
    private static final int VANILLA_STONE_TO_LAVA_RATE = 5;
    private static List<LiquefierRecipe> recipes;

    public static void registerRecipes() {
        LiquefierRecipes.recipes = Lists.newArrayList();

        // vanilla recipes
        for(Block b : new Block[] {
                Blocks.COBBLESTONE,
                Blocks.STONE,
                Blocks.STONEBRICK,
                Blocks.MOSSY_COBBLESTONE,
                Blocks.STONE_BRICK_STAIRS,
                Blocks.STONE_STAIRS,
                Blocks.BRICK_BLOCK,
                Blocks.BRICK_STAIRS
        }) {
            recipes.add(new LiquefierRecipe(b, FluidRegistry.LAVA, VANILLA_STONE_TO_LAVA_RATE));
        }

        for(Block b : new Block[] {
                Blocks.NETHERRACK,
                Blocks.NETHER_BRICK,
                Blocks.NETHER_BRICK_STAIRS,
                Blocks.NETHER_WART_BLOCK,
                Blocks.RED_NETHER_BRICK
        }) {
            recipes.add(new LiquefierRecipe(b, FluidRegistry.LAVA, VANILLA_STONE_TO_LAVA_RATE * 2));
        }

        for(Block b : new Block[] {
                Blocks.PISTON,
                Blocks.STICKY_PISTON,
                Blocks.FURNACE,
                Blocks.OBSIDIAN
        }) {
            recipes.add(new LiquefierRecipe(b, FluidRegistry.LAVA, VANILLA_STONE_TO_LAVA_RATE * 4));
        }

        recipes.add(new LiquefierRecipe(Items.APPLE, 1, FluidRegistry.WATER, 100));
    }

    public static LiquefierRecipe getRecipe(Item item) {
        if (recipes != null) {
            for (LiquefierRecipe recipe : recipes) {
                if (recipe.input == item) {
                    return recipe;
                }
            }
        }
        return null;
    }
}
