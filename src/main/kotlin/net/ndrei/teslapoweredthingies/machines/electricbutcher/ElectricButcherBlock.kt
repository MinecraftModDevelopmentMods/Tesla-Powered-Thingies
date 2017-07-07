package net.ndrei.teslapoweredthingies.machines.electricbutcher

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
 * Created by CF on 2017-07-07.
 */
@AutoRegisterBlock
object ElectricButcherBlock
    : BaseThingyBlock<ElectricButcherEntity>("electric_butcher", ElectricButcherEntity::class.java) {

    override val recipe: IRecipe
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "wxw", "wcw", "wgw",
                'x', Items.DIAMOND_SWORD,
                'c', MachineCaseItem,
                'w', "plankWood",
                'g', GearRegistry.getMaterial(MATERIAL_IRON)?.oreDictName ?: "gearIron"
        )
}