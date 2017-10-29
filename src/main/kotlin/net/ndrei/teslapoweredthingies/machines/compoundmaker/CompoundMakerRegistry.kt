package net.ndrei.teslapoweredthingies.machines.compoundmaker

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.config.readFluidStack
import net.ndrei.teslacorelib.config.readItemStack
import net.ndrei.teslacorelib.utils.isEmpty
import net.ndrei.teslacorelib.utils.stack
import net.ndrei.teslapoweredthingies.api.PoweredThingiesAPI
import net.ndrei.teslapoweredthingies.api.compoundmaker.ICompoundMakerRegistry
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.common.OreOutput
import net.ndrei.teslapoweredthingies.common.SecondaryOreOutput
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile
import net.ndrei.teslapoweredthingies.items.BaseAugmentedLump
import net.ndrei.teslapoweredthingies.items.TeslifiedObsidian
import net.ndrei.teslapoweredthingies.machines.itemcompoundproducer.ItemCompoundProducerRecipe
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRecipe
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRegistry

@RegistryHandler
object CompoundMakerRegistry
    : BaseTeslaRegistry<CompoundMakerRecipe>("compound_maker_recipes", CompoundMakerRecipe::class.java), ICompoundMakerRegistry<CompoundMakerRecipe> {

    private val registeredAugments = mutableListOf<BaseAugmentedLump>()

    private var finished = false
    private var itemCompoundFinished = false

    override fun construct(asm: ASMDataTable) {
        super.construct(asm)
        PoweredThingiesAPI.compoundMakerRegistry = this
    }

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

        this.finished = true
        this.checkCompletion()
    }

    @SubscribeEvent
    @Suppress("unused")
    fun onItemCompoundRecipeAdded(ev: BaseTeslaRegistry.EntryAddedEvent<ItemCompoundProducerRecipe>) {
        val recipe = ev.entry
        if (this.getRecipe(recipe.name) == null) {
            this.addRecipe(CompoundMakerRecipe(recipe.name, recipe.result, recipe.inputFluid, arrayOf(recipe.inputStack)))
        }
    }

    @SubscribeEvent
    @Suppress("unused")
    fun onItemCompoundFinished(ev: BaseTeslaRegistry.DefaultRegistrationCompletedEvent<ItemCompoundProducerRecipe>) {
        this.itemCompoundFinished = true
        this.checkCompletion()
    }

    private fun checkCompletion() {
        if (this.finished && this.itemCompoundFinished) {
            this.registrationCompleted()
        }
    }

    override fun registerRecipe(output: ItemStack,
                                left: FluidStack?,
                                top: Array<ItemStack>,
                                right: FluidStack?,
                                bottom: Array<ItemStack>) = this.also {
        this.addRecipe(CompoundMakerRecipe(output.item.registryName!!, output, left, top, right, bottom))
    }
}
