package net.ndrei.teslapoweredthingies.integrations.crafttweaker

import net.ndrei.teslapoweredthingies.api.IPoweredRecipe
import net.ndrei.teslapoweredthingies.api.IPoweredRegistry

open class BaseRegistryClearAction<T: IPoweredRecipe<T>>(registry: IPoweredRegistry<T>)
    : BaseRegistryAction<T>(registry, "Clear") {

    override fun apply(registry: IPoweredRegistry<T>) {
        registry.registry?.clear()
    }
}
