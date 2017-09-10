package net.ndrei.teslapoweredthingies.machines.incinerator

import com.google.gson.JsonElement
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.JsonUtils
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.config.getLong
import net.ndrei.teslacorelib.config.readItemStack
import net.ndrei.teslacorelib.config.readItemStacks
import net.ndrei.teslapoweredthingies.common.SecondaryOutput
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile

@RegistryHandler
object IncineratorRegistry : IRegistryHandler {
    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        IncineratorRecipes.registerRecipes()

        readExtraRecipesFile(IncineratorBlock.registryName!!.resourcePath) { json ->
            val input = json.readItemStacks("input_stack")
            if (input.isNotEmpty()) {
                val power = json.getLong("power", 0)
                if ((power > 0) && json.has("outputs")) {
                    val secondary = json.getAsJsonArray("outputs").mapNotNull<JsonElement, SecondaryOutput> {
                        if (it.isJsonObject) {
                            val stack = it.asJsonObject.readItemStack() ?: return@mapNotNull null
                            val chance = JsonUtils.getFloat(it.asJsonObject, "chance", 1.0f)
                            if (chance > 0.0f) {
                                return@mapNotNull SecondaryOutput(Math.min(chance, 1.0f), stack)
                            }
                        }
                        return@mapNotNull null
                    }
                    if (secondary.isNotEmpty()) {
                        input.forEach {
                            IncineratorRecipes.registerRecipe(IncineratorRecipe(it, power, secondary.toTypedArray()))
                        }
                    }
                }
            }
        }
    }
}
