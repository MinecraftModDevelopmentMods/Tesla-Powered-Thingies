package net.ndrei.teslapoweredthingies.machines

import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.items.MachineCaseItem

/**
 * Created by CF on 2017-06-30.
 */
@AutoRegisterBlock
object IncineratorBlock
    : BaseThingyBlock<IncineratorEntity>("incinerator", IncineratorEntity::class.java) {

    override val recipe: IRecipe?
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "sfs", "scs", "sgs",
                'f', Blocks.FURNACE,
                'c', MachineCaseItem,
                's', Blocks.STONE,
                'g', Items.FLINT_AND_STEEL
        )
}
