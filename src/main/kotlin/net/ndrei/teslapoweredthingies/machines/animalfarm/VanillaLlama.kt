package net.ndrei.teslapoweredthingies.machines.animalfarm

import net.minecraft.entity.passive.EntityLlama
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by CF on 2017-07-07.
 */
class VanillaLlama(llama: EntityLlama)
    : VanillaGenericAnimal(llama) {

    override fun isFood(stack: ItemStack): Boolean {
        return stack.item === Items.WHEAT || stack.item === Item.getItemFromBlock(Blocks.HAY_BLOCK)
    }

    override fun getFoodNeededForMating(stack: ItemStack): Int {
        return if (stack.item === Item.getItemFromBlock(Blocks.HAY_BLOCK))
            1
        else
            9 // assume wheat
    }

    companion object {
        fun populateFoodItems(food: MutableList<Item>) {
            food.add(Item.getItemFromBlock(Blocks.HAY_BLOCK))
            food.add(Items.WHEAT)
        }
    }
}