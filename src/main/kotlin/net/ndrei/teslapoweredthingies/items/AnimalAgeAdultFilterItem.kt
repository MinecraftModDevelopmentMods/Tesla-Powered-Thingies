package net.ndrei.teslapoweredthingies.items

import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterItem
object AnimalAgeAdultFilterItem
    : BaseAnimalFilterItem("animal_age_filter_adult") {

    override fun canProcess(machine: ElectricFarmMachine, entityIndex: Int, entity: EntityAnimal)
        = !entity.isChild

    override val recipe: IRecipe
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "xxx",
                " y ",
                "   ",
                'x', "dustRedstone",
                'y', AnimalFilterItem
        )
}