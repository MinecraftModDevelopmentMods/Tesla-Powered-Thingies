package net.ndrei.teslapoweredthingies.machines

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.GearRegistry
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.items.MachineCaseItem
import net.ndrei.teslapoweredthingies.render.DualTankEntityRenderer

/**
 * Created by CF on 2017-06-30.
 */
@AutoRegisterBlock
object FluidSolidifierBlock
    : BaseThingyBlock<FluidSolidifierEntity>("fluid_solidifier", FluidSolidifierEntity::class.java) {

    override val specialRenderer: TileEntitySpecialRenderer<FluidSolidifierEntity>?
        @SideOnly(Side.CLIENT)
        get() = DualTankEntityRenderer()

    override val recipe: IRecipe?
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "bob",
                "oco",
                "ogo",
                'b', Items.BUCKET,
                'c', MachineCaseItem,
                'o', "obsidian",
                'g', GearRegistry.getMaterial("iron")?.oreDictName ?: "gearIron"
        )
}