package net.ndrei.teslapoweredthingies.machines.incinerator;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.ndrei.teslapoweredthingies.common.ItemsRegistry;
import net.ndrei.teslapoweredthingies.common.SecondaryOutput;

import java.util.List;

/**
 * Created by CF on 2017-01-07.
 */
public class IncineratorRecipes {
    private static final long VANILLA_BURN_TO_POWER_RATE = 10;
    private static List<IncineratorRecipe> recipes;

    public static void registerRecipes() {
        IncineratorRecipes.recipes = Lists.newArrayList();

        // vanilla recipes
        registerVanillaRecipe(Items.COAL, new SecondaryOutput(.02f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.COAL_BLOCK, new SecondaryOutput(.05f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.PLANKS, new SecondaryOutput(.10f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.LOG, new SecondaryOutput(.10f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.LOG2, new SecondaryOutput(.10f, ItemsRegistry.ASH));
    }

    private static void registerVanillaRecipe(Block block, SecondaryOutput secondary) {
        IncineratorRecipes.registerVanillaRecipe(Item.getItemFromBlock(block), secondary);
    }

    private static void registerVanillaRecipe(Item item, SecondaryOutput secondary) {
        int burnTime = TileEntityFurnace.getItemBurnTime(new ItemStack(item));
        if (burnTime > 0) {
            long power = (long)burnTime * VANILLA_BURN_TO_POWER_RATE;
            IncineratorRecipes.recipes.add(new IncineratorRecipe(item, power, secondary));
        }
    }

    public static boolean isFuel(ItemStack input) {
        if (input.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            return false; // NO BUCKETS!!
        }

        if (TileEntityFurnace.isItemFuel(new ItemStack(input.getItem()))) {
            return true;
        }

        if ((IncineratorRecipes.recipes != null)) {
            for (IncineratorRecipe recipe : IncineratorRecipes.recipes) {
                if (recipe.input.equals(input.getItem())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static long getPower(ItemStack input) {
        if ((IncineratorRecipes.recipes != null)) {
            for (IncineratorRecipe recipe : IncineratorRecipes.recipes) {
                if (recipe.input.equals(input.getItem())) {
                    return recipe.power;
                }
            }
        }

        return isFuel(input)
                ? VANILLA_BURN_TO_POWER_RATE * TileEntityFurnace.getItemBurnTime(input)
                : 0;
    }

    public static SecondaryOutput[] getSecondaryOutputs(Item input) {
        if ((IncineratorRecipes.recipes != null)) {
            for (IncineratorRecipe recipe : IncineratorRecipes.recipes) {
                if (recipe.input.equals(input)) {
                    return recipe.secondaryOutputs;
                }
            }
        }
        return null;
    }
}
