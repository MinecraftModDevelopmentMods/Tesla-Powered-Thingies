package net.ndrei.teslapoweredthingies.common;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;

/**
 * Created by CF on 2017-01-09.
 */
public final class FluidUtils {
    public static boolean canFillFrom(IFluidTank tank, ItemStack bucket) {
        if (!ItemStackUtil.isEmpty(bucket) && (bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))) {
            IFluidHandlerItem handler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            FluidStack fluid = (handler != null) ? handler.drain(1000, false) : null;
            if ((fluid != null) && (fluid.amount > 0)) {
                return (1000 == tank.fill(fluid, false));
            }
        }
        return false;
    }

    public static ItemStack fillFluidFrom(IFluidTank tank, ItemStack bucket) {
        if (!ItemStackUtil.isEmpty(bucket) && (bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))) {
            ItemStack clone = bucket.copy();
            IFluidHandlerItem handler = clone.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            FluidStack fluid = (handler != null) ? handler.drain(Fluid.BUCKET_VOLUME,false) : null;
            if ((fluid != null) && (fluid.amount == Fluid.BUCKET_VOLUME)) {
                int filled = tank.fill(fluid, false);
                if (filled == Fluid.BUCKET_VOLUME) {
                    tank.fill(fluid, true);
                    handler.drain(filled, true);
                    return handler.getContainer();
                }
            }
        }
        return bucket;
    }
}
