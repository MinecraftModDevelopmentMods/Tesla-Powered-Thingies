package net.ndrei.teslapoweredthingies.machines.itemliquefier;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;

/**
 * Created by CF on 2017-04-13.
 */
public class LiquefierRecipe {
    public final Item input;
    public final Fluid output;
    public final int inputStackSize;
    public final int outputQuantity;

    public LiquefierRecipe(Block input, Fluid output, int outputQuantity) {
        this(input, 1, output, outputQuantity);
    }

    public LiquefierRecipe(Block input, int inputStackSize, Fluid output, int outputQuantity) {
        this(Item.getItemFromBlock(input), inputStackSize, output, outputQuantity);
    }

    public LiquefierRecipe(Item input, Fluid output, int outputQuantity) {
        this(input, 1, output, outputQuantity);
    }

    public LiquefierRecipe(Item input, int inputStackSize, Fluid output, int outputQuantity){
        this.input = input;
        this.inputStackSize = inputStackSize;
        this.output = output;
        this.outputQuantity = outputQuantity;
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setTag("input", new ItemStack(this.input, this.inputStackSize).serializeNBT());
        nbt.setTag("output", new FluidStack(this.output, this.outputQuantity).writeToNBT(new NBTTagCompound()));

        return nbt;
    }

    public static LiquefierRecipe deserializeNBT(NBTTagCompound nbt) {
        if ((nbt == null) || !nbt.hasKey("input", Constants.NBT.TAG_COMPOUND) || !nbt.hasKey("output", Constants.NBT.TAG_COMPOUND)) {
            TeslaThingiesMod.logger.error("Incorrect NBT storage for Liquefier Recipe.", nbt);
            return null;
        }
        else {
            ItemStack input = new ItemStack(nbt.getCompoundTag("input"));
            FluidStack output = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("output"));
            return new LiquefierRecipe(input.getItem(), ItemStackUtil.getSize(input), output.getFluid(), output.amount);
        }
    }
}
