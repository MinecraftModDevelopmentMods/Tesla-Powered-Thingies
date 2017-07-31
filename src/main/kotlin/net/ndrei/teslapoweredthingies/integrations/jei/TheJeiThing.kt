package net.ndrei.teslapoweredthingies.integrations.jei

import mezz.jei.api.IJeiRuntime
import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.machines.poweredkiln.PoweredKilnBlock

/**
 * Created by CF on 2017-06-30.
 */
@JEIPlugin
class TheJeiThing : IModPlugin {
    override fun register(registry: IModRegistry) {
        TheJeiThing.blocksMap.values.forEach { it.register(registry) }

        registry.addRecipeCatalyst(ItemStack(PoweredKilnBlock), VanillaRecipeCategoryUid.SMELTING)
    }

    override fun registerCategories(registry: IRecipeCategoryRegistration?) {
        if (registry != null) {
            TheJeiThing.blocksMap.values.forEach { it.register(registry) }
        }
        else
            TeslaThingiesMod.logger.warn("TheJeiThing::registerCategories - Null registry received.")
    }

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime?) {
        if (jeiRuntime != null) {
            TheJeiThing.JEI = jeiRuntime
        }
        else
            TeslaThingiesMod.logger.warn("TheJeiThing::onRuntimeAvailable - Null runtime received.")
    }

    companion object {
        var JEI: IJeiRuntime? = null
            private set

        private val blocksMap = mutableMapOf<Block, BaseCategory<*>>()

        fun registerCategory(category: BaseCategory<*>) {
            blocksMap[category.block] = category
        }

        fun isBlockRegistered(block: Block): Boolean
            = blocksMap.containsKey(block)

        fun showCategory(block: Block) {
            if ((JEI != null) && (blocksMap.containsKey(block))) {
                JEI!!.recipesGui.showCategories(mutableListOf(
                        blocksMap[block]!!.uid
                ))
            }
        }
    }
}
