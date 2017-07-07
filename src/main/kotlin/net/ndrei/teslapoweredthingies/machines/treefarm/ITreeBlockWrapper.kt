package net.ndrei.teslapoweredthingies.machines.treefarm

import net.minecraft.item.ItemStack

/**
 * Created by CF on 2017-07-07.
 */
interface ITreeBlockWrapper {
    fun breakBlock(fortune: Int): List<ItemStack>
}