package net.ndrei.teslapoweredthingies.machines.incinerator;

import net.minecraft.item.ItemStack;
import net.ndrei.teslapoweredthingies.common.SecondaryOutput;

/**
 * Created by CF on 2017-01-07.
 */
public class IncineratorRecipe {
    public final ItemStack input;
    public final long power;
    public final SecondaryOutput[] secondaryOutputs;

    public IncineratorRecipe(ItemStack input, long power) {
        this(input, power, (SecondaryOutput) null);
    }

    public IncineratorRecipe(ItemStack input, long power, SecondaryOutput secondary) {
        this(input, power, (secondary == null) ? null : new SecondaryOutput[] { secondary });
    }

    public IncineratorRecipe(ItemStack input, long power, SecondaryOutput[] secondaries) {
        this.input = input;
        this.power = power;
        this.secondaryOutputs = secondaries;
    }
}
