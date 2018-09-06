package net.ndrei.teslapoweredthingies.machines.incinerator

import com.google.gson.JsonElement
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.JsonUtils
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.config.getLong
import net.ndrei.teslacorelib.config.readItemStack
import net.ndrei.teslacorelib.config.readItemStacks
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.common.SecondaryOutput
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile
import net.ndrei.teslapoweredthingies.items.AshItem

@RegistryHandler
object IncineratorRegistry : BaseTeslaRegistry<IncineratorRecipe>("incinerator_recipes", IncineratorRecipe::class.java) {
    private val VANILLA_BURN_TO_POWER_RATE: Long = 10

    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        this.registerForcedRecipes()

        readExtraRecipesFile(IncineratorBlock.registryName!!.path) { json ->
            val input = json.readItemStacks("input_stack")
            if (input.isNotEmpty()) {
                val power = json.getLong("power", 0)
                if ((power > 0) && json.has("outputs")) {
                    val secondary = json.getAsJsonArray("outputs").mapNotNull<JsonElement, SecondaryOutput> {
                        if (it.isJsonObject) {
                            val stack = it.asJsonObject.readItemStack() ?: return@mapNotNull null
                            val chance = JsonUtils.getFloat(it.asJsonObject, "chance", 1.0f)
                            if (chance > 0.0f) {
                                return@mapNotNull SecondaryOutput(Math.min(chance, 1.0f), stack)
                            }
                        }
                        return@mapNotNull null
                    }
                    if (secondary.isNotEmpty()) {
                        input.forEach {
                            this.addRecipe(IncineratorRecipe(it, power, secondary.toTypedArray()))
                        }
                    }
                }
            }
        }

        super.registrationCompleted()
    }

    private fun registerForcedRecipes() {
        // vanilla recipes
        registerVanillaRecipe(Items.COAL, SecondaryOutput(.02f, AshItem))
        registerVanillaRecipe(Blocks.COAL_BLOCK, SecondaryOutput(.05f, AshItem))
        registerVanillaRecipe(Blocks.PLANKS, SecondaryOutput(.10f, AshItem))
        registerVanillaRecipe(Blocks.LOG, SecondaryOutput(.15f, AshItem))
        registerVanillaRecipe(Blocks.LOG2, SecondaryOutput(.15f, AshItem))
        registerVanillaRecipe(Blocks.WOOL, SecondaryOutput(.01f, AshItem))

        registerVanillaRecipe(Blocks.SAPLING, SecondaryOutput(.15f, AshItem))

        registerVanillaRecipe(Blocks.ACACIA_FENCE, SecondaryOutput(.05f, AshItem))
        registerVanillaRecipe(Blocks.BIRCH_FENCE, SecondaryOutput(.05f, AshItem))
        registerVanillaRecipe(Blocks.JUNGLE_FENCE, SecondaryOutput(.05f, AshItem))
        registerVanillaRecipe(Blocks.OAK_FENCE, SecondaryOutput(.05f, AshItem))
        registerVanillaRecipe(Blocks.SPRUCE_FENCE, SecondaryOutput(.05f, AshItem))
        registerVanillaRecipe(Blocks.DARK_OAK_FENCE, SecondaryOutput(.05f, AshItem))
        registerVanillaRecipe(Blocks.ACACIA_FENCE_GATE, SecondaryOutput(.05f, AshItem))
        registerVanillaRecipe(Blocks.BIRCH_FENCE_GATE, SecondaryOutput(.05f, AshItem))
        registerVanillaRecipe(Blocks.JUNGLE_FENCE_GATE, SecondaryOutput(.05f, AshItem))
        registerVanillaRecipe(Blocks.OAK_FENCE_GATE, SecondaryOutput(.05f, AshItem))
        registerVanillaRecipe(Blocks.SPRUCE_FENCE_GATE, SecondaryOutput(.05f, AshItem))
        registerVanillaRecipe(Blocks.DARK_OAK_FENCE_GATE, SecondaryOutput(.05f, AshItem))

        registerVanillaRecipe(Items.STICK, SecondaryOutput(.01f, AshItem))
        registerVanillaRecipe(Items.WOODEN_AXE, SecondaryOutput(.03f, AshItem))
        registerVanillaRecipe(Items.WOODEN_HOE, SecondaryOutput(.03f, AshItem))
        registerVanillaRecipe(Items.WOODEN_PICKAXE, SecondaryOutput(.03f, AshItem))
        registerVanillaRecipe(Items.WOODEN_SHOVEL, SecondaryOutput(.03f, AshItem))
        registerVanillaRecipe(Items.WOODEN_SWORD, SecondaryOutput(.03f, AshItem))
    }

    private fun registerVanillaRecipe(block: Block, secondary: SecondaryOutput) {
        this.registerVanillaRecipe(Item.getItemFromBlock(block), secondary)
    }

    private fun registerVanillaRecipe(item: Item, secondary: SecondaryOutput) {
        registerVanillaRecipe(ItemStack(item), secondary)
    }

    private fun registerVanillaRecipe(stack: ItemStack, secondary: SecondaryOutput) {
        val burnTime = TileEntityFurnace.getItemBurnTime(stack)
        if (burnTime > 0) {
            val power = burnTime.toLong() * VANILLA_BURN_TO_POWER_RATE
            this.addRecipe(IncineratorRecipe(stack, power, secondary))
        }
    }

    fun isFuel(input: ItemStack): Boolean {
        if (input.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            return false // NO BUCKETS!!
        }

        if (TileEntityFurnace.isItemFuel(ItemStack(input.item))) {
            return true
        }

        return this.hasRecipe { it.input.isItemEqualIgnoreDurability(input) }
    }

    fun getPower(input: ItemStack): Long =
        this.findRecipe { it.input.isItemEqualIgnoreDurability(input) }?.power
            ?: if (isFuel(input)) VANILLA_BURN_TO_POWER_RATE * TileEntityFurnace.getItemBurnTime(input) else 0

    fun getSecondaryOutputs(input: ItemStack) =
        this.findRecipe { it.input.isItemEqualIgnoreDurability(input) } ?.secondaryOutputs ?: arrayOf()
}
