package net.ndrei.teslapoweredthingies.machines.liquidxpstorage

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Items
import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.FilteredSlot
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.FluidTankType
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslacorelib.utils.canFillFrom
import net.ndrei.teslacorelib.utils.copyWithSize
import net.ndrei.teslapoweredthingies.client.ThingiesTexture
import net.ndrei.teslapoweredthingies.common.LiquidXPUtils
import net.ndrei.teslapoweredthingies.common.changeExperience
import net.ndrei.teslapoweredthingies.fluids.LiquidXPFluid
import net.ndrei.teslapoweredthingies.integrations.GUI_PLAYER_LIQUID_XP
import net.ndrei.teslapoweredthingies.integrations.localize
import net.ndrei.teslapoweredthingies.items.XPTankAddonItem
import net.ndrei.teslapoweredthingies.render.LiquidXPStorageSpecialRenderer

/**
 * Created by CF on 2017-07-07.
 */
class LiquidXPStorageEntity : SidedTileEntity(LiquidXPStorageEntity::class.java.name.hashCode()) {
    private lateinit var inputItems: ItemStackHandler
    private lateinit var outputItems: ItemStackHandler
    private lateinit var xpTank: IFluidTank

    //#region inventories

    override fun initializeInventories() {
        super.initializeInventories()

        this.inputItems = object : ItemStackHandler(2) {
            override fun onContentsChanged(slot: Int) {
                this@LiquidXPStorageEntity.markDirty()
            }
        }
        this.outputItems = object : ItemStackHandler(2) {
            override fun getStackLimit(slot: Int, stack: ItemStack): Int {
                return if (slot == 0) 1 else super.getStackLimit(slot, stack)
            }

            override fun onContentsChanged(slot: Int) {
                this@LiquidXPStorageEntity.markDirty()
            }
        }

        super.addInventory(object : ColoredItemHandler(this.inputItems, EnumDyeColor.GREEN,
                "Input Liquid Containers", BoundingRectangle(56, 25, 18, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return slot == 0 && this@LiquidXPStorageEntity.isValidInContainer(stack)
            }

            override fun canExtractItem(slot: Int): Boolean {
                return slot == 1
            }

            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                val slots = mutableListOf<Slot>()
                val box = this.boundingBox
                if (!box.isEmpty) {
                    slots.add(FilteredSlot(this.itemHandlerForContainer, 0, box.left + 1, box.top + 1))
                    slots.add(FilteredSlot(this.itemHandlerForContainer, 1, box.left + 1, box.top + 1 + 36))
                }
                return slots
            }

            override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> = mutableListOf()
        })
        super.addInventoryToStorage(this.inputItems, "income")

        this.xpTank = this.addSimpleFluidTank(4200, "Liquid XP", EnumDyeColor.LIME,
                79, 25, FluidTankType.BOTH, { it.fluid === LiquidXPFluid })

