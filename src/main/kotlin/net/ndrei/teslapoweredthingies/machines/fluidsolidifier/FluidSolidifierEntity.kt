package net.ndrei.teslapoweredthingies.machines.fluidsolidifier

import com.google.common.collect.Lists
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.gui.*
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.gui.IMultiTankMachine
import net.ndrei.teslapoweredthingies.gui.TankInfo
import net.ndrei.teslapoweredthingies.machines.BaseThingyMachine
import net.ndrei.teslapoweredthingies.render.DualTankEntityRenderer

/**
 * Created by CF on 2017-06-30.
 */
class FluidSolidifierEntity : BaseThingyMachine(FluidSolidifierEntity::class.java.name.hashCode()), IMultiTankMachine {
    private lateinit var waterTank: IFluidTank
    private lateinit var lavaTank: IFluidTank
    private lateinit var outputs: ItemStackHandler

    private var resultType = FluidSolidifierResult.COBBLESTONE
    private var lastWorkResult: FluidSolidifierResult? = null

    //region Inventory and GUI stuff

    override fun initializeInventories() {
        super.initializeInventories()

        super.ensureFluidItems()
        this.waterTank = super.addFluidTank(FluidRegistry.WATER, 5000, EnumDyeColor.BLUE, "Water Tank",
                BoundingRectangle(79, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT))
        this.lavaTank = super.addFluidTank(FluidRegistry.LAVA, 5000, EnumDyeColor.RED, "Lava Tank",
                BoundingRectangle(97, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT))

        this.outputs = object : ItemStackHandler(3) {
            override fun onContentsChanged(slot: Int) {
                this@FluidSolidifierEntity.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.outputs, EnumDyeColor.PURPLE, "Output Items", BoundingRectangle(151, 25, 18, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return false
            }
        })
        super.addInventoryToStorage(this.outputs, "inv_outputs")
    }

    override fun shouldAddFluidItemsInventory(): Boolean {
        return true
    }

    override val fluidItemsBoundingBox: BoundingRectangle
        get() = BoundingRectangle(61, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)

    override fun addFluidItemsBackground(pieces: MutableList<IGuiContainerPiece>, box: BoundingRectangle) {
        pieces.add(BasicRenderedGuiPiece(box.left, box.top, 18, 54,
                Textures.MACHINES_TEXTURES.resource, 6, 44))
    }

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val pieces = super.getGuiContainerPieces(container)

        pieces.add(BasicRenderedGuiPiece(115, 32, 36, 40,
                Textures.MACHINES_TEXTURES.resource, 61, 43))

        pieces.add(object : ToggleButtonPiece(125, 44, 16, 16) {
            override val currentState: Int
                get() = 0

            override fun renderState(container: BasicTeslaGuiContainer<*>, state: Int, box: BoundingRectangle) {
                super.renderItemStack(container, this@FluidSolidifierEntity.resultType.resultStack, box)
            }

            override fun clicked() {
                this@FluidSolidifierEntity.resultType = FluidSolidifierResult.fromStateIndex(
                        (this@FluidSolidifierEntity.resultType.stateIndex + 1) % FluidSolidifierResult.values().size)

                val nbt = this@FluidSolidifierEntity.setupSpecialNBTMessage("SET_RESULT_TYPE")
                nbt.setInteger("result_type", this@FluidSolidifierEntity.resultType.stateIndex)
                TeslaCoreLib.network.sendToServer(SimpleNBTMessage(this@FluidSolidifierEntity, nbt))
            }

            override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
                if (!BasicContainerGuiPiece.isInside(container, this, mouseX, mouseY)) {
                    return
                }

                val lines = Lists.newArrayList<String>()

                val result = this@FluidSolidifierEntity.resultType

                lines.add("Result: " + result.resultStack.displayName)
                lines.add("Water: " + result.waterMbConsumed + " mb (min: " + result.waterMbMin + " mb)")
                lines.add("Lava: " + result.lavaMbConsumed + " mb (min: " + result.lavaMbMin + " mb)")
                lines.add("Time: " + result.ticksRequired.toFloat() / 20.0f + " s (~ " + result.ticksRequired * this@FluidSolidifierEntity.workEnergyTick + " T)")

                container.drawTooltip(lines, mouseX - guiX, mouseY - guiY)
            }
        })

        return pieces
    }

    override fun getRenderers(): MutableList<TileEntitySpecialRenderer<in TileEntity>> {
        val list = super.getRenderers()
        list.add(DualTankEntityRenderer)
        return list
    }

    //endregion

    //#region IMultiTankMachine

    override fun getTanks()
            = listOf(
            TankInfo(6.0, 6.0, this.waterTank.fluid, this.waterTank.capacity),
            TankInfo(20.0, 6.0, this.lavaTank.fluid, this.lavaTank.capacity)
    )

