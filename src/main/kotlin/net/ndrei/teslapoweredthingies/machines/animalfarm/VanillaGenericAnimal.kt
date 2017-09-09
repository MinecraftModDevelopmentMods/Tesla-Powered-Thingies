package net.ndrei.teslapoweredthingies.machines.animalfarm

import com.google.common.collect.Lists
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityCow
import net.minecraft.entity.passive.EntityMooshroom
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.common.IShearable

/**
 * Created by CF on 2017-07-07.
 */
open class VanillaGenericAnimal(override val animal: EntityAnimal)
    : IAnimalWrapper {

    override fun breedable(): Boolean {
        val animal = this.animal
        return !animal.isInLove && !animal.isChild && animal.growingAge == 0
    }

    override fun isFood(stack: ItemStack): Boolean {
        return this.animal.isBreedingItem(stack)
    }

    override fun canMateWith(wrapper: IAnimalWrapper): Boolean {
        return this.breedable() && wrapper.breedable() && this.animal.javaClass == wrapper.animal.javaClass
    }

    override fun mate(player: EntityPlayer, stack: ItemStack, wrapper: IAnimalWrapper): Int {
        val consumedFood: Int
        val neededFood = 2 * this.getFoodNeededForMating(stack)
        consumedFood = if (stack.count < neededFood) {
            0
        } else if (!this.canMateWith(wrapper) || !this.isFood(stack)) {
            0
        } else {
            this.animal.setInLove(player)
            wrapper.animal.setInLove(player)
            neededFood
        }

        return consumedFood
    }

    protected open fun getFoodNeededForMating(stack: ItemStack): Int {
        return 1
    }

    override fun shearable(): Boolean {
        return this.animal !is EntityMooshroom && this.animal is IShearable
    }

    override fun canBeShearedWith(stack: ItemStack): Boolean {
        if (stack.isEmpty || stack.item !== Items.SHEARS) {
            return false
        }
        var isShearable = false
        val animal = this.animal
        if (this.shearable() && animal is IShearable) {
            val shearable = animal as IShearable
            isShearable = shearable.isShearable(stack, animal.entityWorld, animal.position)
        }
        return isShearable
    }

    override fun shear(stack: ItemStack, fortune: Int): List<ItemStack> {
        var result: List<ItemStack> = Lists.newArrayList<ItemStack>()
        val animal = this.animal
        if (animal is IShearable) {
            val shearable = animal as IShearable
            if (shearable.isShearable(stack, animal.entityWorld, animal.position)) {
                result = shearable.onSheared(stack, animal.entityWorld, animal.position, fortune)
            }
        }
        return result
    }

    override fun canBeMilked(): Boolean {
        val animal = this.animal
        return animal is EntityCow && !animal.isChild
    }

    override fun milk(): ItemStack {
        return if (this.canBeMilked())
            ItemStack(Items.MILK_BUCKET, 1)
        else
            ItemStack.EMPTY
    }

    override fun canBeBowled(): Boolean {
        val animal = this.animal
        return animal is EntityMooshroom && !animal.isChild
    }

    override fun bowl(): ItemStack {
        return if (this.canBeBowled())
            ItemStack(Items.MUSHROOM_STEW, 1)
        else
            ItemStack.EMPTY
    }

    companion object {
        fun populateFoodItems(food: MutableList<Item>) {
            // cows / mooshrooms
            food.add(Items.WHEAT)

            // chicken
            food.add(Items.WHEAT_SEEDS)
            food.add(Items.BEETROOT_SEEDS)
            food.add(Items.PUMPKIN_SEEDS)
            food.add(Items.MELON_SEEDS)

            // pigs
            food.add(Items.CARROT)
            food.add(Items.POTATO)
            food.add(Items.BEETROOT)

            food.add(Items.GOLDEN_CARROT)
            food.add(Item.getItemFromBlock(Blocks.HAY_BLOCK))
            food.add(Items.APPLE)
        }
    }
}
