package net.ndrei.teslapoweredthingies.integrations.jei

import mezz.jei.api.IGuiHelper
import mezz.jei.api.IModRegistry
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredientRenderer
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.SecondaryOutput
import net.ndrei.teslapoweredthingies.machines.PowderMakerBlock
import net.ndrei.teslapoweredthingies.machines.powdermaker.IPowderMakerRecipe
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRecipes

/**
 * Created by CF on 2017-07-05.
 */
class PowderMakerCategory(guiHelper: IGuiHelper)
    : BaseCategory<PowderMakerCategory.PowderMakerRecipeWrapper>() {

    //#region class implementation

    private val background: IDrawable
    private val slotBackground: IDrawable

    init {
        this.background = guiHelper.createDrawable(TeslaThingiesMod.JEI_TEXTURES, 0, 0, 124, 66)
        this.slotBackground = guiHelper.createDrawable(TeslaThingiesMod.MACHINES_TEXTURES, 6, 6, 18, 18)
    }

    override fun getUid(): String {
        return PowderMakerCategory.UID
    }

    override fun getTitle(): String {
        return PowderMakerBlock.localizedName
    }

    override fun getBackground(): IDrawable {
        return this.background
    }

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: PowderMakerRecipeWrapper, ingredients: IIngredients) {
        val stacks = recipeLayout.itemStacks

        stacks.init(0, true, 6, 6)
        stacks.set(0, ingredients.getInputs(ItemStack::class.java)[0])

        val recipe = recipeWrapper.recipe
        val outputs = recipe.getOutputs()
        if (outputs.isNotEmpty()) {
            var index = 1
            for (so in outputs) {
                stacks.init(index, false, object : IIngredientRenderer<ItemStack> {
//                    private lateinit var so: IRecipeOutput

                    @SideOnly(Side.CLIENT)
                    override fun render(minecraft: Minecraft, xPosition: Int, yPosition: Int, ingredient: ItemStack?) {
                        minecraft.renderItem.renderItemIntoGUI(ingredient!!, xPosition, yPosition)
                        minecraft.renderItem.renderItemOverlayIntoGUI(minecraft.fontRenderer, ingredient, xPosition + 1, yPosition + 1, null)

                        val percent = when(so) {
                            is SecondaryOutput -> "${Math.round(so.chance * 100f)}%"
                            else -> "100%"
                        }
                        GlStateManager.pushMatrix()
                        GlStateManager.translate((xPosition + 8).toFloat(), (yPosition + 20).toFloat(), 0f)
                        GlStateManager.scale(.75f, .75f, 1f)
                        minecraft.fontRenderer.drawString(percent,
                                -minecraft.fontRenderer.getStringWidth(percent) / 2,
                                0, 0x424242)
                        GlStateManager.popMatrix()
                    }

                    @SideOnly(Side.CLIENT)
                    override fun getTooltip(minecraft: Minecraft, ingredient: ItemStack, tooltipFlag: ITooltipFlag): List<String> {
                        return ingredient.getTooltip(minecraft.player, tooltipFlag)
                    }

                    override fun getFontRenderer(minecraft: Minecraft, ingredient: ItemStack): FontRenderer {
                        return minecraft.fontRenderer
                    }

//                    internal fun init(so: IRecipeOutput): IIngredientRenderer<ItemStack> {
//                        this.so = so
//                        return this
//                    }
                }/*.init(so)*/, 6 + (index - 1) * 18, 45, 18, 18, 1, 1)
                stacks.set(index, so.getOutput())
                stacks.setBackground(index, this.slotBackground)
                index++
            }
        }
    }

    //#endregion

    class PowderMakerRecipeWrapper(val recipe: IPowderMakerRecipe)
        : IRecipeWrapper {

        override fun getIngredients(ingredients: IIngredients) {
            ingredients.setInputs(ItemStack::class.java, this.recipe.getPossibleInputs())
            ingredients.setOutputLists(ItemStack::class.java, this.recipe.getPossibleOutputs())
        }

        @SideOnly(Side.CLIENT)
        override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY)
        }
    }

    companion object {
        val UID = "PowderMaker"

        fun register(registry: IModRegistry, guiHelper: IGuiHelper) {
            registry.addRecipeCategories(PowderMakerCategory(guiHelper))
            registry.addRecipeCategoryCraftingItem(ItemStack(PowderMakerBlock), UID)
            registry.handleRecipes(IPowderMakerRecipe::class.java, { PowderMakerRecipeWrapper(it) }, UID)
            registry.addRecipes(PowderMakerRecipes.getRecipes(), UID)
        }
    }
}