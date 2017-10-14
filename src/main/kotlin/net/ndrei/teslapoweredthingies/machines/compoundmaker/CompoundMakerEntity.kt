package net.ndrei.teslapoweredthingies.machines.compoundmaker

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.EnumDyeColor
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.FluidTankType
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.gui.IMultiTankMachine
import net.ndrei.teslapoweredthingies.gui.TankInfo
import net.ndrei.teslapoweredthingies.machines.BaseThingyMachine
import net.ndrei.teslapoweredthingies.render.DualTankEntityRenderer

class CompoundMakerEntity
    : BaseThingyMachine(CompoundMakerEntity::class.java.name.hashCode()), IMultiTankMachine {

    private lateinit var leftFluid: IFluidTank
    private lateinit var rightFluid: IFluidTank
    private lateinit var topInventory: IItemHandlerModifiable
    private lateinit var bottomInventory: IItemHandlerModifiable
    private lateinit var outputInventory: IItemHandlerModifiable

    //#region inventory & gui

    override fun initializeInventories() {
        this.leftFluid = this.addSimpleFluidTank(5000, "Left Tank", EnumDyeColor.BLUE, 52, 25,
            FluidTankType.INPUT,
            { fluid -> CompoundMakerRegistry.acceptsLeft(fluid) })

        this.topInventory = this.addSimpleInventory(3, "input_top", EnumDyeColor.GREEN, "Top Inputs",
            BoundingRectangle.slots(70, 22, 3, 1),
            { stack, _ -> CompoundMakerRegistry.acceptsTop(stack)},
            { _, _ -> false }, true)


        this.bottomInventory = this.addSimpleInventory(3, "input_bottom", EnumDyeColor.BROWN, "Bottom Inputs",
            BoundingRectangle.slots(70, 64, 3, 1),
            { stack, _ -> CompoundMakerRegistry.acceptsBottom(stack) },
            { _, _ -> false }, true)

        this.rightFluid = this.addSimpleFluidTank(5000, "Right Tank", EnumDyeColor.PURPLE, 124, 25,
            FluidTankType.INPUT,
            { fluid -> CompoundMakerRegistry.acceptsRight(fluid) })

        this.outputInventory = this.addSimpleInventory(1, "output", EnumDyeColor.ORANGE, "Output",
            BoundingRectangle.slots(88, 43, 1, 1),
            { _, _ -> false },
            { _, _ -> true }, true)

        super.initializeInventories()
    }

    override fun shouldAddFluidItemsInventory() = false

    override fun getRenderers() = super.getRenderers().also {
        it.add(DualTankEntityRenderer)
    }

    override fun getTanks() =
        listOf(
            TankInfo(4.0, 8.0, this.leftFluid.fluid, this.leftFluid.capacity),
            TankInfo(22.0, 8.0, this.rightFluid.fluid, this.rightFluid.capacity)
        )

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>) =
        super.getGuiContainerPieces(container).also {
            it.add(BasicRenderedGuiPiece(70, 40, 54, 24,
                Textures.MACHINES_TEXTURES.resource, 5, 105))
        }

    //#endregion

    override fun performWork(): Float {
        return 0.0f
    }
}