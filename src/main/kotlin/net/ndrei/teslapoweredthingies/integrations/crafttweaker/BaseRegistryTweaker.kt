@file:Suppress("MemberVisibilityCanPrivate")

package net.ndrei.teslapoweredthingies.integrations.crafttweaker

import crafttweaker.CraftTweakerAPI
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.ndrei.teslapoweredthingies.api.IPoweredRecipe
import net.ndrei.teslapoweredthingies.api.IPoweredRegistry
import stanhebben.zenscript.annotations.ZenMethod

@Suppress("unused")
abstract class BaseRegistryTweaker<R: IPoweredRecipe<R>>(protected val registry: IPoweredRegistry<R>) {
    private val actionsCache = mutableListOf<BaseRegistryAction<R>>()
    private var registrationCompleted = false

    init {
        @Suppress("LeakingThis")
        MinecraftForge.EVENT_BUS.register(this)
    }

    protected fun addDelayedAction(action: BaseRegistryAction<R>) {
        if (!this.registrationCompleted) {
            this.actionsCache.add(action)
        }
        else {
            action.apply()
        }
    }

    protected fun runRegistrations() {
        this.registrationCompleted = true
        this.actionsCache.forEach { it.apply() }
        this.actionsCache.clear()
    }

    private class Add<T: IPoweredRecipe<T>>(tweaker: BaseRegistryTweaker<T>, getter: () -> T)
        : BaseRegistryAddAction<T>(tweaker.registry, getter)

    private class Clear<T: IPoweredRecipe<T>>(tweaker: BaseRegistryTweaker<T>)
        : BaseRegistryClearAction<T>(tweaker.registry)

    private class Remove<T: IPoweredRecipe<T>>(tweaker: BaseRegistryTweaker<T>, key: ResourceLocation)
        : BaseRegistryRemoveAction<T>(tweaker.registry, key)

    private class LogKeys<T: IPoweredRecipe<T>>(tweaker: BaseRegistryTweaker<T>)
        : BaseRegistryAction<T>(tweaker.registry, "Logging keys of") {
        override fun apply(registry: IPoweredRegistry<T>) {
            CraftTweakerAPI.logCommand(this.describe())
            registry.registry!!.keys.forEach {
                CraftTweakerAPI.logCommand(it.toString())
            }
            CraftTweakerAPI.logCommand("<${this.describe()}> finished.")
        }
    }

    fun add(recipe: R) { this.addDelayedAction(Add(this, { recipe })) }
    fun add(getter: () -> R) { this.addDelayedAction(Add(this, getter ))}

    @ZenMethod
    fun clear() { this.addDelayedAction(Clear(this)) }

    @ZenMethod
    fun removeRecipe(key: String) { this.removeRecipe(ResourceLocation(key)) }
    fun removeRecipe(key: ResourceLocation) { this.addDelayedAction(Remove(this, key)) }

    fun replaceRecipe(key: String, recipe: R) { this.removeRecipe(key); this.add(recipe) }
    fun replaceRecipe(key: ResourceLocation, recipe: R) { this.removeRecipe(key); this.add(recipe) }

    fun replaceRecipe(key: String, getter: () -> R) { this.removeRecipe(key); this.add(getter) }
    fun replaceRecipe(key: ResourceLocation, getter: () -> R) { this.removeRecipe(key); this.add(getter) }

    @ZenMethod
    fun logKeys() { this.addDelayedAction(LogKeys(this)) }
}
