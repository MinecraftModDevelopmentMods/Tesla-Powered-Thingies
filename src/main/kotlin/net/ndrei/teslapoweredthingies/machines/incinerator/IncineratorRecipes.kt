package net.ndrei.teslapoweredthingies.machines.incinerator

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.ndrei.teslapoweredthingies.common.SecondaryOutput
import net.ndrei.teslapoweredthingies.items.AshItem

/**
 * Created by CF on 2017-06-30.
 */
object IncineratorRecipes {
    private val VANILLA_BURN_TO_POWER_RATE: Long = 10
    private val recipes: MutableList<IncineratorRecipe> = mutableListOf()

    fun registerRecipe(recipe: IncineratorRecipe) {
        this.recipes.add(recipe)
    }

    fun registerRecipes() {
        this.recipes.clear()

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
        IncineratorRecipes.registerVanillaRecipe(Item.getItemFromBlock(block), secondary)
    }

    private fun registerVanillaRecipe(item: Item, secondary: SecondaryOutput) {
        registerVanillaRecipe(ItemStack(item), secondary)
    }

    private fun registerVanillaRecipe(stack: ItemStack, secondary: SecondaryOutput) {
        val burnTime = TileEntityFurnace.getItemBurnTime(stack)
        if (burnTime > 0) {
            val power = burnTime.toLong() * VANILLA_BURN_TO_POWER_RATE
            IncineratorRecipes.recipes.add(IncineratorRecipe(stack, power, secondary))
        }
    }

    fun isFuel(input: ItemStack): Boolean {
        if (input.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            return false // NO BUCKETS!!
        }

        if (TileEntityFurnace.isItemFuel(ItemStack(input.item))) {
            return true
        }

        return IncineratorRecipes.recipes
            .any { it.input.isItemEqualIgnoreDurability(input) }
    }

    fun getPower(input: ItemStack): Long =
        IncineratorRecipes.recipes
            .firstOrNull { it.input.isItemEqualIgnoreDurability(input) }
            ?.power
            ?: if (isFuel(input)) VANILLA_BURN_TO_POWER_RATE * TileEntityFurnace.getItemBurnTime(input) else 0

    fun getSecondaryOutputs(input: ItemStack): Array<SecondaryOutput> {
        return IncineratorRecipes.recipes
            .firstOrNull { it.input.isItemEqualIgnoreDurability(input) }
            ?.secondaryOutputs ?: arrayOf()
    }

    fun getRecipes() =  IncineratorRecipes.recipes.toList()
}
