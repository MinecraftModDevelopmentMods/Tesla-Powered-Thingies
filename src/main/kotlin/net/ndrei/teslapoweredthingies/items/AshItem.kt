package net.ndrei.teslapoweredthingies.items

import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterItem

/**
 * Created by CF on 2017-06-30.
 */
@AutoRegisterItem
object AshItem : BaseThingyItem("ash") {
    override val recipe: IRecipe?
        get() = ShapedOreRecipe(null, ItemStack(Items.DYE, 1, 15),
                "xx",
                "xx",
                'x', this
        )
}
