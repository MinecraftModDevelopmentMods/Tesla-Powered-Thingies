package net.ndrei.teslapoweredthingies.machines.incinerator;

import net.minecraft.item.Item;
import net.ndrei.teslapoweredthingies.common.SecondaryOutput;

/**
 * Created by CF on 2017-01-07.
 */
public class IncineratorRecipe {
    public final Item input;
    public final long power;
    public final SecondaryOutput[] secondaryOutputs;

    public IncineratorRecipe(Item input, long power) {
        this(input, power, (SecondaryOutput) null);
    }

    public IncineratorRecipe(Item input, long power, SecondaryOutput secondary) {
        this(input, power, (secondary == null) ? null : new SecondaryOutput[] { secondary });
    }

    public IncineratorRecipe(Item input, long power, SecondaryOutput[] secondaries) {
        this.input = input;
        this.power = power;
        this.secondaryOutputs = secondaries;
    }
}
