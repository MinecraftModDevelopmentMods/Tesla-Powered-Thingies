package net.ndrei.teslapoweredthingies.common

import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by CF on 2017-06-30.
 */
class SecondaryOutput(val chance: Float, val stack: ItemStack) {
    constructor(chance: Float, item: Item)
            : this(chance, ItemStack(item))
}
