package net.ndrei.teslapoweredthingies.machines.incinerator

import net.minecraft.entity.item.EntityItem
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.gui.GeneratorBurnPiece
import net.ndrei.teslapoweredthingies.gui.IWorkItemProvider
import net.ndrei.teslapoweredthingies.gui.ItemStackPiece
import net.ndrei.teslapoweredthingies.machines.BaseThingyGenerator

/**
 * Created by CF on 2017-06-30.
 */
class IncineratorEntity : BaseThingyGenerator(IncineratorEntity::class.java.name.hashCode()), IWorkItemProvider {
    private var inputs: ItemStackHandler? = null
    private var outputs: ItemStackHandler? = null
    private var currentItem: ItemStackHandler? = null

    override fun initializeInventories() {
        super.initializeInventories()

        this.inputs = object : ItemStackHandler(1) {
            override fun onContentsChanged(slot: Int) {
                this@IncineratorEntity.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.inputs!!, EnumDyeColor.GREEN, "Input Items", BoundingRectangle(61, 43, 18, 18)) {
            override fun canExtractItem(slot: Int): Boolean {
                return false
            }

            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                if (stack.isEmpty) {
                    return false
                }

                return IncineratorRecipes.isFuel(stack)
            }
        })
        super.addInventoryToStorage(this.inputs!!, "inv_inputs")

        this.outputs = object : ItemStackHandler(3) {
            override fun onContentsChanged(slot: Int) {
                this@IncineratorEntity.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.outputs!!, EnumDyeColor.PURPLE, "Output Items", BoundingRectangle(133, 25, 18, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return false
            }
        })
        super.addInventoryToStorage(this.outputs!!, "inv_outputs")

        this.currentItem = object : ItemStackHandler(1) {
            override fun onContentsChanged(slot: Int) {
                this@IncineratorEntity.markDirty()
            }
        }
        super.addInventoryToStorage(this.currentItem!!, "inv_current")
    }

    override fun consumeFuel(): Long {
        if (this.currentItem!!.getStackInSlot(0).isEmpty) {
            var stack = this.inputs!!.extractItem(0, 1, true)
            if (!stack.isEmpty) {
                val power = IncineratorRecipes.getPower(stack)
                if (power > 0) {
                    stack = this.inputs!!.extractItem(0, 1, false)
                    if (!stack.isEmpty) {
                        this.currentItem!!.setStackInSlot(0, stack)
                        return power
                    }
                }
            }
        }
        return 0
    }

    override fun fuelConsumed() {
        val stack = this.currentItem!!.getStackInSlot(0)
        if (!ItemStackUtil.isEmpty(stack)) {
            val secondary = IncineratorRecipes.getSecondaryOutputs(stack.item)
            if (secondary != null && secondary.size > 0) {
                for (so in secondary) {
                    val chance = this.getWorld().rand.nextFloat()
                    // TeslaThingiesMod.logger.info("Change: " + chance + " vs " + so.chance);
                    if (chance <= so.chance) {
                        var thing = so.getPossibleOutput()
                        if (!ItemStackUtil.isEmpty(thing)) {
                            thing = ItemHandlerHelper.insertItem(this.outputs, thing.copy(), false)
                            if (!ItemStackUtil.isEmpty(thing)) {
                                val spawnPos = this.pos.offset(super.facing)
                                this.getWorld().spawnEntity(
                                        EntityItem(this.getWorld(), spawnPos.x.toDouble(), spawnPos.y.toDouble(), spawnPos.z.toDouble(), thing))
                            }
                            super.forceSync()
                        }
                    }
                }
            }
        }
        this.currentItem!!.setStackInSlot(0, ItemStack.EMPTY)
    }

    override val energyOutputRate: Long
        get() = 40

    override val energyFillRate: Long
        get() = 40

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val pieces = super.getGuiContainerPieces(container)

        pieces.add(BasicRenderedGuiPiece(79, 41, 54, 22,
                TeslaThingiesMod.MACHINES_TEXTURES, 24, 4))

        pieces.add(GeneratorBurnPiece(99, 64, this))

        pieces.add(object : ItemStackPiece(95, 41, 22, 22, this@IncineratorEntity) {
            override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
                if (!this.isInside(container, mouseX, mouseY)) {
                    return
                }

                val lines = GeneratorBurnPiece.getTooltipLines(this@IncineratorEntity)
                if (lines != null && lines.size > 0) {
                    container.drawTooltip(lines, mouseX - guiX, mouseY - guiY)
                }
            }
        })

        return pieces
    }

    override val workItem: ItemStack
        get() = if (this.currentItem == null) ItemStack.EMPTY else this.currentItem!!.getStackInSlot(0)
}
