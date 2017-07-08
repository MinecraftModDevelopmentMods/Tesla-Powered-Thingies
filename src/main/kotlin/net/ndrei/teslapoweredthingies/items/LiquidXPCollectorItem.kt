package net.ndrei.teslapoweredthingies.items

import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.FluidTankProperties
import net.minecraftforge.fluids.capability.IFluidHandlerItem
import net.minecraftforge.fluids.capability.IFluidTankProperties
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.items.BaseAddon
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.ILiquidXPCollector
import net.ndrei.teslapoweredthingies.fluids.LiquidXPFluid

/**
 * Created by CF on 2017-07-06.
 */
@AutoRegisterItem
object LiquidXPCollectorItem
    : BaseAddon(TeslaThingiesMod.MODID, TeslaThingiesMod.creativeTab, "liquidxp_collector") {

    const val MAX_CAPACITY = 1000

//    override  val recipe: IRecipe
//        get() = ShapedOreRecipe(null, ItemStack(this, 1),
//                " x ",
//                "gyg",
//                "bbb",
//                'x', Items.EXPERIENCE_BOTTLE,
//                'g', GearRegistry.getMaterial(MATERIAL_IRON)?.oreDictName ?: "gearIron",
//                'y', BaseAddonItem,
//                'b', Items.GLASS_BOTTLE)

    override fun canBeAddedTo(machine: SidedTileEntity)
            = (machine is ILiquidXPCollector) && !machine.hasXPCollector()

    override val workEnergyMultiplier: Float
        get() = 1.25f

    override fun onAdded(addon: ItemStack, machine: SidedTileEntity) {
        super.onAdded(addon, machine)

        if (machine is ILiquidXPCollector) {
            (machine as ILiquidXPCollector).onLiquidXPAddonAdded(addon)
        }
    }

    override fun onRemoved(addon: ItemStack, machine: SidedTileEntity) {
        super.onRemoved(addon, machine)

        if (machine is ILiquidXPCollector) {
            (machine as ILiquidXPCollector).onLiquidXPAddonRemoved(addon)
        }
    }

    override fun addInformation(stack: ItemStack?, worldIn: World?, tooltip: MutableList<String>?, flagIn: ITooltipFlag?) {
        super.addInformation(stack, worldIn, tooltip, flagIn)

        if ((stack != null) && (tooltip != null)) {
            val nbt = if (stack.isEmpty) null else stack.tagCompound
            if ((nbt != null) && nbt.hasKey("StoredLiquidXP", Constants.NBT.TAG_INT)) {
                tooltip.add(ChatFormatting.DARK_GREEN.toString() + "Stored XP: " + ChatFormatting.GREEN + nbt.getInteger("StoredLiquidXP"))
            } else {
                tooltip.add(ChatFormatting.DARK_GRAY.toString() + "No XP Stored")
            }
        }
    }

    override fun initCapabilities(stack: ItemStack?, nbt: NBTTagCompound?): ICapabilityProvider? {
        return if (stack != null) LiquidXPHolderCapability(stack) else null
    }

    private class LiquidXPHolderCapability(private val stack: ItemStack)
        : ICapabilityProvider, IFluidHandlerItem {

        override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
            return capability === CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY
        }

        override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
            if (capability === CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
                return this as T
            }
            return null
        }

        override fun getContainer(): ItemStack {
            return this.stack
        }

        override fun getTankProperties(): Array<IFluidTankProperties> {
            return arrayOf(FluidTankProperties(FluidStack(LiquidXPFluid, getStoredXP(this.stack)),
                    MAX_CAPACITY, true, true))
        }

        override fun fill(resource: FluidStack?, doFill: Boolean): Int {
            if (resource == null || resource.amount == 0 || resource.fluid !== LiquidXPFluid) {
                return 0
            }

            val existing = LiquidXPCollectorItem.getStoredXP(this.stack)
            val filled = Math.min(LiquidXPCollectorItem.MAX_CAPACITY - existing, resource.amount)
            if (doFill) {
                LiquidXPCollectorItem.setStoredXP(this.stack, existing + filled)
            }
            return filled
        }

        override fun drain(resource: FluidStack?, doDrain: Boolean): FluidStack? {
            if (resource == null || resource.fluid !== LiquidXPFluid) {
                return null
            }

            return this.drain(resource.amount, doDrain)
        }

        override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
            val existing = LiquidXPCollectorItem.getStoredXP(this.stack)
            if (maxDrain == 0 || existing == 0) {
                return null
            }

            val canDrain = Math.min(existing, maxDrain)
            if (doDrain) {
                LiquidXPCollectorItem.setStoredXP(this.stack, existing - canDrain)
            }
            return FluidStack(LiquidXPFluid, canDrain)
        }
    }

    override fun getItemStackLimit(stack: ItemStack?): Int {
        val xp = if (stack != null) LiquidXPCollectorItem.getStoredXP(stack) else 0
        return if (xp == 0) 16 else 4
    }

    fun getStoredXP(stack: ItemStack): Int {
        if (!ItemStackUtil.isEmpty(stack) && stack.item === LiquidXPCollectorItem) {
            val nbt = stack.tagCompound
            if (nbt != null && nbt.hasKey("StoredLiquidXP", Constants.NBT.TAG_INT)) {
                return nbt.getInteger("StoredLiquidXP")
            }
        }
        return 0
    }

    fun setStoredXP(stack: ItemStack, storedXp: Int) {
        if (!ItemStackUtil.isEmpty(stack) && stack.item === LiquidXPCollectorItem) {
            var nbt = stack.tagCompound
            if (nbt == null) {
                stack.tagCompound = NBTTagCompound()
                nbt = stack.tagCompound
            }

            if (storedXp > 0) {
                nbt!!.setInteger("StoredLiquidXP", storedXp)
            } else if (nbt!!.hasKey("StoredLiquidXP", Constants.NBT.TAG_INT)) {
                nbt.removeTag("StoredLiquidXP")
            }
        }
    }
}
