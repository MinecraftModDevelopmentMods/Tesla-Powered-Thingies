package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.config.readFluidStack
import net.ndrei.teslacorelib.config.readItemStack
import net.ndrei.teslacorelib.config.readItemStacks
import net.ndrei.teslacorelib.utils.equalsIgnoreSize
import net.ndrei.teslapoweredthingies.api.PoweredThingiesAPI
import net.ndrei.teslapoweredthingies.api.itemcompoundproducer.IItemCompoundProducerRegistry
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile
import net.ndrei.teslapoweredthingies.fluids.MoltenTeslaFluid
import net.ndrei.teslapoweredthingies.items.BaseColoredTeslaLump
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRegistry

/**
 * Created by CF on 2017-07-13.
 */
@RegistryHandler
object ItemCompoundProducerRegistry
    : BaseTeslaRegistry<ItemCompoundProducerRecipe>("item_compound_recipes", ItemCompoundProducerRecipe::class.java)
    , IItemCompoundProducerRegistry<ItemCompoundProducerRecipe> {

    override fun construct(asm: ASMDataTable) {
        super.construct(asm)
        PoweredThingiesAPI.itemCompoundProducerRegistry = this
    }

    //#region registration methods

    private val registeredLumps = mutableListOf<BaseColoredTeslaLump>()

    override fun registerItems(asm: ASMDataTable, registry: IForgeRegistry<Item>) {
        val lumpNames = mutableSetOf<String>()
        arrayOf("basemetals", "modernmetals").forEach { modId ->
            if (Loader.isModLoaded(modId)) {
                Block.REGISTRY.keys
                    .filter { (it.namespace == modId) && it.path.endsWith("_ore") }
                    .map { it.path }
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
                        ItemCompoundProducerRegistry.addRecipe(
                            ItemCompoundProducerRecipe(
                                "lump_${it}_${ore.item.registryName.toString().replace(':', '_')}",
                                ItemStackUtil.copyWithSize(ore, 1),
                                FluidStack(MoltenTeslaFluid, 100),
                                ItemStackUtil.copyWithSize(lump, 2)
                            )
                        )

                        // lump -> dust recipes
                        PowderMakerRegistry.registerDefaultOreRecipe(it.decapitalize(), lump, false)
                    }
                }
            }
        }

        readExtraRecipesFile(ItemCompoundProducerBlock.registryName!!.path) { json ->
            val stacks = json.readItemStacks("input_stack")
            if (stacks.isNotEmpty()) {
                val fluid = json.readFluidStack("input_fluid") ?: return@readExtraRecipesFile
                val output = json.readItemStack("output") ?: return@readExtraRecipesFile

                stacks.forEach {
                    ItemCompoundProducerRegistry.addRecipe(ItemCompoundProducerRecipe(it, fluid, output))
                }
            }
        }

        this.registrationCompleted()
    }

    //#endregion

    private fun ItemCompoundProducerRecipe.matchesInput(fluid: FluidStack, ignoreSize: Boolean = true)
        = this.inputFluid.isFluidEqual(fluid) && (ignoreSize || (this.inputFluid.amount <= fluid.amount))

    private fun ItemCompoundProducerRecipe.matchesInput(stack: ItemStack, ignoreSize: Boolean = true)
        = this.inputStack.equalsIgnoreSize(stack) && (ignoreSize || (this.inputStack.count <= stack.count))

    override fun hasRecipe(fluid: FluidStack) = this.hasRecipe { it.matchesInput(fluid) }
    override fun hasRecipe(stack: ItemStack) = this.hasRecipe { it.matchesInput(stack) }

    override fun findRecipe(fluid: FluidStack, stack: ItemStack)
        = this.findRecipe { it.matchesInput(fluid, false) && it.matchesInput(stack, false) }
}
