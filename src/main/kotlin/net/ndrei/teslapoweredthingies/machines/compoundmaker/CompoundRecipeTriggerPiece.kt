package net.ndrei.teslapoweredthingies.machines.compoundmaker

import net.minecraft.util.text.TextFormatting
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.GuiIcon
import net.ndrei.teslacorelib.gui.ToggleButtonPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslapoweredthingies.integrations.GUI_COMPOUND_MAKER
import net.ndrei.teslapoweredthingies.integrations.localize

class CompoundRecipeTriggerPiece(private val entity: CompoundMakerEntity, left: Int = 151, top: Int = 65)
    : ToggleButtonPiece(left, top, 14, 14, 1) {

    override val currentState: Int
        get() = this.entity.selectedRecipeMode.ordinal

    override fun getStateToolTip(state: Int): List<String> {
        return listOf(
            localize(GUI_COMPOUND_MAKER, CompoundMakerEntity.RecipeRunType.byOrdinal(state).langKey),
            if (!this.isEnabled)
                localize(GUI_COMPOUND_MAKER, "no_recipe", { +TextFormatting.DARK_GRAY })
            else
                localize(GUI_COMPOUND_MAKER, "toggle", { +TextFormatting.GRAY })
        )
    }

    override val isEnabled: Boolean
        get() = (this.entity.hasCurrentRecipe && (this.entity.selectedRecipeMode != CompoundMakerEntity.RecipeRunType.PAUSED))
            || this.entity.availableRecipes.isNotEmpty()

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        GuiIcon.SMALL_BUTTON.drawCentered(container, this)
        super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY)
    }

    override fun renderState(container: BasicTeslaGuiContainer<*>, state: Int, box: BoundingRectangle) {
        when (state) {
            CompoundMakerEntity.RecipeRunType.PAUSED.ordinal -> CompoundMakerIcon.TRIGGER_PAUSED.drawCentered(container, this, true)
            CompoundMakerEntity.RecipeRunType.SINGLE.ordinal -> CompoundMakerIcon.TRIGGER_ONE.drawCentered(container, this, true)
            CompoundMakerEntity.RecipeRunType.ALL.ordinal -> CompoundMakerIcon.TRIGGER_ALL.drawCentered(container, this, true)
            CompoundMakerEntity.RecipeRunType.LOCK.ordinal -> GuiIcon.LOCK_CLOSE.drawCentered(container, this, true)
        }
    }

    override fun clicked() {
        this.entity.selectedRecipeMode = this.entity.selectedRecipeMode.next
    }
}
