package net.ndrei.teslapoweredthingies.machines.sewer

import net.minecraft.init.Blocks
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
object SewerBlock
    : BaseThingyBlock<SewerEntity>("sewer", SewerEntity::class.java) {

    override val recipe: IRecipe
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "xxx", "wcw", "wgw",
                'x', Blocks.IRON_BARS,
                'c', MachineCaseItem,
                'w', "plankWood",
                'g', GearRegistry.getMaterial(MATERIAL_STONE)?.oreDictName ?: "gearStone"
        )
}