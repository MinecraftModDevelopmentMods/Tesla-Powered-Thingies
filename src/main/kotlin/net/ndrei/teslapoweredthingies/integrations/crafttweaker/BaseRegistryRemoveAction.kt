package net.ndrei.teslapoweredthingies.integrations.crafttweaker

import net.minecraft.util.ResourceLocation
import net.ndrei.teslapoweredthingies.api.IPoweredRecipe
import net.ndrei.teslapoweredthingies.api.IPoweredRegistry

open class BaseRegistryRemoveAction<T: IPoweredRecipe<T>>(registry: IPoweredRegistry<T>, private val key: ResourceLocation)
    : BaseRegistryAction<T>(registry, "Remove from") {

    override fun apply(registry: IPoweredRegistry<T>) {
        registry.removeRecipe(this.key)
    }
}
