package net.ndrei.teslapoweredthingies.machines

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.gui.LockedInventoryTogglePiece
import net.ndrei.teslacorelib.gui.SideDrawerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.render.IWorkAreaProvider
import net.ndrei.teslacorelib.render.WorkingAreaRenderer
import net.ndrei.teslacorelib.utils.BlockCube
import net.ndrei.teslacorelib.utils.BlockPosUtils
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.common.GuiPieceSide
import net.ndrei.teslapoweredthingies.items.MachineRangeAddonTier1
import net.ndrei.teslapoweredthingies.items.MachineRangeAddonTier2

/**
 * Created by CF on 2017-07-06.
 */
abstract class ElectricFarmMachine protected constructor(typeId: Int) : BaseThingyMachine(typeId), IWorkAreaProvider {
    protected var inStackHandler: IItemHandlerModifiable? = null
    protected var filteredInStackHandler: ColoredItemHandler? = null
    protected var outStackHandler: IItemHandlerModifiable? = null

    //#region inventories & gui    methods

    override fun initializeInventories() {
        super.initializeInventories()

        this.initializeInputInventory()
        this.initializeOutputInventory()
    }

    protected open val inputSlots: Int
        get() = 3

    protected open fun initializeInputInventory() {
        val inputSlots = this.inputSlots
        this.inStackHandler = if (inputSlots > 0) {
            val slots = Math.max(0, inputSlots)
            val suggestedColumns = Math.min(this.inputInventoryColumns, slots)
            val bounds = this.getInputInventoryBounds(suggestedColumns, slots / suggestedColumns)
            this.addSimpleInventory(slots, "inputs",
                EnumDyeColor.GREEN, "Input Items",
                bounds,
                { stack, slot -> this@ElectricFarmMachine.acceptsInputStack(slot, stack) },
                { _, _ -> false },
                this.lockableInputInventory,
                colorIndex = COLOR_INDEX_INPUTS)
        } else {
            null
        }
        this.filteredInStackHandler = this.getInventory(EnumDyeColor.GREEN)
    }

    protected open val lockableInputInventory: Boolean
        get() = true

    protected open val lockableInputLockPosition: GuiPieceSide
        get() = GuiPieceSide.NONE

    protected open val inputInventoryColumns get() = Math.max(this.inputSlots, 3)

    protected open fun getInputInventoryBounds(columns: Int, rows: Int)
        = BoundingRectangle(115 + (3 - columns) * 9, 25, 18 * columns, 18 * rows)

    protected open fun acceptsInputStack(slot: Int, stack: ItemStack) = true

    protected open val outputSlots: Int
        get() = 6

    private fun initializeOutputInventory() {
        val outputSlots = this.outputSlots
        this.outStackHandler = if (outputSlots > 0) {
            val slots = Math.max(0, outputSlots)
            val suggestedColumns = Math.min(this.outputInventoryColumns, slots)
            val bounds = this.getOutputInventoryBounds(suggestedColumns, slots / suggestedColumns)
            this.addSimpleInventory(slots, "outputs", EnumDyeColor.PURPLE, "Output Items",
                bounds,
                { _, _ -> false },
                { _, _ -> true },
                colorIndex = COLOR_INDEX_OUTPUTS)
        } else {
            null
        }
    }

    protected open val outputInventoryColumns get() = 3

    protected open fun getOutputInventoryBounds(columns: Int, rows: Int)
        = BoundingRectangle(115, if (this.inputSlots > 0) 43 else 25, 18 * columns, 18 * rows)

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = super.getGuiContainerPieces(container)

        if (this.lockableInputInventory && (this.lockableInputLockPosition != GuiPieceSide.NONE)
                && (this.filteredInStackHandler != null)) {
            val box = this.filteredInStackHandler!!.boundingBox
            if (!box.isEmpty)
            list.add(LockedInventoryTogglePiece(
                    when (this.lockableInputLockPosition) {
                        GuiPieceSide.LEFT -> box.left - 16
                        else -> box.right + 2 // assume RIGHT
                    }, box.top + 2, this, this.filteredInStackHandler!!.color)
            )
        }

        if (this.hasWorkArea) {
            list.add(object: SideDrawerPiece(SideDrawerPiece.findFreeSpot(list)) {
                override fun renderState(container: BasicTeslaGuiContainer<*>, state: Int, box: BoundingRectangle) {
                    Textures.MACHINES_TEXTURES.bind(container)

                    GlStateManager.enableBlend()
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
                    container.drawTexturedModalRect(
                            box.left, box.top + 1,
                            99, if (this@ElectricFarmMachine.showWorkArea) 21 else 7,
                            14, 14)
                    GlStateManager.disableBlend()
                }

                override fun getStateToolTip(state: Int)
                    = listOf(
                        if (this@ElectricFarmMachine.showWorkArea)
                            "Hide work area"
                        else
                            "Show work area"
                )

                override fun clicked() {
                    this@ElectricFarmMachine.showWorkArea = !this@ElectricFarmMachine.showWorkArea
                }
            })
        }

        return list
    }

    override fun getRenderers(): MutableList<TileEntitySpecialRenderer<in TileEntity>> {
        val list = super.getRenderers()
        if (this.hasWorkArea && this.showWorkArea) {
            list.add(WorkingAreaRenderer)
        }
        return list
    }

    //#endregion
    //#region range addons         methods

    open fun supportsRangeAddons() = true

    protected val range: Int
        get() = this.getRange(3, 3)

    private fun getRange(base: Int, perTier: Int): Int {
        val tier1 = if (this.hasAddon(MachineRangeAddonTier1::class.java)) 1 else 0
        val tier2 = tier1 * if (this.hasAddon(MachineRangeAddonTier2::class.java)) 1 else 0

        return base + (tier1 + tier2) * perTier
    }

    protected  fun getWorkArea(facing: EnumFacing, height: Int): BlockCube {
        return BlockPosUtils.getCube(this.getPos(), facing, this.range, height)
    }

    //#endregion
    //#region output & spawn items methods

    private fun spawnOverloadedItem(stack: ItemStack) =
        null != super.spawnItemFromFrontSide(stack)

    fun outputItems(loot: ItemStack)
        = loot.isEmpty || this.outputItems(listOf(loot))

    fun outputItems(loot: List<ItemStack>): Boolean {
        if (loot.isNotEmpty()) {
            for (lootStack in loot) {
                var remaining = if (this.filteredInStackHandler == null)
                    ItemStackUtil.insertItemInExistingStacks(this.inStackHandler, lootStack, false)
                else
                    ItemHandlerHelper.insertItemStacked(this.filteredInStackHandler, lootStack, false)

                if (!remaining.isEmpty) {
                    remaining = ItemHandlerHelper.insertItem(this.outStackHandler, lootStack, false)
                }
                if (!remaining.isEmpty) {
                    return this.spawnOverloadedItem(remaining)
                }
            }
        }
        return true
    }

    //#endregion
    //#region work area diaplsy    methods

    open val hasWorkArea: Boolean = true

    var showWorkArea: Boolean = false

    override fun getWorkArea(): BlockCube = this.getWorkArea(this.facing.opposite, 1)
    override fun getWorkAreaColor(): Int = 0x54CBFF

    override fun getRenderBoundingBox(): AxisAlignedBB {
        return super.getRenderBoundingBox().union(this.getWorkArea().boundingBox)
    }

    //#endregion

    companion object {
        const val COLOR_INDEX_INPUTS = 10
        const val COLOR_INDEX_OUTPUTS = 20
    }
}
