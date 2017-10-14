package net.ndrei.teslapoweredthingies.machines.poweredkiln

import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.ndrei.teslapoweredthingies.MOD_ID
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistryEntry

/**
 * Created by CF on 2017-07-06.
 */
class PoweredKilnRecipe(name: ResourceLocation, val input: ItemStack, val output: ItemStack)
    : BaseTeslaRegistryEntry<PoweredKilnRecipe>(PoweredKilnRecipe::class.java, name) {

    constructor(input: ItemStack, output: ItemStack)
        : this(ResourceLocation(MOD_ID, input.item.registryName.toString().replace(':', '_')), input, output)
}