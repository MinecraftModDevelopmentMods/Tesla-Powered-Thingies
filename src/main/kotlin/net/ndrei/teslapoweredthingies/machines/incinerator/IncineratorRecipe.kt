package net.ndrei.teslapoweredthingies.machines.incinerator

import net.minecraft.item.ItemStack
import net.ndrei.teslapoweredthingies.common.SecondaryOutput

/**
 * Created by CF on 2017-06-30.
 */
class IncineratorRecipe(val input: ItemStack, val power: Long, val secondaryOutputs: Array<SecondaryOutput>) {
    constructor(input: ItemStack, power: Long, secondary: SecondaryOutput? = null)
            : this(input, power, if (secondary == null) arrayOf() else arrayOf(secondary))
}
