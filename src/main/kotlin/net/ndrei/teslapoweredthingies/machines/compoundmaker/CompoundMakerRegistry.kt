package net.ndrei.teslapoweredthingies.machines.compoundmaker

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryBuilder
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslapoweredthingies.MOD_ID
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.machines.itemcompoundproducer.ItemCompoundProducerRecipe

@RegistryHandler
object CompoundMakerRegistry
    : BaseTeslaRegistry<CompoundMakerRecipe>("compoung_maker_recipes", CompoundMakerRecipe::class.java) {

    fun acceptsLeft(fluid: FluidStack) = this.hasRecipe { it.matchesLeft(fluid, true, false) }
    fun acceptsRight(fluid: FluidStack) = this.hasRecipe { it.matchedRight(fluid, true, false) }
    fun acceptsTop(stack: ItemStack) = this.hasRecipe { it.matchesTop(stack, true, false) }
    fun acceptsBottom(stack: ItemStack) = this.hasRecipe { it.matchesBottom(stack, true, false) }

    fun findRecipes(left: IFluidTank, top: IItemHandler, right: IFluidTank, bottom: IItemHandler) =
        this.findRecipes { it.matches(left, top, right, bottom) }

    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        val recipes = this.registry

    }

    @SubscribeEvent
    fun onItemCompoundRecipeAdded(ev: BaseTeslaRegistry.EntryAddedEvent<ItemCompoundProducerRecipe>) {
        val recipe = ev.entry
        if (this.getRecipe(recipe.name) == null) {
            this.addRecipe(CompoundMakerRecipe(recipe.name, recipe.result, recipe.inputFluid, arrayOf(recipe.inputStack)))
        }
    }
}
