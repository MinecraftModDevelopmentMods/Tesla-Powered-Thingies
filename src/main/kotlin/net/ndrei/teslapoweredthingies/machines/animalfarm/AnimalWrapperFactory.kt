package net.ndrei.teslapoweredthingies.machines.animalfarm

import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityCow
import net.minecraft.entity.passive.EntityHorse
import net.minecraft.entity.passive.EntityLlama
import net.minecraft.item.Item

/**
 * Created by CF on 2017-07-07.
 */
object AnimalWrapperFactory {
    /**
     * @param entity
     * * the entity to be wrapped
     * *
     * @return
     * * return a nicely wrapped entity ready to be exploited in many many ways
     */
    fun getAnimalWrapper(entity: EntityAnimal): IAnimalWrapper {
        return when (entity) {
            is EntityLlama -> VanillaLlama(entity)
            is EntityHorse -> VanillaHorse(entity)
            is EntityCow -> if (entity.javaClass.name == "com.robrit.moofluids.common.entity.EntityFluidCow")
                MooFluidCow(entity)
            else
                VanillaGenericAnimal(entity)
            else -> VanillaGenericAnimal(entity)
        }
    }

    fun populateFoodItems(food: MutableList<Item>) {
        VanillaGenericAnimal.populateFoodItems(food)
        VanillaHorse.populateFoodItems(food)
        VanillaLlama.populateFoodItems(food)
    }
}
