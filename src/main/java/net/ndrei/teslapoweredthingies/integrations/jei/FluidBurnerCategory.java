package net.ndrei.teslapoweredthingies.integrations.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;
import net.ndrei.teslapoweredthingies.machines.FluidBurnerBlock;
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerCoolantRecipe;
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerFuelRecipe;
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerRecipes;

import java.util.List;

/**
 * Created by CF on 2017-04-14.
 */
@SuppressWarnings("NullableProblems")
public class FluidBurnerCategory extends BaseCategory<FluidBurnerCategory.FluidBurnerRecipeWrapper> {
    public static final String UID = "FluidBurner";

    public static void register(IModRegistry registry, IGuiHelper guiHelper) {
        registry.addRecipeCategories(new FluidBurnerCategory(guiHelper));
        registry.addRecipeCategoryCraftingItem(new ItemStack(FluidBurnerBlock.INSTANCE), UID);

        List<FluidBurnerRecipeWrapper> recipes = Lists.newArrayList();
        for(FluidBurnerFuelRecipe fuel : FluidBurnerRecipes.getFuels()) {
            recipes.add(new FluidBurnerRecipeWrapper(fuel, null));
            for(FluidBurnerCoolantRecipe coolant : FluidBurnerRecipes.getCoolants()) {
                recipes.add(new FluidBurnerRecipeWrapper(fuel, coolant));
            }
        }
        registry.addRecipes(recipes, UID);
    }

    //#region class implementation

    private IDrawable background = null;
    private IDrawable fuelOverlay = null;
    private IDrawable coolantOverlay = null;

    public FluidBurnerCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TeslaThingiesMod.JEI_TEXTURES, 0, 66, 124, 66);
        this.fuelOverlay = guiHelper.createDrawable(TeslaThingiesMod.JEI_TEXTURES, 8, 74, 8, 27);
        this.coolantOverlay = guiHelper.createDrawable(TeslaThingiesMod.JEI_TEXTURES, 20, 74, 8, 27);
    }

    @Override
    public String getUid() {
        return FluidBurnerCategory.UID;
    }

    @Override
    public String getTitle() {
        return FluidBurnerBlock.INSTANCE.getLocalizedName();
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, FluidBurnerRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();

        int capacity = (recipeWrapper.coolant != null)
            ? Math.max(recipeWrapper.fuel.amount, recipeWrapper.coolant.amount)
            : recipeWrapper.fuel.amount;
        fluids.init(0, true, 8, 8, 8, 27, capacity, false, this.fuelOverlay);
        fluids.set(0, ingredients.getInputs(FluidStack.class).get(0));
        if (ingredients.getInputs(FluidStack.class).size() == 2) {
            fluids.init(1, true, 20, 8, 8, 27, capacity, false, this.coolantOverlay);
            fluids.set(1, ingredients.getInputs(FluidStack.class).get(1));
        }
    }

    //#endregion

    public static class FluidBurnerRecipeWrapper extends BlankRecipeWrapper {
        private FluidBurnerFuelRecipe fuel;
        private FluidBurnerCoolantRecipe coolant;

        FluidBurnerRecipeWrapper(FluidBurnerFuelRecipe fuel, FluidBurnerCoolantRecipe coolant) {
            this.fuel = fuel;
            this.coolant = coolant;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            if (this.coolant == null) {
                ingredients.setInput(FluidStack.class, new FluidStack(this.fuel.fluid, this.fuel.amount));
            }
            else {
                ingredients.setInputs(FluidStack.class, Lists.newArrayList(
                        new FluidStack(this.fuel.fluid, this.fuel.amount),
                        new FluidStack(this.coolant.fluid, this.coolant.amount)
                ));
            }
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);

            int ticks = this.fuel.baseTicks;
            if (this.coolant != null)
                ticks = Math.round((float)ticks * this.coolant.timeMultiplier);

            String duration = String.format("%,d ticks", ticks);
            String power = String.format("%,d T", ticks * 80);
            minecraft.fontRenderer.drawString(duration, 36, 12, 0x007F7F);
            minecraft.fontRenderer.drawString(power, 36, 12 + minecraft.fontRenderer.FONT_HEIGHT, 0x007F7F);
        }
    }
}
