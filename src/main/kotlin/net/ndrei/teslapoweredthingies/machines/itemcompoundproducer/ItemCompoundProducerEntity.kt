package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.FluidTankPiece
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.FluidTank
import net.ndrei.teslacorelib.inventory.LockableItemHandler
import net.ndrei.teslacorelib.tileentities.ElectricMachine
import net.ndrei.teslacorelib.utils.insertItems
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.gui.*
import net.ndrei.teslapoweredthingies.render.DualTankEntityRenderer

/**
 * Created by CF on 2017-07-13.
 */
class ItemCompoundProducerEntity
    : ElectricMachine(ItemCompoundProducerEntity::class.java.name.hashCode()), IMultiTankMachine {

    private lateinit var inputItems: LockableItemHandler
    private lateinit var inputFluid: IFluidTank
    private lateinit var outputs: ItemStackHandler

    private var currentStack: ItemStack = ItemStack.EMPTY
    private var currentFluid: FluidStack? = null

    //#region inventory and gui methods

    override fun initializeInventories() {
        super.initializeInventories()

        this.inputItems = object: LockableItemHandler(3) {
            override fun onContentsChanged(slot: Int) {
                this@ItemCompoundProducerEntity.markDirty()
            }
        }
        this.addInventory(object : ColoredItemHandler(this.inputItems, EnumDyeColor.GREEN, "Input Items", BoundingRectangle(88, 25, 18, 54)) {
            override fun canExtractItem(slot: Int) = false

            override fun canInsertItem(slot: Int, stack: ItemStack)
                    = super.canInsertItem(slot, stack) && !stack.isEmpty && ItemCompoundProducerRecipes.hasRecipe(stack)
        })
        this.addInventoryToStorage(this.inputItems, "inv_inputs")

        this.inputFluid = object: FluidTank(5000) {
            override fun canFillFluidType(fluid: FluidStack?)
                = if (fluid != null) ItemCompoundProducerRecipes.hasRecipe(fluid) else false

            override fun onContentsChanged() {
                this@ItemCompoundProducerEntity.markDirty()
            }
        }
        super.addFluidTank(this.inputFluid, EnumDyeColor.BLUE, "Fluid Tank",
                BoundingRectangle(70, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT))

        this.outputs = object: ItemStackHandler(6) {
            override fun onContentsChanged(slot: Int) {
                this@ItemCompoundProducerEntity.markDirty()
            }
        }
        this.addInventory(object : ColoredItemHandler(this.outputs, EnumDyeColor.PURPLE, "Output Items", BoundingRectangle(133, 25, 36, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack) = false
        })
        this.addInventoryToStorage(this.outputs, "inv_outputs")
    }

    override val fluidItemsBoundingBox: BoundingRectangle
        get() = BoundingRectangle(52, 25, 18, 54)

    override fun addFluidItemsBackground(pieces: MutableList<IGuiContainerPiece>, box: BoundingRectangle) {
        pieces.add(BasicRenderedGuiPiece(box.left, box.top, 18, 54,
                Textures.MACHINES_TEXTURES.resource, 6, 44))
    }

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = super.getGuiContainerPieces(container)

        list.add(BasicRenderedGuiPiece(106, 32, 27, 40,
                Textures.MACHINES_TEXTURES.resource, 66, 86))

        list.add(FluidDisplayPiece(108, 42, 20, 20, { this.currentFluid }))
        list.add(ItemStackPiece(108, 42, 20, 20, object: IWorkItemProvider {
            override val workItem: ItemStack
                get() = this@ItemCompoundProducerEntity.currentStack
        }, .75f))

        return list
    }

    override fun getRenderers(): MutableList<TileEntitySpecialRenderer<in TileEntity>> {
        val list = super.getRenderers()
        list.add(DualTankEntityRenderer)
        return list
    }

    override fun getTanks()
            = listOf(TankInfo(13.0, 4.0, this.inputFluid.fluid, this.inputFluid.capacity))

    //#endregion
    //#region storage

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        this.currentStack = if (compound.hasKey("current_stack", Constants.NBT.TAG_COMPOUND))
            ItemStack(compound.getCompoundTag("current_stack"))
        else
            ItemStack.EMPTY

        this.currentFluid = if (compound.hasKey("current_fluid", Constants.NBT.TAG_COMPOUND))
            FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("current_fluid"))
        else
            null
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        if (!this.currentStack.isEmpty) {
            compound.setTag("current_stack", this.currentStack.writeToNBT(NBTTagCompound()))
        }

        if (this.currentFluid != null) {
            compound.setTag("current_fluid", this.currentFluid!!.writeToNBT(NBTTagCompound()))
        }

        return super.writeToNBT(compound)
    }

    //#endregion

    override fun processImmediateInventories() {
        super.processImmediateInventories()

        if (this.currentStack.isEmpty) {
            val fluid = this.inputFluid.fluid
            if ((fluid != null) && (fluid.amount > 0)) {
                for(slot in 0 until this.inputItems.slots) {
                    val stack = this.inputItems.getStackInSlot(slot)
                    if (!stack.isEmpty) {
                        val recipe = ItemCompoundProducerRecipes.findRecipe(fluid, stack) ?: continue
                        val drained = this.inputFluid.drain(recipe.inputFluid.amount, false) ?: continue
                        val taken = this.inputItems.extractItem(slot, recipe.inputStack.count, true)
                        if ((drained.amount == recipe.inputFluid.amount) && (taken.count == recipe.inputStack.count)) {
                            this.currentFluid = this.inputFluid.drain(recipe.inputFluid.amount, true)
                            this.currentStack = this.inputItems.extractItem(slot, recipe.inputStack.count, false)
                            break
                        }
                    }
                }
            }
        }
    }

    override fun performWork(): Float {
        var result = 0.0f
        if (!this.currentStack.isEmpty && (this.currentFluid != null)) {
            val recipe = ItemCompoundProducerRecipes.findRecipe(this.currentFluid!!, this.currentStack) ?: return result

            val remaining = this.outputs.insertItems(recipe.result.copy(), true)
            if (remaining.isEmpty) {
                this.outputs.insertItems(recipe.result.copy(), false)
                this.currentFluid = null
                this.currentStack = ItemStack.EMPTY
                result = 1.0f
            }
        }
        return result
    }
}
