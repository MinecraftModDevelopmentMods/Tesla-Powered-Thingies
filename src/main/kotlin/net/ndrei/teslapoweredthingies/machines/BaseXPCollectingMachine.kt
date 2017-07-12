package net.ndrei.teslapoweredthingies.machines

import net.minecraft.entity.item.EntityXPOrb
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagInt
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.FilteredFluidTank
import net.ndrei.teslacorelib.inventory.FluidTank
import net.ndrei.teslacorelib.utils.BlockCube
import net.ndrei.teslacorelib.utils.BlockPosUtils
import net.ndrei.teslapoweredthingies.common.ILiquidXPCollector
import net.ndrei.teslapoweredthingies.fluids.LiquidXPFluid
import net.ndrei.teslapoweredthingies.items.LiquidXPCollectorItem

/**
 * Created by CF on 2017-07-06.
 */
abstract class BaseXPCollectingMachine(typeId: Int)
    : ElectricFarmMachine(typeId), ILiquidXPCollector {

    protected var xpTank: IFluidTank? = null

    override fun hasXPCollector()
            = this.hasAddon(LiquidXPCollectorItem::class.java)

    override fun onLiquidXPAddonAdded(stack: ItemStack) {
        if (this.xpTank != null) {
            super.removeFluidTank(EnumDyeColor.LIME, this.xpTank!!)
            this.xpTank = null
        }

        this.xpTank = FilteredFluidTank(LiquidXPFluid, object : FluidTank(LiquidXPCollectorItem.MAX_CAPACITY) {
            override fun onContentsChanged() {
                val amount = this.fluidAmount
                if (amount > 0) {
                    val stack = this@BaseXPCollectingMachine.getAddonStack(LiquidXPCollectorItem::class.java)
                    if (!ItemStackUtil.isEmpty(stack)) {
                        stack.setTagInfo("StoredLiquidXP", NBTTagInt(this.fluidAmount))
                    }
                } else {
                    val stack = this@BaseXPCollectingMachine.getAddonStack(LiquidXPCollectorItem::class.java)
                    if (!ItemStackUtil.isEmpty(stack)) {
                        val nbt = stack.getTagCompound()
                        if (nbt != null && nbt!!.hasKey("StoredLiquidXP", Constants.NBT.TAG_INT)) {
                            nbt!!.removeTag("StoredLiquidXP")
                        }
                    }
                }
            }
        })
        super.addFluidTank(this.xpTank!!, EnumDyeColor.LIME, "Liquid XP",
                BoundingRectangle(151, 25, 18, 54))
        val xp = LiquidXPCollectorItem.getStoredXP(stack)
        if (xp > 0) {
            this.xpTank!!.fill(FluidStack(LiquidXPFluid, xp), true)
        }

        BasicTeslaGuiContainer.refreshParts(this.getWorld())
    }

    override fun onLiquidXPAddonRemoved(stack: ItemStack) {
        if (this.xpTank != null) {
            super.removeFluidTank(EnumDyeColor.LIME, this.xpTank!!)
            this.xpTank = null

            BasicTeslaGuiContainer.refreshParts(this.getWorld())
        }
    }

    override fun performWork(): Float {
        var result = this.performWorkInternal() / 1.25f

        if (result <= .800001 && super.hasAddon(LiquidXPCollectorItem::class.java) && this.xpTank != null) {
            var orbCollected = false
            for (orb in this.xpOrbLookupCube.findEntities(EntityXPOrb::class.java, this.getWorld())) {
                if (this.xpTank!!.capacity <= this.xpTank!!.fluidAmount) {
                    break
                }

                if (0 < this.xpTank!!.fill(FluidStack(LiquidXPFluid, orb.getXpValue()), true)) {
                    this.getWorld().removeEntity(orb)
                    orbCollected = true
                }
            }
            if (orbCollected) {
                result += .2f
            }
        }

        return Math.min(1.0f, result)
    }

    protected open val xpOrbLookupCube: BlockCube
        get() = BlockPosUtils.getCube(this.getPos(), super.facing.opposite, 4, 1)

    protected abstract fun performWorkInternal(): Float

    override fun getInputInventoryBounds(columns: Int, rows: Int): BoundingRectangle {
        val area = super.getInputInventoryBounds(columns, rows)
        return BoundingRectangle(area.left - 18, area.top, area.width, area.height)
    }

    override fun getOutputInventoryBounds(columns: Int, rows: Int): BoundingRectangle {
        val area = super.getOutputInventoryBounds(columns, rows)
        return BoundingRectangle(area.left - 18, area.top, area.width, area.height)
    }
}
