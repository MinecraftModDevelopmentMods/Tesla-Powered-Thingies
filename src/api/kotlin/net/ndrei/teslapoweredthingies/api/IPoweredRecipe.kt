package net.ndrei.teslapoweredthingies.api

import net.minecraftforge.registries.IForgeRegistryEntry

interface IPoweredRecipe<T: IPoweredRecipe<T>> : IForgeRegistryEntry<T>