package net.ndrei.teslapoweredthingies.machines.animalfarm

import net.minecraft.entity.passive.AbstractHorse
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by CF on 2017-07-07.
 */
class VanillaHorse(horse: AbstractHorse)
    : VanillaGenericAnimal(horse) {

    override fun canMateWith(wrapper: IAnimalWrapper): Boolean {
        return this.breedable() && wrapper.breedable() && wrapper is VanillaHorse
    }

    override fun isFood(stack: ItemStack): Boolean {
        return stack.item === Items.GOLDEN_CARROT || stack.item === Items.APPLE
    }

    override fun getFoodNeededForMating(stack: ItemStack): Int {
        return if (stack.item === Items.GOLDEN_CARROT)
            1
        else
            6 // assume apple
    }

    companion object {
        fun populateFoodItems(food: MutableList<Item>) {
            food.add(Items.GOLDEN_CARROT)
            food.add(Items.APPLE)
        }
    }
}