        super.addInventory(object : ColoredItemHandler(this.outputItems, EnumDyeColor.PURPLE,
                "Output Liquid Containers", BoundingRectangle(102, 25, 18, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return slot == 0 && this@LiquidXPStorageEntity.isValidOutContainer(stack)
            }

            override fun canExtractItem(slot: Int): Boolean {
                return slot == 1
            }

            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                val slots = mutableListOf<Slot>()
                val box = this.boundingBox
                if (!box.isEmpty) {
                    slots.add(FilteredSlot(this.itemHandlerForContainer, 0, box.left + 1, box.top + 1))
                    slots.add(FilteredSlot(this.itemHandlerForContainer, 1, box.left + 1, box.top + 1 + 36))
                }
                return slots
            }

            override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> = mutableListOf()
        })
        super.addInventoryToStorage(this.outputItems, "outcome")
    }

    private fun isValidInContainer(stack: ItemStack): Boolean {
        return !stack.isEmpty && ((stack.item === Items.EXPERIENCE_BOTTLE) || this.fluidHandler.canFillFrom(stack))
    }

    private fun isValidOutContainer(stack: ItemStack): Boolean {
        return !stack.isEmpty && ((stack.item === Items.GLASS_BOTTLE) || stack.copyWithSize(1).canFillFrom(this.xpTank))
    }

    override fun shouldAddFluidItemsInventory(): Boolean {
        return false
    }

    //#endregion
    //#region gui

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = super.getGuiContainerPieces(container)

        list.add(BasicRenderedGuiPiece(56, 25, 64, 54,
                ThingiesTexture.FARM_TEXTURES.resource, 65, 1))

        if (this.hasAddon(XPTankAddonItem.javaClass)) {
            list.add(LiquidXPStorageButton(25, 25, "-1", localize(GUI_PLAYER_LIQUID_XP, "Take 1"), {
                val message = this.setupSpecialNBTMessage("TAKE_XP")
                message.setInteger("levels", 1)
                this.sendToServer(message)
            }))
            list.add(LiquidXPStorageButton(25, 43, "-10", localize(GUI_PLAYER_LIQUID_XP, "Take 10"), {
                val message = this.setupSpecialNBTMessage("TAKE_XP")
                message.setInteger("levels", 10)
                this.sendToServer(message)
            }))
            list.add(LiquidXPStorageButton(25, 61, "MAX", localize(GUI_PLAYER_LIQUID_XP, "Take MAX"), {
                val message = this.setupSpecialNBTMessage("TAKE_XP")
                message.setInteger("levels", 666)
                this.sendToServer(message)
            }))

            list.add(LiquidXPStorageButton(133, 25, "+1", localize(GUI_PLAYER_LIQUID_XP, "Give 1"), {
                val message = this.setupSpecialNBTMessage("GIVE_XP")
                message.setInteger("levels", 1)
                this.sendToServer(message)
            }))
            list.add(LiquidXPStorageButton(133, 43, "+10", localize(GUI_PLAYER_LIQUID_XP, "Give 10"), {
                val message = this.setupSpecialNBTMessage("GIVE_XP")
                message.setInteger("levels", 10)
                this.sendToServer(message)
            }))
            list.add(LiquidXPStorageButton(133, 61, "MAX", localize(GUI_PLAYER_LIQUID_XP, "Give MAX"), {
                val message = this.setupSpecialNBTMessage("GIVE_XP")
                message.setInteger("levels", 666)
                this.sendToServer(message)
            }))
        }

        return list
    }

    override fun getRenderers(): MutableList<TileEntitySpecialRenderer<in TileEntity>> {
        val list = super.getRenderers()
        list.add(LiquidXPStorageSpecialRenderer)
        return list
    }

    override fun processClientMessage(messageType: String?, player: EntityPlayerMP?, compound: NBTTagCompound): SimpleNBTMessage? {
        if (player != null) {
            when (messageType) {
                "TAKE_XP" -> {
                    val levels = compound.getInteger("levels")
                    val levelsToTake = when (levels) {
                        1 -> 1
                        10 -> 10
                        else -> player.experienceLevel
                    }
                    val targetLevel = Math.max(1, player.experienceLevel - levelsToTake)
                    val targetXP = LiquidXPUtils.getXPForLevel(targetLevel - 1)
                    val xpToTake = player.experienceTotal - targetXP
                    if (xpToTake > 0) {
                        val maxXP = (this.xpTank.capacity - this.xpTank.fluidAmount) / LiquidXPUtils.LiquidXP_PER_XP
                        val finalXP = Math.min(maxXP, xpToTake)
                        if (finalXP > 0) {
                            val filled = this.xpTank.fill(FluidStack(LiquidXPFluid, finalXP * LiquidXPUtils.LiquidXP_PER_XP), true)
                            if (filled > 0) {
                                player.changeExperience(-filled / LiquidXPUtils.LiquidXP_PER_XP)
                            }
                        }
                    }
                }
                "GIVE_XP" -> {
                    val levels = compound.getInteger("levels")
                    val levelsToGive = when (levels) {
                        1 -> 1
                        10 -> 10
                        else -> this.xpTank.fluidAmount / LiquidXPUtils.LiquidXP_PER_XP
                    }
                    val targetLevel = player.experienceLevel + levelsToGive
                    val targetXP = LiquidXPUtils.getXPForLevel(targetLevel - 1)
                    val xpToGive = targetXP - player.experienceTotal
                    if (xpToGive > 0) {
                        val maxXP = this.xpTank.fluidAmount / LiquidXPUtils.LiquidXP_PER_XP
                        val finalXP = Math.min(maxXP, xpToGive)
                        if (finalXP > 0) {
                            val drained = this.xpTank.drain(finalXP * LiquidXPUtils.LiquidXP_PER_XP, true)
                            if ((drained != null) && (drained.amount > 0)) {
                                player.changeExperience(drained.amount / LiquidXPUtils.LiquidXP_PER_XP)
                            }
                        }
                    }
                }
            }
        }

        return super.processClientMessage(messageType, player, compound)
    }

    //#endregion

    private var delay = 0

    override fun innerUpdate() {
        if (this.getWorld().isRemote) {
            return
        }

        if (--this.delay > 0) {
            return
        }

        var transferred = 0

        //#region process inputs

        val i_stack = this.inputItems.getStackInSlot(0)
        val capacity = this.xpTank.capacity - this.xpTank.fluidAmount
        if (!i_stack.isEmpty && (capacity > 0)) {
            if ((i_stack.item === Items.EXPERIENCE_BOTTLE) && (capacity >= 15)) {
                val outcome = ItemStack(Items.GLASS_BOTTLE, 1)
                val income = FluidStack(LiquidXPFluid, 15)
                val filled = this.xpTank.fill(income, false)
                if (filled == 15) {
                    val target = this.inputItems.getStackInSlot(1)
                    var inputted = false
                    if (target.isEmpty) {
                        this.inputItems.setStackInSlot(1, outcome)
                        inputted = true
                    }
                    else if ((target.item === outcome.item) && (target.count < target.maxStackSize)) {
                        target.grow(1)
                        inputted = true
                    }

                    if (inputted) {
                        i_stack.shrink(1)
                        this.xpTank.fill(income, true)
                    }
                }
            }
            else {
                super.processFluidItems(this.inputItems)
                // listOf(this.xpTank).processInputInventory(this.inputItems)
            }
        }

        //#endregion

        //#region process outputs

        val o_stack = this.outputItems.getStackInSlot(0)
        val maxDrain = this.xpTank.fluidAmount
        if (!o_stack.isEmpty && (maxDrain > 0)) {
            if (o_stack.item === Items.GLASS_BOTTLE) {
                //#region glass bottle -> experience bottle

                if (maxDrain >= 15) {
                    val existing = this.outputItems.getStackInSlot(1)
                    var result = ItemStack.EMPTY
                    if (existing.isEmpty) {
                        result = ItemStack(Items.EXPERIENCE_BOTTLE, 1)
                    } else if (existing.count < existing.maxStackSize) {
                        result = ItemStackUtil.copyWithSize(existing, existing.count + 1)
                    }

                    if (!result.isEmpty) {
                        this.outputItems.setStackInSlot(1, result)
                        this.xpTank.drain(15, true)

                        i_stack.shrink(1)
                        if (i_stack.count == 0) {
                            this.outputItems.setStackInSlot(0, ItemStack.EMPTY)
                        }

                        transferred += 15
                    }
                }

                //#endregion
            }
            else if (o_stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
                    && this.outputItems.getStackInSlot(1).isEmpty) {
                val toFill = Math.max(maxDrain, Fluid.BUCKET_VOLUME)
                val initial = this.xpTank.fluidAmount
                val bucket = ItemStackUtil.copyWithSize(o_stack, 1)
                val result = FluidUtil.tryFillContainer(bucket, this.fluidHandler, toFill, null, true)
                if (result.isSuccess) {
                    // stack = result.getResult()
                    // this.outputItems.insertItem(1, stack, false)
                    this.outputItems.setStackInSlot(1, result.getResult())
                    this.outputItems.getStackInSlot(0).shrink(1)

                    transferred += initial - this.xpTank.fluidAmount
                }
            }
        }

        //#endregion

        if (transferred > 0) {
            this.delay = Math.max(5, transferred / 50)
            this.forceSync()
        } else if (this.delay < 0) {
            this.delay = 0
        }
    }

    val fillPercent: Float
        get() = this.xpTank.fluidAmount.toFloat() / this.xpTank.capacity.toFloat()
}
