package net.ndrei.teslapoweredthingies.machines.animalgym

import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.items.MachineCaseItem
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock

/**
 * Created by CF on 2017-07-08.
 */
@AutoRegisterBlock
object AnimalGymBlock
    : BaseThingyBlock<AnimalGymEntity>("animal_gym", AnimalGymEntity::class.java) {

    override val recipe: IRecipe
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "xgx", "wcw", "wgw",
                'x', Blocks.IRON_BARS,
                'c', MachineCaseItem,
                'w', "plankWood",
                'g', "gearIron"
        )
}
