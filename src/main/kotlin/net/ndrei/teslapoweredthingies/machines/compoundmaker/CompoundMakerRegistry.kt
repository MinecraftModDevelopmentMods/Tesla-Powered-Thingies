package net.ndrei.teslapoweredthingies.machines.compoundmaker

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryBuilder
import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.config.readFluidStack
import net.ndrei.teslacorelib.config.readItemStack
import net.ndrei.teslacorelib.config.readItemStacks
import net.ndrei.teslacorelib.utils.isEmpty
import net.ndrei.teslacorelib.utils.stack
import net.ndrei.teslapoweredthingies.MOD_ID
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.common.OreOutput
import net.ndrei.teslapoweredthingies.common.SecondaryOreOutput
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile
import net.ndrei.teslapoweredthingies.items.BaseAugmentedLump
import net.ndrei.teslapoweredthingies.items.BaseColoredTeslaLump
import net.ndrei.teslapoweredthingies.items.TeslifiedObsidian
import net.ndrei.teslapoweredthingies.machines.itemcompoundproducer.ItemCompoundProducerBlock
import net.ndrei.teslapoweredthingies.machines.itemcompoundproducer.ItemCompoundProducerRecipe
import net.ndrei.teslapoweredthingies.machines.itemcompoundproducer.ItemCompoundProducerRegistry
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRecipe
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRegistry

@RegistryHandler
object CompoundMakerRegistry
    : BaseTeslaRegistry<CompoundMakerRecipe>("compound_maker_recipes", CompoundMakerRecipe::class.java) {

    private val registeredAugments = mutableListOf<BaseAugmentedLump>()

    fun acceptsLeft(fluid: FluidStack) = this.hasRecipe { it.matchesLeft(fluid, true, false) }
    fun acceptsRight(fluid: FluidStack) = this.hasRecipe { it.matchedRight(fluid, true, false) }
    fun acceptsTop(stack: ItemStack) = this.hasRecipe { it.matchesTop(stack, true, false) }
    fun acceptsBottom(stack: ItemStack) = this.hasRecipe { it.matchesBottom(stack, true, false) }

    fun findRecipes(left: IFluidTank, top: IItemHandler, right: IFluidTank, bottom: IItemHandler) =
        this.findRecipes { it.matches(left, top, right, bottom) }

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
                val aug = BaseAugmentedLump(it, color)
                aug.registerItem(registry)
                OreDictionary.registerOre("augmentedLump${it.capitalize()}", aug)
                this.registeredAugments.add(aug)
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun registerRenderers(asm: ASMDataTable) {
        this.registeredAugments.forEach { it.registerRenderer() }
    }

    override fun postInit(asm: ASMDataTable) {
        if (TeslaCoreLib.isClientSide) {
            this.registeredAugments.forEach {
                Minecraft.getMinecraft().itemColors.registerItemColorHandler({ s: ItemStack, t: Int -> it.getColorFromItemStack(s, t) }, arrayOf(it))
            }
        }
    }

    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        val recipes = this.registry ?: return

        this.registeredAugments.forEach {
            this.addRecipe(CompoundMakerRecipe(it.registryName!!, it.stack(),
                top = arrayOf(it.lump),
                bottom = arrayOf(TeslifiedObsidian.stack())))
            PowderMakerRegistry.addRecipe(PowderMakerRecipe(it.registryName!!, listOf(it.stack()),
                listOf(
                    OreOutput("dust${it.material.capitalize()}", 2),
                    SecondaryOreOutput(.24f, "dust${it.material.capitalize()}", 1)
                )))
        }

        readExtraRecipesFile(CompoundMakerBlock.registryName!!.resourcePath) { json ->
            val topStacks = (if (json.has("top_stacks"))
                json.getAsJsonArray("top_stacks").map { it.asJsonObject.readItemStack() }
            else listOf()).filterNotNull()

            val bottomStacks = (if (json.has("bottom_stacks"))
                json.getAsJsonArray("bottom_stacks").map { it.asJsonObject.readItemStack() }
            else listOf()).filterNotNull()

            val leftFluid = json.readFluidStack("left_fluid")
            val rightFluid = json.readFluidStack("right_fluid")

            val result = json.readItemStack("output")
            if ((result?.isEmpty == false) &&
                (topStacks.isNotEmpty() || bottomStacks.isNotEmpty() || !leftFluid.isEmpty || !rightFluid.isEmpty)) {

                recipes.register(CompoundMakerRecipe(result.item.registryName!!, result,
                    leftFluid, topStacks.toTypedArray(), rightFluid, bottomStacks.toTypedArray()))
            }
        }
    }

    @SubscribeEvent
    fun onItemCompoundRecipeAdded(ev: BaseTeslaRegistry.EntryAddedEvent<ItemCompoundProducerRecipe>) {
        val recipe = ev.entry
        if (this.getRecipe(recipe.name) == null) {
            this.addRecipe(CompoundMakerRecipe(recipe.name, recipe.result, recipe.inputFluid, arrayOf(recipe.inputStack)))
        }
    }
}
