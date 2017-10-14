package net.ndrei.teslapoweredthingies.common

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.minecraftforge.registries.IForgeRegistryInternal
import net.minecraftforge.registries.RegistryManager

abstract class BaseTeslaRegistryEntry<T: BaseTeslaRegistryEntry<T>>(
    private val type: Class<T>,
    private var registryName: ResourceLocation? = null)
    : IForgeRegistryEntry<T> {

    override final fun getRegistryType() = this.type
    override final fun getRegistryName() = this.registryName
    override final fun setRegistryName(name: ResourceLocation?): T = this.type.cast(this).also { it.registryName = name }
}
