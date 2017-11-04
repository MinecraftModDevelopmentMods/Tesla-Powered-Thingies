package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslapoweredthingies.MOD_ID
import net.ndrei.teslapoweredthingies.api.itemcompoundproducer.IItemCompoundProducerRecipe
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistryEntry

/**
 * Created by CF on 2017-07-13.
 */
class ItemCompoundProducerRecipe(val name: ResourceLocation, override val inputStack: ItemStack, override val inputFluid: FluidStack, override val result: ItemStack)
    : BaseTeslaRegistryEntry<ItemCompoundProducerRecipe>(ItemCompoundProducerRecipe::class.java, name)
    , IItemCompoundProducerRecipe<ItemCompoundProducerRecipe> {

    constructor(inputStack: ItemStack, inputFluid: FluidStack, result: ItemStack)
        : this(result.item.registryName!!, inputStack, inputFluid, result)

    constructor(name: String, inputStack: ItemStack, inputFluid: FluidStack, result: ItemStack)
        : this(ResourceLocation(MOD_ID, name), inputStack, inputFluid, result)
}
