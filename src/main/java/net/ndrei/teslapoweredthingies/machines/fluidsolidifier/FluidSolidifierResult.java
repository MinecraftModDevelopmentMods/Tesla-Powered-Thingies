package net.ndrei.teslapoweredthingies.machines.fluidsolidifier;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * Created by CF on 2017-01-15.
 */
public enum FluidSolidifierResult {
    COBBLESTONE(0, new ItemStack(Blocks.COBBLESTONE), 30, 1000, 0, 1000, 0),
    STONE(1, new ItemStack(Blocks.STONE), 40, 1000, 125, 1000, 125),
    OBSIDIAN(2, new ItemStack(Blocks.OBSIDIAN), 900, 1000, 1000, 1000, 1000);

    public final ItemStack resultStack;
    public final int stateIndex;
    public final int waterMbMin;
    public final int lavaMbMin;
    public final int waterMbConsumed;
    public final int lavaMbConsumed;
    public final int ticksRequired;

    public static final FluidSolidifierResult[] VALUES;

    static {
        VALUES = new FluidSolidifierResult[3];
        for(FluidSolidifierResult v : values()) {
            VALUES[v.stateIndex] = v;
        }
    }

    FluidSolidifierResult(int stateIndex, ItemStack resultStack, int ticksRequired, int waterMbMin, int waterMbConsumed, int lavaMbMin, int lavaMbConsumed) {
        this.stateIndex = stateIndex;
        this.resultStack = resultStack;
        this.ticksRequired = ticksRequired;
        this.waterMbMin = waterMbMin;
        this.waterMbConsumed = waterMbConsumed;
        this.lavaMbMin = lavaMbMin;
        this.lavaMbConsumed = lavaMbConsumed;
    }

    public static FluidSolidifierResult fromStateIndex(int ordinal) {
        return VALUES[ordinal];
    }
}
