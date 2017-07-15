package net.ndrei.teslapoweredthingies.machines.poweredkiln

import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler

/**
 * Created by CF on 2017-07-06.
 */
@RegistryHandler
object PoweredKilnRegistry : IRegistryHandler {
    override fun postInit(asm: ASMDataTable) {
        // register furnace recipes
        FurnaceRecipes.instance().smeltingList.forEach { input, output -> PoweredKilnRecipes.registerRecipe(input, output) }
    }
}