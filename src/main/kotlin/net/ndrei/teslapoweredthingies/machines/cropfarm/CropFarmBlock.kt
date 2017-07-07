package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.items.MachineCaseItem
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterBlock
object CropFarmBlock
    : BaseThingyBlock<CropFarmEntity>("crop_farm", CropFarmEntity::class.java) {

    override val recipe: IRecipe
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "xyz",
                "acb",
                "wgw",
                'x', Items.WHEAT_SEEDS,
                'y', Items.DIAMOND_HOE,
                'z', Items.WHEAT_SEEDS,
                'a', "cropCarrot",
                'b', "cropPotato",
                'c', MachineCaseItem,
                'w', "plankWood",
                'g', "gearStone"
        )
}