package net.ndrei.teslapoweredthingies.machines.incinerator

import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.ndrei.teslapoweredthingies.MOD_ID
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistryEntry
import net.ndrei.teslapoweredthingies.common.SecondaryOutput

/**
 * Created by CF on 2017-06-30.
 */
class IncineratorRecipe(name: ResourceLocation, val input: ItemStack, val power: Long, val secondaryOutputs: Array<SecondaryOutput>)
    : BaseTeslaRegistryEntry<IncineratorRecipe>(IncineratorRecipe::class.java, name) {

    constructor(input: ItemStack, power: Long, secondary: SecondaryOutput? = null)
        : this(input, power, if (secondary == null) arrayOf() else arrayOf(secondary))

    constructor(input: ItemStack, power: Long, secondaryOutputs: Array<SecondaryOutput>)
        : this(ResourceLocation(MOD_ID, input.item.registryName.toString().replace(':', '_')), input, power, secondaryOutputs)
}
