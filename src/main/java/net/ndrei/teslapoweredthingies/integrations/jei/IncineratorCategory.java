package net.ndrei.teslapoweredthingies.integrations.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;
import net.ndrei.teslapoweredthingies.common.BlocksRegistry;
import net.ndrei.teslapoweredthingies.common.SecondaryOutput;
import net.ndrei.teslapoweredthingies.machines.incinerator.IncineratorRecipe;
import net.ndrei.teslapoweredthingies.machines.incinerator.IncineratorRecipes;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by CF on 2017-04-14.
 */
@SuppressWarnings("NullableProblems")
public class IncineratorCategory extends BlankRecipeCategory<IncineratorCategory.IncineratorRecipeWrapper> {
    public static final String UID = "Incinerator";

    public static void register(IModRegistry registry, IGuiHelper guiHelper) {
        registry.addRecipeCategories(new IncineratorCategory(guiHelper));
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlocksRegistry.incinerator), UID);
        registry.handleRecipes(IncineratorRecipe.class, IncineratorRecipeWrapper::new, UID);
        registry.addRecipes(IncineratorRecipes.getRecipes(), UID);
    }

    //#region class implementation

    private IDrawable background = null;
    private IDrawable slotBackground = null;

    public IncineratorCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TeslaThingiesMod.JEI_TEXTURES, 0, 0, 124, 66);
        this.slotBackground = guiHelper.createDrawable(TeslaThingiesMod.MACHINES_TEXTURES, 6, 6, 18, 18);
    }

    @Override
    public String getUid() {
        return IncineratorCategory.UID;
    }

    @Override
    public String getTitle() {
        return BlocksRegistry.incinerator.getLocalizedName();
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IncineratorRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        stacks.init(0, true, 6, 6);
        stacks.set(0, ingredients.getInputs(ItemStack.class).get(0));

        IncineratorRecipe recipe = recipeWrapper.recipe;
        if ((recipe.secondaryOutputs != null) && (recipe.secondaryOutputs.length > 0)) {
            int index = 1;
            for (SecondaryOutput so : recipe.secondaryOutputs) {
                stacks.init(index, false, new IIngredientRenderer<ItemStack>() {
                    private SecondaryOutput so;

                    @Override
                    public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable ItemStack ingredient) {
                        minecraft.getRenderItem().renderItemIntoGUI(ingredient, xPosition, yPosition);
                        minecraft.getRenderItem().renderItemOverlayIntoGUI(minecraft.fontRenderer, ingredient, xPosition + 1, yPosition + 1, null);

                        String percent = "" + Math.round(so.chance * 100.0f) + "%";
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(xPosition + 8, yPosition + 20, 0);
                        GlStateManager.scale(.75f, .75f, 1f);
                        minecraft.fontRenderer.drawString(percent,
                                 - minecraft.fontRenderer.getStringWidth(percent) / 2,
                                0, 0x424242);
                        GlStateManager.popMatrix();
                    }

                    @Override
                    public List<String> getTooltip(Minecraft minecraft, ItemStack ingredient) {
                        return ingredient.getTooltip(minecraft.player, minecraft.gameSettings.advancedItemTooltips);
                    }

                    @Override
                    public FontRenderer getFontRenderer(Minecraft minecraft, ItemStack ingredient) {
                        return minecraft.fontRenderer;
                    }

                    IIngredientRenderer<ItemStack> init(SecondaryOutput so) {
                        this.so = so;
                        return this;
                    }
                }.init(so), 6 + (index - 1) * 18, 45, 18, 18, 1, 1);
                stacks.set(index, so.stack);
                stacks.setBackground(index, this.slotBackground);
                index++;
            }
        }
    }

    //#endregion

    public static class IncineratorRecipeWrapper extends BlankRecipeWrapper {
        private IncineratorRecipe recipe;

        public IncineratorRecipeWrapper(IncineratorRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInput(ItemStack.class, this.recipe.input);
            if ((this.recipe.secondaryOutputs != null) && (this.recipe.secondaryOutputs.length > 0)) {
                List<ItemStack> secondary = Lists.newArrayList();
                for (SecondaryOutput so : this.recipe.secondaryOutputs) {
                    secondary.add(so.stack.copy());
                }
                ingredients.setOutputs(ItemStack.class, secondary);
            }
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);

            String power = String.format("%,d T", this.recipe.power);
            minecraft.fontRenderer.drawString(power, 44, 15 - minecraft.fontRenderer.FONT_HEIGHT / 2, 0x007F7F);
        }
    }
}
