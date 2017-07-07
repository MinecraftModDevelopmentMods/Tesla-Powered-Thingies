package net.ndrei.teslapoweredthingies.machines.animalfarm

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.GearRegistry
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.items.MachineCaseItem
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock

/**
 * Created by CF on 2017-07-06.
 */
@AutoRegisterBlock
object AnimalFarmBlock
    : BaseThingyBlock<AnimalFarmEntity>("animal_farm", AnimalFarmEntity::class.java) {

    override val recipe: IRecipe
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "xyz",
                "wcw",
                "wgw",
                'x', "cropWheat",
                'y', "cropCarrot",
                'z', "cropWheat",
                'c', MachineCaseItem,
                'w', "plankWood",
                'g', GearRegistry.getMaterial("stone")?.oreDictName ?: "gearStone"
        )
}