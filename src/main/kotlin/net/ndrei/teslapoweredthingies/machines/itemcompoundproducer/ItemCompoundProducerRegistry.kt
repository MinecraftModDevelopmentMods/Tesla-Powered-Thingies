package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ModClassLoader
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.ModContainerFactory
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.config.readFluidStack
import net.ndrei.teslacorelib.config.readItemStack
import net.ndrei.teslacorelib.config.readItemStacks
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile
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
        val lumpNames = mutableSetOf<String>()
        arrayOf("basemetals", "modernmetals").forEach { modId ->
            if (Loader.isModLoaded(modId)) {
                Block.REGISTRY.keys
                    .filter { (it.resourceDomain == modId) && it.resourcePath.endsWith("_ore") }
                    .map { it.resourcePath }
                    .mapTo(lumpNames) { it.substring(0, it.length - 4) }
            }
        }
        OreDictionary.getOreNames()
            .filter { it.startsWith("ore") }
            .mapTo(lumpNames) { it.substring(3).decapitalize() }
        lumpNames.forEach {
            val color = MaterialColors.getColor(it)
            if (color != null) {
                val lump = BaseColoredTeslaLump(it, color)
                lump.registerItem(registry)
                OreDictionary.registerOre("teslaLump${it.capitalize()}", lump)
                this.registeredLumps.add(lump)
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun registerRenderers(asm: ASMDataTable) {
        this.registeredLumps.forEach { it.registerRenderer() }
    }

    override fun postInit(asm: ASMDataTable) {
        if (TeslaCoreLib.isClientSide) {
            this.registeredLumps.forEach {
                Minecraft.getMinecraft().itemColors.registerItemColorHandler({ s: ItemStack, t: Int -> it.getColorFromItemStack(s, t) }, arrayOf(it))
            }
        }
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
                         PowderMakerRecipes.registerDefaultOreRecipe(it.decapitalize(), lump, false)
                     }
                 }
            }
        }

        readExtraRecipesFile(ItemCompoundProducerBlock.registryName!!.resourcePath) { json ->
            val stacks = json.readItemStacks("input_stack")
            if (stacks.isNotEmpty()) {
                val fluid = json.readFluidStack("input_fluid") ?: return@readExtraRecipesFile
                val output = json.readItemStack("output") ?: return@readExtraRecipesFile

                stacks.mapTo(ItemCompoundProducerRecipes.recipes) {
                    ItemCompoundProducerRecipe(it, fluid, output)
                }
            }
        }
    }
}
