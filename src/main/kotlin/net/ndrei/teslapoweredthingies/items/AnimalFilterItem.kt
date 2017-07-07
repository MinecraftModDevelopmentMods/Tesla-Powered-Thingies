package net.ndrei.teslapoweredthingies.items

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.items.BaseAddonItem

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterItem
object AnimalFilterItem : BaseThingyItem("animal_filter") {
    override val recipe: IRecipe
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "x x",
                " y ",
                "x x",
                'x', "cropWheat",
                'y', BaseAddonItem
        )
}