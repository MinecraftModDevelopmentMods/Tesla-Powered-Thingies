package net.ndrei.teslapoweredthingies.machines.portabletank

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandlerItem
import net.modcrafters.mclib.wrapInTag
import net.ndrei.teslacorelib.inventory.FluidStorage
import net.ndrei.teslacorelib.inventory.FluidTank
import net.ndrei.teslacorelib.utils.hasPath

class SimpleTankCaps(private var stack: ItemStack, tankCount: Int, tanksCapacity: Int) : ICapabilityProvider, IFluidHandlerItem {
    private var storage: FluidStorage = FluidStorage()

    private val lazyStorage by lazy { this.readTanks(); this.storage; }

    init {
        (0 until tankCount).forEach {
            this.storage.addTank(FluidTank(tanksCapacity))
        }
        this.readTanks()
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY == capability) {
            return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this)
        }
        return null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?)= (this.getCapability(capability, facing) != null)

    private fun readTanks() {
        val nbt: NBTTagCompound? = if (this.stack.tagCompound?.hasPath("tileentity.fluids") == true)
            this.stack.tagCompound!!.getCompoundTag("tileentity")!!.getCompoundTag("fluids") else null
        this.storage.deserializeNBT(nbt ?: NBTTagCompound())
    }

    override fun drain(resource: FluidStack, doDrain: Boolean) = this.lazyStorage.drain(resource, doDrain)
    override fun drain(maxDrain: Int, doDrain: Boolean) = this.lazyStorage.drain(maxDrain, doDrain)
    override fun fill(resource: FluidStack, doFill: Boolean) = this.lazyStorage.fill(resource, doFill)

    override fun getTankProperties() = this.lazyStorage.tankProperties

    override fun getContainer(): ItemStack = this.stack.copy().also {
        val tanks = this.storage.serializeNBT()
        it.setTagInfo("tileentity", tanks.wrapInTag("fluids"))
    }
}
