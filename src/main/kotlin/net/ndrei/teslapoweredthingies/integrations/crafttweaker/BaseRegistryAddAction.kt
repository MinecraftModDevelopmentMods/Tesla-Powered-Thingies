package net.ndrei.teslapoweredthingies.integrations.crafttweaker

import net.ndrei.teslapoweredthingies.api.IPoweredRecipe
import net.ndrei.teslapoweredthingies.api.IPoweredRegistry

open class BaseRegistryAddAction<T: IPoweredRecipe<T>>(registry: IPoweredRegistry<T>, private val recipeGetter: () -> T)
    : BaseRegistryAction<T>(registry, "Adding to") {

    constructor(registry: IPoweredRegistry<T>, recipe: T): this(registry, { recipe })

    override fun apply(registry: IPoweredRegistry<T>) {
        registry.addRecipe(this.recipeGetter())
    }
}
