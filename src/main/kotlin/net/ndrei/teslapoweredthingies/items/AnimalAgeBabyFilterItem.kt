package net.ndrei.teslapoweredthingies.items

import net.minecraft.entity.passive.EntityAnimal
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterItem
object AnimalAgeBabyFilterItem
    : BaseAnimalFilterItem("animal_age_filter_baby") {

    override fun canProcess(machine: ElectricFarmMachine, entityIndex: Int, entity: EntityAnimal)
            = entity.isChild

//    override val recipe: IRecipe
//        get() = ShapedOreRecipe(null, ItemStack(this, 1),
//                "   ",
//                " y ",
//                "xxx",
//                'x', "dustRedstone",
//                'y', AnimalFilterItem
//        )
}