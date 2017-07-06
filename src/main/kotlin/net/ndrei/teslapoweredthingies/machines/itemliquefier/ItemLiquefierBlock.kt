package net.ndrei.teslapoweredthingies.machines.itemliquefier

import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.GearRegistry
import net.ndrei.teslacorelib.MATERIAL_IRON
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.items.MachineCaseItem
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock

/**
 * Created by CF on 2017-06-30.
 */
@AutoRegisterBlock
object ItemLiquefierBlock
    : BaseThingyBlock<ItemLiquefierEntity>("item_liquefier", ItemLiquefierEntity::class.java) {

    override val recipe: IRecipe?
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "bob", "oco", "ogo",
                'b', Items.LAVA_BUCKET,
                'c', MachineCaseItem,
                'o', "obsidian",
                'g', GearRegistry.getMaterial(MATERIAL_IRON)?.oreDictName ?: "gearIron"
        )
}