package net.ndrei.teslapoweredthingies.machines.compoundmaker

import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.ButtonPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslapoweredthingies.client.ThingiesTexture

class CompoundRecipeButtonPiece(private val entity: CompoundMakerEntity, private val direction: Direction, left: Int = 151, top: Int = 25)
    : ButtonPiece(left, top, 14, 7) {
    enum class Direction { UP, DOWN }

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY)

        CompoundMakerIcon.BUTTON_BACKGROUND.drawCentered(container, this)
        if (this.isInside(container, mouseX, mouseY) && this.isEnabled) {
            ButtonPiece.drawHoverArea(container, this, 1)
        }
    }

    override val isEnabled : Boolean
        get() {
            if (this.entity.hasCurrentRecipe) {
                return false
            }

            val index = this.entity.selectedRecipeIndex
            val recipes = this.entity.availableRecipes

            return !when (this.direction) {
                Direction.UP -> ((index == 0) || recipes.isEmpty())
                Direction.DOWN -> (recipes.isEmpty() || (index >= (recipes.size - 1)))
            }
        }

    override fun renderState(container: BasicTeslaGuiContainer<*>, over: Boolean, box: BoundingRectangle) {
        stateMap[this.direction to this.isEnabled]?.drawCentered(container, this, true)
    }

    override fun clicked() {
        when (this.direction) {
            Direction.UP -> this.entity.selectedRecipeIndex--
            Direction.DOWN -> this.entity.selectedRecipeIndex++
        }
    }

    companion object {
        private val stateMap = mapOf(
            (Direction.UP to true) to CompoundMakerIcon.RECIPE_BUTTON_UP,
            (Direction.UP to false) to CompoundMakerIcon.RECIPE_BUTTON_UP_GRAYED,
            (Direction.DOWN to true) to CompoundMakerIcon.RECIPE_BUTTON_DOWN,
            (Direction.DOWN to false) to CompoundMakerIcon.RECIPE_BUTTON_DOWN_GRAYED
        )
    }
}
