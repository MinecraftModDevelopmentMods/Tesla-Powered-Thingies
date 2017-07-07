package net.ndrei.teslapoweredthingies.items

import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.NonNullList
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.items.BaseAddon
import net.ndrei.teslacorelib.items.BaseAddonItem
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.IAdditionalProcessingAddon
import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine
import net.ndrei.teslapoweredthingies.machines.cropfarm.CropFarmEntity
import java.lang.reflect.InvocationTargetException

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterItem
object FruitPickerAddon
    : BaseAddon(TeslaThingiesMod.MODID, TeslaThingiesMod.creativeTab, "fruit_picker_addon")
        , IAdditionalProcessingAddon {
    const val PICK_ENERGY = .05f

    override val recipe: IRecipe?
        get() = ShapedOreRecipe(null,this,
                " t ",
                "tat",
                "shs",
                't', "treeSapling",
                'a', BaseAddonItem,
                's', "stickWood",
                'h', Blocks.HOPPER
        )

    override fun canBeAddedTo(machine: SidedTileEntity)
        = machine is CropFarmEntity

    override fun processAddon(machine: ElectricFarmMachine, availableProcessing: Float): Float {
        val energyUsed = 0.0f
        if (availableProcessing >= PICK_ENERGY) {
            for (pos in machine.groundArea) {
                for (y in 5 downTo 1) {
                    val current = pos.up(y)
                    if (!machine.world.isAirBlock(current)) {
                        val state = machine.world.getBlockState(current)
                        val block = state.block
                        try {
                            val isMature = block.javaClass.getMethod("isMature", IBlockState::class.java)
                            val mature = isMature.invoke(block, state) as Boolean
                            if (mature) {
                                val loot = NonNullList.create <ItemStack>()
                                block.getDrops(loot, machine.world, current, state, 1)
                                if (machine.outputItems(loot)) {
                                    machine.world.setBlockState(current, block.defaultState)
                                }
                            }
                        } catch (e: NoSuchMethodException) {
                        } catch (e: InvocationTargetException) {
                        } catch (e: IllegalAccessException) {
                        }
                    }
                }
            }
        }
        return energyUsed
    }
}
