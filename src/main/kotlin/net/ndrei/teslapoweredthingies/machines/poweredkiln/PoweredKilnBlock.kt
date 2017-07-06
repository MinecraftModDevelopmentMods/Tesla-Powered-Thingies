package net.ndrei.teslapoweredthingies.machines.poweredkiln

import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.items.MachineCaseItem
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock

/**
 * Created by CF on 2017-07-06.
 */
@AutoRegisterBlock
object PoweredKilnBlock
    : BaseThingyBlock<PoweredKilnEntity>("powered_kiln", PoweredKilnEntity::class.java) {

    override val recipe: IRecipe?
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "fff",
                "scs",
                "sxs",
                'f', Blocks.FURNACE,
                'c', MachineCaseItem,
                's', Blocks.STONE,
                'x', Items.FLINT_AND_STEEL
        )
}
