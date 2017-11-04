package net.ndrei.teslapoweredthingies.api.itemliquefier

import net.minecraft.item.ItemStack
import net.ndrei.teslapoweredthingies.api.IPoweredRegistry

interface IItemLiquefierRegistry<T: IItemLiquefierRecipe<T>>: IPoweredRegistry<T> {
    fun getRecipe(input: ItemStack): T?
}
