package net.ndrei.teslapoweredthingies.machines

import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.tileentities.ElectricGenerator
import net.ndrei.teslapoweredthingies.gui.OpenJEICategoryPiece

/**
 * Created by CF on 2017-07-06.
 */
abstract class BaseThingyGenerator(typeId: Int) : ElectricGenerator(typeId) {
    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = super.getGuiContainerPieces(container)

        list.add(OpenJEICategoryPiece(this.getBlockType()))

        return list
    }
}