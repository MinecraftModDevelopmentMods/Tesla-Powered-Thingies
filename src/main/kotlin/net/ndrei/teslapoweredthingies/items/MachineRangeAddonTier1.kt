package net.ndrei.teslapoweredthingies.items

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.GearRegistry
import net.ndrei.teslacorelib.MATERIAL_GOLD
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.items.BaseAddonItem
import net.ndrei.teslacorelib.items.BaseTieredAddon
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine

/**
 * Created by CF on 2017-07-06.
 */
@AutoRegisterItem
object MachineRangeAddonTier1
    : BaseTieredAddon(TeslaThingiesMod.MODID, TeslaThingiesMod.creativeTab, "addon_range_tier1") {

    override val tier: Int
        get() = 1

    override val addonFunction: String
        get() = "thingies.range"

    override fun canBeAddedTo(machine: SidedTileEntity): Boolean {
        if (machine !is ElectricFarmMachine || !machine.supportsRangeAddons()) {
            return false
        }

        return super.canBeAddedTo(machine)
    }

    override val recipe: IRecipe
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                " g ",
                "rcr",
                " r ",
                'c', BaseAddonItem,
                'r', "dustRedstone",
                'g', GearRegistry.getMaterial(MATERIAL_GOLD)?.oreDictName ?: "gearGold"
        )
}