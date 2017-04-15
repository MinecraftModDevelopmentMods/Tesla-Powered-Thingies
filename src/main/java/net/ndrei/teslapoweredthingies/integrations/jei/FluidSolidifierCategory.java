package net.ndrei.teslapoweredthingies.integrations.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;
import net.ndrei.teslapoweredthingies.common.BlocksRegistry;
import net.ndrei.teslapoweredthingies.machines.fluidsolidifier.FluidSolidifierResult;

/**
 * Created by CF on 2017-04-14.
 */
@SuppressWarnings("NullableProblems")
public class FluidSolidifierCategory extends BlankRecipeCategory<FluidSolidifierCategory.FluidSolidifierRecipeWrapper> {
    public static final String UID = "FluidSolidifier";

    public static void register(IModRegistry registry, IGuiHelper guiHelper) {
        registry.addRecipeCategories(new FluidSolidifierCategory(guiHelper));
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlocksRegistry.fluidSolidifier), UID);
        registry.handleRecipes(FluidSolidifierResult.class, FluidSolidifierCategory.FluidSolidifierRecipeWrapper::new, UID);
        registry.addRecipes(Lists.newArrayList(FluidSolidifierResult.VALUES), UID);
    }

    //#region class implementation

    private IDrawable background = null;
    private IDrawable lavaOverlay = null;
    private IDrawable waterOverlay = null;

    public FluidSolidifierCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TeslaThingiesMod.JEI_TEXTURES, 0, 132, 124, 66);
        this.lavaOverlay = guiHelper.createDrawable(TeslaThingiesMod.JEI_TEXTURES, 8, 147, 8, 27);
        this.waterOverlay = guiHelper.createDrawable(TeslaThingiesMod.JEI_TEXTURES, 20, 147, 8, 27);
    }

    @Override
    public String getUid() {
        return FluidSolidifierCategory.UID;
    }

    @Override
    public String getTitle() {
        return BlocksRegistry.fluidSolidifier.getLocalizedName();
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, FluidSolidifierRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();

        fluids.init(0, true, 8, 15, 8, 27, recipeWrapper.recipe.lavaMbMin, true, this.lavaOverlay);
        fluids.set(0, ingredients.getInputs(FluidStack.class).get(0));
        fluids.init(1, true, 20, 15, 8, 27, recipeWrapper.recipe.waterMbMin, true, this.waterOverlay);
        fluids.set(1, ingredients.getInputs(FluidStack.class).get(1));

        fluids.init(2, true, 43, 15, 8, 27, recipeWrapper.recipe.lavaMbMin, true, this.lavaOverlay);
        fluids.set(2, new FluidStack(FluidRegistry.LAVA, recipeWrapper.recipe.lavaMbConsumed));
        fluids.init(3, true, 55, 15, 8, 27, recipeWrapper.recipe.waterMbMin, true, this.waterOverlay);
        fluids.set(3, new FluidStack(FluidRegistry.WATER, recipeWrapper.recipe.waterMbConsumed));

        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        stacks.init(0, false, 77, 20);
        stacks.set(0, recipeWrapper.recipe.resultStack);
    }

    //#endregion

    public static class FluidSolidifierRecipeWrapper extends BlankRecipeWrapper {
        private FluidSolidifierResult recipe;

        FluidSolidifierRecipeWrapper(FluidSolidifierResult recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputs(FluidStack.class, Lists.newArrayList(
                    new FluidStack(FluidRegistry.LAVA, this.recipe.lavaMbMin),
                    new FluidStack(FluidRegistry.WATER, this.recipe.waterMbMin)
            ));
            ingredients.setOutput(ItemStack.class, this.recipe.resultStack);
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);

            String required  = "required";
            String consumed = "consumed";
            minecraft.fontRenderer.drawString(required, 18 - minecraft.fontRenderer.getStringWidth(required) / 2, 8 - minecraft.fontRenderer.FONT_HEIGHT, 0x424242);
            minecraft.fontRenderer.drawString(consumed, 54 - minecraft.fontRenderer.getStringWidth(consumed) / 2, 47, 0x424242);
        }
    }
}
