package net.ndrei.teslapoweredthingies.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by CF on 2017-01-07.
 */
public class SecondaryOutput {
    public final float chance;
    public final ItemStack stack;

    public SecondaryOutput(float chance, Item item) {
        this(chance, new ItemStack(item));
    }

    public SecondaryOutput(float chance, ItemStack stack){
        this.chance = chance;
        this.stack = stack;
    }
}
