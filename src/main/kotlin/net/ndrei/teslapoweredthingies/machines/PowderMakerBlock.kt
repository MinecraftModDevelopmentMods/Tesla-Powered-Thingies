package net.ndrei.teslapoweredthingies.machines

import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.items.MachineCaseItem

/**
 * Created by CF on 2017-07-04.
 */
@AutoRegisterBlock
object PowderMakerBlock
    : BaseThingyBlock<PowderMakerEntity>("powder_maker", PowderMakerEntity::class.java) {

    override val recipe: IRecipe?
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "sps",
                "scs",
                "sps",
                'p', Blocks.PISTON,
                'c', MachineCaseItem,
                's', Blocks.STONE
        )
}
