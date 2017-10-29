package net.ndrei.teslapoweredthingies.common

import net.minecraft.util.ResourceLocation
import net.ndrei.teslapoweredthingies.api.IPoweredRecipe

abstract class BaseTeslaRegistryEntry<T: BaseTeslaRegistryEntry<T>>(
    private val type: Class<T>,
    private var registryName: ResourceLocation? = null)
    : IPoweredRecipe<T> {

    override final fun getRegistryType() = this.type
    override final fun getRegistryName() = this.registryName
    override final fun setRegistryName(name: ResourceLocation?): T = this.type.cast(this).also { it.registryName = name }
}
