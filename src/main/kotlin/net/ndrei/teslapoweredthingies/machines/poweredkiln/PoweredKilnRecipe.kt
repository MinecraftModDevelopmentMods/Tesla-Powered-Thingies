package net.ndrei.teslapoweredthingies.machines.poweredkiln

import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.ndrei.teslapoweredthingies.MOD_ID
import net.ndrei.teslapoweredthingies.api.poweredkiln.IPoweredKilnRecipe
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistryEntry

/**
 * Created by CF on 2017-07-06.
 */
class PoweredKilnRecipe(name: ResourceLocation, override val input: ItemStack, override val output: ItemStack)
    : BaseTeslaRegistryEntry<PoweredKilnRecipe>(PoweredKilnRecipe::class.java, name)
    , IPoweredKilnRecipe<PoweredKilnRecipe> {

    constructor(input: ItemStack, output: ItemStack)
        : this(ResourceLocation(MOD_ID, input.item.registryName.toString().replace(':', '_')), input, output)
}
