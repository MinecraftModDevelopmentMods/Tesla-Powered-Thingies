package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile
import net.ndrei.teslapoweredthingies.config.readFluidStack

/**
 * Created by CF on 2017-07-13.
 */
@RegistryHandler
object FluidCompoundProducerRegistry : IRegistryHandler {
    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
//        FluidCompoundProducerRecipes.recipes.add(
//                FluidCompoundProducerRecipe(
//                        FluidStack(FluidRegistry.WATER, 100),
//                        FluidStack(FluidRegistry.LAVA, 100),
//                        FluidStack(MoltenTeslaFluid, 100)
//                )
//        )

        readExtraRecipesFile(FluidCompoundProducerBlock.registryName!!.resourcePath) { json ->
            val inputA = json.readFluidStack("input_a") ?: return@readExtraRecipesFile
            val inputB = json.readFluidStack("input_b") ?: return@readExtraRecipesFile
            val output = json.readFluidStack("output") ?: return@readExtraRecipesFile

            FluidCompoundProducerRecipes.recipes.add(
                    FluidCompoundProducerRecipe(inputA, inputB, output)
            )
        }
    }
}
