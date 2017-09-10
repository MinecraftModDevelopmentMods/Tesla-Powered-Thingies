package net.ndrei.teslapoweredthingies.machines.poweredkiln

import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.config.readItemStacks
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile

@RegistryHandler
object PoweredKilnRegistry : IRegistryHandler {
    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        readExtraRecipesFile(PoweredKilnBlock.registryName!!.resourcePath) { json ->
            val input = json.readItemStacks("input_stack")
            if (input.isNotEmpty()) {
                val output = json.readItemStacks("output_stack").firstOrNull()
                if (output != null) {
                    input.forEach {
                        PoweredKilnRecipes.registerRecipe(it, output)
                    }
                }
            }
        }
    }
}