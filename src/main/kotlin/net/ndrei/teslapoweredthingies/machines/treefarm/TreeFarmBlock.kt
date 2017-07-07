package net.ndrei.teslapoweredthingies.machines.treefarm

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.GearRegistry
import net.ndrei.teslacorelib.MATERIAL_STONE
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.items.MachineCaseItem
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterBlock
object TreeFarmBlock
    : BaseThingyBlock<TreeFarmEntity>("tree_farm", TreeFarmEntity::class.java) {

    override val recipe: IRecipe
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "sss", "wcw", "wgw",
                's', "treeSapling",
                'c', MachineCaseItem,
                'w', "plankWood",
                'g', GearRegistry.getMaterial(MATERIAL_STONE)?.oreDictName ?: "gearStone"
        )
}