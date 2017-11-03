package net.ndrei.teslapoweredthingies.integrations.crafttweaker

import net.ndrei.teslapoweredthingies.api.IPoweredRecipe
import net.ndrei.teslapoweredthingies.api.IPoweredRegistry

abstract class BaseRegistryAction<T: IPoweredRecipe<T>>(protected val registry: IPoweredRegistry<T>, private val action: String) : BaseCTAction() {
    override fun describe() = "${this.action.capitalize()} ${this.registry.registryName}"

    override final fun apply() {
        this.apply(this.registry)
    }

    abstract protected fun apply(registry: IPoweredRegistry<T>)
}
