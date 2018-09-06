package net.ndrei.teslapoweredthingies.api

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryModifiable

interface IPoweredRegistry<T: IPoweredRecipe<T>> {
    val registry: IForgeRegistryModifiable<T>?
    val registryName: ResourceLocation

    val isRegistrationCompleted: Boolean

    fun addRecipe(recipe: T, suffixDuplicates: Boolean = true) = this.also {
        val name = recipe.registryName
        if ((name != null) && (this.getRecipe(name) != null)) {
            var index = 0
            var newName: ResourceLocation
            do {
                index++
                newName = ResourceLocation(name.namespace, "${name.path}_$index")
            } while (this.getRecipe(newName) != null)
            recipe.registryName = newName
        }
        this.registry!!.register(recipe)
    }

    fun hasRecipe(filter: (T) -> Boolean) =
        this.registry?.valuesCollection?.any(filter) ?: false

    fun findRecipes(filter: (T) -> Boolean) =
        this.registry?.valuesCollection?.filter(filter) ?: listOf()

    fun findRecipe(filter: (T) -> Boolean) =
        this.registry?.valuesCollection?.firstOrNull(filter)

    fun getRecipe(name: ResourceLocation) =
        this.registry?.getValue(name)

    fun getAllRecipes(): List<T> =
        this.registry?.valuesCollection?.toList() ?: listOf()

    fun removeRecipe(registration: ResourceLocation) {
        this.registry?.remove(registration)
    }
}
