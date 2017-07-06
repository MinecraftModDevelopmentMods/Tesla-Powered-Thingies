package net.ndrei.teslapoweredthingies.machines.poweredkiln

import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.ndrei.teslacorelib.AfterAllModsRegistry
import net.ndrei.teslacorelib.IAfterAllModsRegistry

/**
 * Created by CF on 2017-07-06.
 */
@AfterAllModsRegistry
object PoweredKilnRegistry : IAfterAllModsRegistry {
    override fun registerAfterMaterials(asm: ASMDataTable) {
        // register furnace recipes
        FurnaceRecipes.instance().smeltingList.forEach { input, output -> PoweredKilnRecipes.registerRecipe(input, output) }
    }
}