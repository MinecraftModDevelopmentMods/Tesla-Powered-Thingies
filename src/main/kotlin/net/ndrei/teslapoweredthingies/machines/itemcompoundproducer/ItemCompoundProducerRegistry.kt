package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import net.minecraft.item.Item
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslapoweredthingies.fluids.MoltenTeslaFluid
import net.ndrei.teslapoweredthingies.items.BaseColoredTeslaLump
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRecipes

/**
 * Created by CF on 2017-07-13.
 */
@RegistryHandler
object ItemCompoundProducerRegistry : IRegistryHandler {
    private val registeredLumps = mutableListOf<BaseColoredTeslaLump>()

    override fun registerItems(asm: ASMDataTable, registry: IForgeRegistry<Item>) {
        // get ores
        val ores = OreDictionary.getOreNames()
                .filter { it.startsWith("ore") }
                .map { it.substring(3).decapitalize() }
        ores.forEach {
            val color = MaterialColors.getColor(it)
            if (color != null) {
                val lump = BaseColoredTeslaLump(it, color)
                registry.register(lump)
                this.registeredLumps.add(lump)
            }
        }
    }

    override fun registerRenderers(asm: ASMDataTable) {
        this.registeredLumps.forEach { it.registerRenderer() }
        this.registeredLumps.clear()
    }

    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        val ores = OreDictionary.getOreNames()
                .filter { it.startsWith("teslaLump") }
                .map { it.substring("teslaLump".length) }
        ores.forEach {
            val lump = OreDictionary.getOres("teslaLump$it")?.firstOrNull()
            if ((lump != null) && !lump.isEmpty) {
                 OreDictionary.getOres("ore$it").forEach { ore ->
                     if ((ore != null) && !ore.isEmpty) {
                         // add ore -> lump recipe
                         ItemCompoundProducerRecipes.recipes.add(
                                 ItemCompoundProducerRecipe(
                                         ItemStackUtil.copyWithSize(ore, 1),
                                         FluidStack(MoltenTeslaFluid, 100),
                                         ItemStackUtil.copyWithSize(lump, 2)
                                 )
                         )

                         // lump -> dust recipes
                         PowderMakerRecipes.registerDefaultOreRecipe(
                                 it.decapitalize(), lump, false)
                     }
                 }
            }
        }
    }
}