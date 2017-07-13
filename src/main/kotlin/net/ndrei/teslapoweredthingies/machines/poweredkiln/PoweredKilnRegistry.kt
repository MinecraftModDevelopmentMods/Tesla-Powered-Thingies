package net.ndrei.teslapoweredthingies.machines.poweredkiln

import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.AfterAllModsRegistry
import net.ndrei.teslacorelib.IAfterAllModsRegistry

/**
 * Created by CF on 2017-07-06.
 */
@AfterAllModsRegistry
object PoweredKilnRegistry : IAfterAllModsRegistry {
    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        // register furnace recipes
        FurnaceRecipes.instance().smeltingList.forEach { input, output -> PoweredKilnRecipes.registerRecipe(input, output) }
    }
}