//    override val leftTankPercent: Float
//        get() = Math.min(1f, Math.max(0f, this.waterTank.fluidAmount.toFloat() / this.waterTank.capacity.toFloat()))
//
//    override val rightTankPercent: Float
//        get() = Math.min(1f, Math.max(0f, this.lavaTank.fluidAmount.toFloat() / this.lavaTank.capacity.toFloat()))
//
//    override val leftTankFluid: Fluid?
//        get() = this.waterTank.fluid?.fluid
//
//    override val rightTankFluid: Fluid?
//        get() = this.lavaTank.fluid?.fluid

    //#endregion

    //region serialization

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        if (compound.hasKey("result_type")) {
            this.resultType = FluidSolidifierResult.fromStateIndex(
                    compound.getInteger("result_type"))
        }

        if (compound.hasKey("work_result")) {
            this.lastWorkResult = FluidSolidifierResult.fromStateIndex(
                    compound.getInteger("work_result"))
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val nbt = super.writeToNBT(compound)

        nbt.setInteger("result_type", this.resultType.stateIndex)

        if (this.lastWorkResult != null) {
            nbt.setInteger("work_result", this.lastWorkResult!!.stateIndex)
        }

        return nbt
    }

    override fun processClientMessage(messageType: String?, compound: NBTTagCompound): SimpleNBTMessage? {
        if (messageType != null && messageType == "SET_RESULT_TYPE") {
            this.resultType = FluidSolidifierResult.fromStateIndex(compound.getInteger("result_type"))
            this.markDirty()
        }

        return super.processClientMessage(messageType, compound)
    }

    //endregion

    override val energyForWork: Int
        get() {
            this.lastWorkResult = this.resultType
            return this.lastWorkResult!!.ticksRequired * this.energyForWorkRate
        }

    override fun performWork(): Float {
        if (this.lastWorkResult == null) {
            return 0.0f
        }

        val hasWater = this.waterTank.fluidAmount >= this.lastWorkResult!!.waterMbMin
        if (hasWater) {
            val hasLava = this.lavaTank.fluidAmount >= this.lastWorkResult!!.lavaMbMin
            if (hasLava) {
                val waterRequired = this.lastWorkResult!!.waterMbConsumed > 0
                val water = if (waterRequired) this.waterTank.drain(this.lastWorkResult!!.waterMbConsumed, false) else null
                if (!waterRequired || water != null && water.amount == this.lastWorkResult!!.waterMbConsumed) {
                    val lavaRequired = this.lastWorkResult!!.lavaMbConsumed > 0
                    val lava = if (lavaRequired) this.lavaTank.drain(this.lastWorkResult!!.lavaMbConsumed, false) else null
                    if (!lavaRequired || lava != null && lava.amount == this.lastWorkResult!!.lavaMbConsumed) {
                        val remaining = ItemHandlerHelper.insertItem(this.outputs, this.lastWorkResult!!.resultStack.copy(), false)
                        if (ItemStackUtil.isEmpty(remaining)) {
                            // actually drain liquids
                            if (waterRequired) {
                                this.waterTank.drain(this.lastWorkResult!!.waterMbConsumed, true)
                            }
                            if (lavaRequired) {
                                this.lavaTank.drain(this.lastWorkResult!!.lavaMbConsumed, true)
                            }

                            // work performed
                            this.lastWorkResult = null
                            return 1.0f
                        }
                    }
                }
            }
        }
        return 0.0f
    }
}
