package net.ndrei.teslapoweredthingies.items

import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.capabilities.ICapabilityAddon
import net.ndrei.teslacorelib.items.BaseAddon
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslapoweredthingies.MOD_ID
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.IFluidStorageMachine

@AutoRegisterItem
object VoidStorageAddon : BaseAddon(MOD_ID, TeslaThingiesMod.creativeTab, "void_storage_addon"), ICapabilityAddon {
    override fun canBeAddedTo(machine: SidedTileEntity): Boolean {
        return machine is IFluidStorageMachine
    }

    override fun <T> getCapability(sidedTileEntity: SidedTileEntity, capability: Capability<T>, facing: EnumFacing?, orientedFacing: EnumFacing?): T? {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            val cap = sidedTileEntity.getCapability(capability, facing)
            if ((cap != null) && (cap is IFluidHandler)) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(FluidHandlerWrapper(cap))
            }
        }

        return null
    }

    private class FluidHandlerWrapper(val cap: IFluidHandler): IFluidHandler {
        override fun drain(resource: FluidStack?, doDrain: Boolean) = cap.drain(resource, doDrain)
        override fun drain(maxDrain: Int, doDrain: Boolean) = cap.drain(maxDrain, doDrain)
        override fun getTankProperties(): Array<out IFluidTankProperties> = this.cap.tankProperties

        override fun fill(resource: FluidStack?, doFill: Boolean): Int {
            if (resource == null) {
                return 0
            }

            this.cap.fill(resource, doFill)
            return resource.amount
        }
    }
}
