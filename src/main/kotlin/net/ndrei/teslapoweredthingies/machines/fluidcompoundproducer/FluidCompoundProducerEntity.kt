package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.EnumDyeColor
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.FluidTankType
import net.ndrei.teslapoweredthingies.client.ThingiesTexture
import net.ndrei.teslapoweredthingies.common.gui.FluidDisplayPiece
import net.ndrei.teslapoweredthingies.common.gui.IMultiTankMachine
import net.ndrei.teslapoweredthingies.common.gui.TankInfo
import net.ndrei.teslapoweredthingies.machines.BaseThingyMachine
import net.ndrei.teslapoweredthingies.render.DualTankEntityRenderer

/**
 * Created by CF on 2017-07-13.
 */
class FluidCompoundProducerEntity
    : BaseThingyMachine(FluidCompoundProducerEntity::class.java.name.hashCode()), IMultiTankMachine {

    private lateinit var inputFluidA: IFluidTank
    private lateinit var inputFluidB: IFluidTank
    private lateinit var output: IFluidTank

    private var currentFluidA: FluidStack? = null
    private var currentFluidB: FluidStack? = null
    private var currentOutput: FluidStack? = null

    //#region inventory and gui methods

    override fun initializeInventories() {
        super.initializeInventories()

        this.inputFluidA = this.addSimpleFluidTank(5000, "Fluid Tank A", EnumDyeColor.BLUE,
                79, 25, FluidTankType.INPUT, {
            FluidCompoundProducerRegistry.hasRecipe(it, this@FluidCompoundProducerEntity.inputFluidB.fluid)
        })

        this.inputFluidB = this.addSimpleFluidTank(5000, "Fluid Tank B", EnumDyeColor.RED,
                97, 25, FluidTankType.INPUT, {
            FluidCompoundProducerRegistry.hasRecipe(it, this@FluidCompoundProducerEntity.inputFluidA.fluid)
        })

        this.output = this.addSimpleFluidTank(5000, "Output Fluid Tank", EnumDyeColor.WHITE,
                142, 25, FluidTankType.OUTPUT)
    }

    override val fluidItemsBoundingBox: BoundingRectangle
        get() = BoundingRectangle(61, 25, 18, 54)

    override fun addFluidItemsBackground(pieces: MutableList<IGuiContainerPiece>, box: BoundingRectangle) {
        pieces.add(BasicRenderedGuiPiece(box.left, box.top, 18, 54,
                ThingiesTexture.MACHINES_TEXTURES.resource, 6, 44))
    }

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = super.getGuiContainerPieces(container)

        list.add(BasicRenderedGuiPiece(115, 32, 27, 40,
                ThingiesTexture.MACHINES_TEXTURES.resource, 66, 86))

        list.add(FluidDisplayPiece(117, 42, 11, 20, { this.currentFluidA }))
        list.add(FluidDisplayPiece(126, 42, 11, 20, { this.currentFluidB }))

        // TODO: slowly "transform" the fluid based on work energy fill rate
        // list.add(FluidDisplayPiece(117, 54, 20, 8, { this.currentOutput ?: FluidStack(MoltenTeslaFluid, 100) }))

        return list
    }

    override fun getRenderers(): MutableList<TileEntitySpecialRenderer<in TileEntity>> {
        val list = super.getRenderers()
        list.add(DualTankEntityRenderer)
        return list
    }

    override fun getTanks()
            = listOf(
            TankInfo(4.0, 6.0, this.inputFluidA.fluid, this.inputFluidA.capacity),
            TankInfo(13.0, 12.0, this.output.fluid, this.output.capacity),
            TankInfo(22.0, 6.0, this.inputFluidB.fluid, this.inputFluidB.capacity)
    )

    //#endregion
    //#region storage

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        this.currentFluidA = if (compound.hasKey("current_fluid_a", Constants.NBT.TAG_COMPOUND))
            FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("current_fluid_a"))
        else
            null

        this.currentFluidB = if (compound.hasKey("current_fluid_b", Constants.NBT.TAG_COMPOUND))
            FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("current_fluid_b"))
        else
            null

        this.currentOutput = if (compound.hasKey("current_fluid_out", Constants.NBT.TAG_COMPOUND))
            FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("current_fluid_out"))
        else
            null
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        this.currentFluidA?.also { compound.setTag("current_fluid_a", it.writeToNBT(NBTTagCompound())) }
        this.currentFluidB?.also { compound.setTag("current_fluid_b", it.writeToNBT(NBTTagCompound())) }
        this.currentOutput?.also { compound.setTag("current_fluid_out", it.writeToNBT(NBTTagCompound())) }

        return super.writeToNBT(compound)
    }

    //#endregion

    override fun processImmediateInventories() {
        super.processImmediateInventories()

        if (this.currentFluidA == null) {
            // assume both fluids are empty :)
            val fluidA = this.inputFluidA.fluid
            val fluidB = this.inputFluidB.fluid
            if ((fluidA != null) && (fluidA.amount > 0) && (fluidB != null) && (fluidB.amount > 0)) {
                val recipe = FluidCompoundProducerRegistry.findRecipe(fluidA, fluidB)
                if (recipe != null) {
                    val drainedA = this.inputFluidA.drain(recipe.inputA.amount, false)
                    if ((drainedA?.amount == recipe.inputA.amount) && drainedA.isFluidEqual(recipe.inputA)) {
                        val drainedB = this.inputFluidB.drain(recipe.inputB.amount, false)
                        if ((drainedB?.amount == recipe.inputB.amount) && drainedB.isFluidEqual(recipe.inputB)) {
                            this.inputFluidA.drain(recipe.inputA.amount, true)
                            this.inputFluidB.drain(recipe.inputB.amount, true)
                            this.currentFluidA = drainedA
                            this.currentFluidB = drainedB
                            this.currentOutput = recipe.output.copy()
                        }
                    }
                }
            }
        }
    }

    override fun performWork(): Float {
        var result = 0.0f
        if ((this.currentFluidA != null) && (this.currentFluidB != null) && (this.currentOutput != null)) {
            val output = this.currentOutput!!
            val filled = this.output.fill(output, false)
            if (filled == output.amount) {
                this.output.fill(output, true)
                this.currentFluidA = null
                this.currentFluidB = null
                this.currentOutput = null
                result = 1.0f
            }
        }
        return result
    }
}
