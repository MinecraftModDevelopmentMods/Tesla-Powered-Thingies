package net.ndrei.teslapoweredthingies.common

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.eventhandler.GenericEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.*
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslapoweredthingies.MOD_ID

abstract class BaseTeslaRegistry<T: IForgeRegistryEntry<T>>(registryName: String, private val type: Class<T>) : IRegistryHandler {
    protected val REGISTRY_NAME = ResourceLocation(MOD_ID, registryName)

    override fun construct(asm: ASMDataTable) {
        super.construct(asm)
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun registerRegistry(ev: RegistryEvent.NewRegistry) {
        RegistryBuilder<T>()
            .setName(REGISTRY_NAME)
            .setMaxID(MAX_RECIPE_ID)
            .setType(this.type)
            .add(AddCallback(this.type))
            .add(ClearCallback(this.type))
            .disableSaving()
            .allowModification()
            .create()
    }

    class AddCallback<T : IForgeRegistryEntry<T>>(private val type: Class<T>) : IForgeRegistry.AddCallback<T> {
        override fun onAdd(owner: IForgeRegistryInternal<T>?, stage: RegistryManager?, id: Int, obj: T, oldObj: T?) {
            MinecraftForge.EVENT_BUS.post(EntryAddedEvent(this.type, obj))
        }
    }

    class ClearCallback<T : IForgeRegistryEntry<T>>(private val type: Class<T>) : IForgeRegistry.ClearCallback<T> {
        override fun onClear(owner: IForgeRegistryInternal<T>?, stage: RegistryManager?) {
            if (owner != null) {
                MinecraftForge.EVENT_BUS.post(RegistryClearEvent(this.type, owner))
            }
        }
    }

    class EntryAddedEvent<T : IForgeRegistryEntry<T>> internal constructor(type: Class<T>, val entry: T) : GenericEvent<T>(type)
    class RegistryClearEvent<T : IForgeRegistryEntry<T>> internal constructor(type: Class<T>, val registry: IForgeRegistry<T>) : GenericEvent<T>(type)

    // just for the ease of remembered the event name
    open fun registerThings(ev: RegistryEvent.Register<T>) { }

    val registry get(): IForgeRegistry<T>? = GameRegistry.findRegistry(this.type)!!

    fun addRecipe(recipe: T, suffixDuplicates: Boolean = true) = this.also {
        val name = recipe.registryName
        if ((name != null) && (this.getRecipe(name) != null)) {
            var index = 0
            var newName: ResourceLocation
            do {
                index++
                newName = ResourceLocation(name.resourceDomain, "${name.resourcePath}_${index}")
            } while (this.getRecipe(newName) != null)
            recipe.registryName = newName
        }
        this.registry!!.register(recipe)
    }

    fun hasRecipe(filter: (T) -> Boolean) =
        this.registry?.values?.any(filter) ?: false

    fun findRecipes(filter: (T) -> Boolean) =
        this.registry?.values?.filter(filter) ?: listOf()

    fun findRecipe(filter: (T) -> Boolean) =
        this.registry?.values?.firstOrNull(filter)

    fun getRecipe(name: ResourceLocation) =
        this.registry?.getValue(name)

    fun getAllRecipes(): List<T> =
        this.registry?.values ?: listOf()

    companion object {
        const val MAX_RECIPE_ID = Integer.MAX_VALUE shr 5
    }
}