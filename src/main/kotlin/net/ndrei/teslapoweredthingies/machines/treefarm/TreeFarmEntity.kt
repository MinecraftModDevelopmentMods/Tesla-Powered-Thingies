package net.ndrei.teslapoweredthingies.machines.treefarm

import com.google.common.collect.Lists
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.common.util.Constants
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.localization.makeTextComponent
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.common.GuiPieceSide
import net.ndrei.teslapoweredthingies.integrations.GUI_TREE_FARM
import net.ndrei.teslapoweredthingies.integrations.localize
import net.ndrei.teslapoweredthingies.machines.CROP_FARM_WORK_AREA_COLOR
import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine

/**
 * Created by CF on 2017-07-07.
 */
class TreeFarmEntity : ElectricFarmMachine(TreeFarmEntity::class.java.name.hashCode()) {
    private val scanner = TreeScanner()
    private var lastScan = 0
    private var scannedBlocks = 0
    private var pendingBlocks = 0

    //#region inventory & gui management

    override fun acceptsInputStack(slot: Int, stack: ItemStack): Boolean {
        if (stack.isEmpty)
            return false

        if (stack.item == Items.SHEARS) {
            return true // TODO: validate modded shears too
        }

        return (TreeWrapperFactory.getSaplingWrapper(stack) != null)
    }

    override val lockableInputLockPosition: GuiPieceSide
        get() = GuiPieceSide.LEFT

    override fun getWorkAreaColor(): Int = CROP_FARM_WORK_AREA_COLOR

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = super.getGuiContainerPieces(container)

        list.add(object: BasicRenderedGuiPiece(45, 45, 14, 14, Textures.MACHINES_TEXTURES.resource, 100, 96) {
            override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
                super.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY)

                if (this.isInside(container, mouseX, mouseY)) {
                    container.drawTooltip(if ((this@TreeFarmEntity.scannedBlocks == 0) && (this@TreeFarmEntity.pendingBlocks == 0))
                        listOf(localize(GUI_TREE_FARM, "waiting_for_tree") { +TextFormatting.GRAY })
                    else listOf(
                        localize(GUI_TREE_FARM, "scanned") {
                            +TextFormatting.RED
                            +this@TreeFarmEntity.scannedBlocks.makeTextComponent()
                        },
                        localize(GUI_TREE_FARM, "pending") {
                            +TextFormatting.GREEN
                            +this@TreeFarmEntity.pendingBlocks.makeTextComponent()
                        },
                        localize(GUI_TREE_FARM, "last_scan") {
                            +TextFormatting.GRAY
                            +this@TreeFarmEntity.lastScan.makeTextComponent()
                        }
                    ), this.left + this.width, this.top)
                }
            }
        })

        return list
    }

    override fun processServerMessage(messageType: String, compound: NBTTagCompound): SimpleNBTMessage? {
        val result = super.processServerMessage(messageType, compound)

        when (messageType) {
            "update_scanner" -> {
                this.scannedBlocks = if (compound.hasKey("blocks", Constants.NBT.TAG_INT)) compound.getInteger("blocks") else 0
                this.pendingBlocks = if (compound.hasKey("pending", Constants.NBT.TAG_INT)) compound.getInteger("pending") else 0
                this.lastScan =  if (compound.hasKey("last", Constants.NBT.TAG_INT)) compound.getInteger("last") else 0
            }
        }

        return result
    }

    override fun processClientMessage(messageType: String?, compound: NBTTagCompound): SimpleNBTMessage? {
        return super.processClientMessage(messageType, compound)
    }

    //#endregion

    override fun performWork(): Float {
        val facing = super.facing
        var result = 0f
        val cube = this.getWorkArea(facing.getOpposite(), 1)

        //#region scan trees

        result += this.scanner.scan(this.getWorld(), cube, SCAN_PERCENT, 1.0f)
        val scanned = Math.round(result / SCAN_PERCENT)
        if ((this.scannedBlocks != this.scanner.blockCount()) || (this.pendingBlocks != this.scanner.pendingCount()) || (this.lastScan != scanned)) {
            this.scannedBlocks = this.scanner.blockCount()
            this.pendingBlocks = this.scanner.pendingCount()
            this.lastScan = scanned

            TeslaCoreLib.network.send(SimpleNBTMessage(this, this.setupSpecialNBTMessage("update_scanner").also {
                it.setInteger("blocks", this.scannedBlocks)
                it.setInteger("pending", this.pendingBlocks)
                it.setInteger("last", this.lastScan)
            }))
        }

        //#endregion

        val inputs = this.inStackHandler!!

        //#region plant saplings

        val saplings = TreeWrapperFactory.getSaplingWrappers(ItemStackUtil.getCombinedInventory(inputs))
                .toMutableList()
        if (saplings.isNotEmpty()) {
            for (pos in cube) {
                if (result > 1 - PLANT_PERCENT) {
                    break
                }

                if (this.getWorld().isAirBlock(pos)) {
                    for (sapling in saplings) {
                        if (sapling.canPlant(this.getWorld(), pos)) {
                            val planted = sapling.plant(this.getWorld(), pos)
                            if (planted > 0) {
                                val original = sapling.stack
                                val extracted = ItemStackUtil.extractFromCombinedInventory(inputs, original, planted)
                                if (original.count <= extracted) {
                                    saplings.remove(sapling)
                                } else {
                                    ItemStackUtil.shrink(original, extracted)
                                }

                                result += PLANT_PERCENT
                                break
                            }
                        }
                    }
                }
            }
        }

        //#endregion

        //#region cut trees

        var hasShears = false
        var shearsSlot = 0
        for (index in 0..inputs.getSlots() - 1) {
            val stack = inputs.getStackInSlot(index)
            if (!stack.isEmpty && stack.item === Items.SHEARS) {
                hasShears = true
                shearsSlot = index
                break
            }
        }

        val items = Lists.newArrayList<ItemStack>()
        while (result <= 1 - BREAK_PERCENT && this.scanner.blockCount() > 0) {
            val pos = this.scanner.popScannedPos()
            if (pos != null) {
                val wrapper = TreeWrapperFactory.getBlockWrapper(this.getWorld(), pos, null)
                if (wrapper is ITreeLeafWrapper) {
                    if (hasShears) {
                        items.addAll(wrapper.shearBlock())
                        if (shearsSlot >= 0) {
                            if (inputs.getStackInSlot(shearsSlot).attemptDamageItem(1, this.getWorld().rand, TeslaThingiesMod.getFakePlayer(this.getWorld()))) {
                                inputs.setStackInSlot(shearsSlot, ItemStack.EMPTY)
                                shearsSlot = -1
                                hasShears = false
                            }
                        }
                    } else {
                        items.addAll(wrapper!!.breakBlock(1))
                    }
                } else if (wrapper != null) {
                    items.addAll(wrapper!!.breakBlock(1))
                }

                result += BREAK_PERCENT
            }
        }

        if (items.size > 0) {
            // TODO: find a way to not lose the items
            super.outputItems(items)
        }

        //#endregion

        return result
    }

    companion object {
        private const val SCAN_PERCENT = 0.025f
        private const val BREAK_PERCENT = 0.05f
        private const val PLANT_PERCENT = 0.10f
    }
}
