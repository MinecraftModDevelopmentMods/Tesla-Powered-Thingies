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
        registerVanillaRecipe(Blocks.LOG, new SecondaryOutput(.15f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.LOG2, new SecondaryOutput(.15f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.WOOL, new SecondaryOutput(.01f, ItemsRegistry.ASH));

        registerVanillaRecipe(Blocks.SAPLING, new SecondaryOutput(.15f, ItemsRegistry.ASH));

        registerVanillaRecipe(Blocks.ACACIA_FENCE, new SecondaryOutput(.05f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.BIRCH_FENCE, new SecondaryOutput(.05f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.JUNGLE_FENCE, new SecondaryOutput(.05f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.OAK_FENCE, new SecondaryOutput(.05f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.SPRUCE_FENCE, new SecondaryOutput(.05f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.DARK_OAK_FENCE, new SecondaryOutput(.05f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.ACACIA_FENCE_GATE, new SecondaryOutput(.05f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.BIRCH_FENCE_GATE, new SecondaryOutput(.05f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.JUNGLE_FENCE_GATE, new SecondaryOutput(.05f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.OAK_FENCE_GATE, new SecondaryOutput(.05f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.SPRUCE_FENCE_GATE, new SecondaryOutput(.05f, ItemsRegistry.ASH));
        registerVanillaRecipe(Blocks.DARK_OAK_FENCE_GATE, new SecondaryOutput(.05f, ItemsRegistry.ASH));

        registerVanillaRecipe(Items.STICK, new SecondaryOutput(.01f, ItemsRegistry.ASH));
        registerVanillaRecipe(Items.WOODEN_AXE, new SecondaryOutput(.03f, ItemsRegistry.ASH));
        registerVanillaRecipe(Items.WOODEN_HOE, new SecondaryOutput(.03f, ItemsRegistry.ASH));
        registerVanillaRecipe(Items.WOODEN_PICKAXE, new SecondaryOutput(.03f, ItemsRegistry.ASH));
        registerVanillaRecipe(Items.WOODEN_SHOVEL, new SecondaryOutput(.03f, ItemsRegistry.ASH));
        registerVanillaRecipe(Items.WOODEN_SWORD, new SecondaryOutput(.03f, ItemsRegistry.ASH));
    }

    private static void registerVanillaRecipe(Block block, SecondaryOutput secondary) {
        IncineratorRecipes.registerVanillaRecipe(Item.getItemFromBlock(block), secondary);
    }

    private static void registerVanillaRecipe(Item item, SecondaryOutput secondary) {
//        NonNullList<ItemStack> list = NonNullList.create();
//        if (item.getHasSubtypes()) {
//            item.getSubItems(item, null, list);
//        }
//        else {
//            list.add(new ItemStack(item));
//        }
//
//        for(ItemStack stack : list){
//            registerVanillaRecipe(stack, secondary);
//        }
        registerVanillaRecipe(new ItemStack(item), secondary);
    }

    private static void registerVanillaRecipe(ItemStack stack, SecondaryOutput secondary) {
        int burnTime = TileEntityFurnace.getItemBurnTime(stack);
        if (burnTime > 0) {
            long power = (long) burnTime * VANILLA_BURN_TO_POWER_RATE;
            IncineratorRecipes.recipes.add(new IncineratorRecipe(stack, power, secondary));
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
                if (recipe.input.isItemEqualIgnoreDurability(input)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static long getPower(ItemStack input) {
        if ((IncineratorRecipes.recipes != null)) {
            for (IncineratorRecipe recipe : IncineratorRecipes.recipes) {
                if (recipe.input.isItemEqualIgnoreDurability(input)) {
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
            ItemStack testStack = new ItemStack(input);
            for (IncineratorRecipe recipe : IncineratorRecipes.recipes) {
                if (recipe.input.isItemEqualIgnoreDurability(testStack)) {
                    return recipe.secondaryOutputs;
                }
            }
        }
        return null;
    }

    public static List<IncineratorRecipe> getRecipes() {
        return IncineratorRecipes.recipes;
    }
}
