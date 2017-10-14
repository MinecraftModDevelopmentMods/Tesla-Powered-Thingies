package net.ndrei.teslapoweredthingies.machines.incinerator

import net.minecraft.entity.item.EntityItem
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.SyncItemHandler
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.gui.GeneratorBurnPiece
import net.ndrei.teslapoweredthingies.gui.IWorkItemProvider
import net.ndrei.teslapoweredthingies.gui.ItemStackPiece
import net.ndrei.teslapoweredthingies.machines.BaseThingyGenerator

/**
 * Created by CF on 2017-06-30.
 */
class IncineratorEntity : BaseThingyGenerator(IncineratorEntity::class.java.name.hashCode()), IWorkItemProvider {
    private lateinit var inputs: IItemHandler
    private lateinit var outputs: IItemHandler
    private lateinit var currentItem: IItemHandlerModifiable

    //#region inventory & gui

    override fun initializeInventories() {
        super.initializeInventories()

        this.inputs = this.addSimpleInventory(1, "inv_inputs", EnumDyeColor.GREEN, "Input Items",
            BoundingRectangle.slots(61, 43, 1, 1),
            { stack, _ -> IncineratorRegistry.isFuel(stack) },
            { _, _ -> false },
            true)

        this.outputs = this.addSimpleInventory(3, "inv_outputs", EnumDyeColor.PURPLE, "Output Items",
            BoundingRectangle.slots(133, 25, 1, 3),
            { _, _ -> false })

        this.currentItem = SyncItemHandler(1)
        super.addInventoryToStorage(this.currentItem as SyncItemHandler, "inv_current")
    }

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val pieces = super.getGuiContainerPieces(container)

        pieces.add(BasicRenderedGuiPiece(79, 41, 54, 22,
                Textures.MACHINES_TEXTURES.resource, 24, 4))

        pieces.add(GeneratorBurnPiece(99, 64, this))

        pieces.add(object : ItemStackPiece(95, 41, 22, 22, this@IncineratorEntity) {
            override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
                if (!this.isInside(container, mouseX, mouseY)) {
                    return
                }

                val lines = GeneratorBurnPiece.getTooltipLines(this@IncineratorEntity)
                if (lines != null && lines.isNotEmpty()) {
                    container.drawTooltip(lines, mouseX - guiX, mouseY - guiY)
                }
            }
        })

        return pieces
    }

    override val workItem: ItemStack
        get() = this.currentItem.getStackInSlot(0)

    //#endregion

    override fun consumeFuel(): Long {
        if (this.currentItem.getStackInSlot(0).isEmpty) {
            var stack = this.inputs.extractItem(0, 1, true)
            if (!stack.isEmpty) {
                val power = IncineratorRegistry.getPower(stack)
                if (power > 0) {
                    stack = this.inputs.extractItem(0, 1, false)
                    if (!stack.isEmpty) {
                        this.currentItem.setStackInSlot(0, stack)
                        return power
                    }
                }
            }
        }
        return 0
    }

    override fun fuelConsumed() {
        val stack = this.currentItem.getStackInSlot(0)
        if (!stack.isEmpty) {
            val secondary = IncineratorRegistry.getSecondaryOutputs(stack)
            if (secondary.isNotEmpty()) {
                for (so in secondary) {
                    val chance = this.getWorld().rand.nextFloat()
                    // TeslaThingiesMod.logger.info("Change: " + chance + " vs " + so.chance);
                    if (chance <= so.chance) {
                        var thing = so.getPossibleOutput()
                        if (!thing.isEmpty) {
                            thing = ItemHandlerHelper.insertItem(this.outputs, thing.copy(), false)
                            if (!thing.isEmpty) {
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
        this.currentItem.setStackInSlot(0, ItemStack.EMPTY)
    }

    override val energyOutputRate: Long
        get() = 40

    override val energyFillRate: Long
        get() = 40
}
