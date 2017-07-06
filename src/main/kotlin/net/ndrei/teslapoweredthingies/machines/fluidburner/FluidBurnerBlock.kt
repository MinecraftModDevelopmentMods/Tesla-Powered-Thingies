package net.ndrei.teslapoweredthingies.machines.fluidburner

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.items.MachineCaseItem
import net.ndrei.teslacorelib.items.gears.GearIronItem
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock
import net.ndrei.teslapoweredthingies.render.DualTankEntityRenderer

/**
 * Created by CF on 2017-06-30.
 */
@AutoRegisterBlock
object FluidBurnerBlock
    : BaseThingyBlock<FluidBurnerEntity>("fluid_burner", FluidBurnerEntity::class.java) {

    override val specialRenderer: TileEntitySpecialRenderer<FluidBurnerEntity>?
        @SideOnly(Side.CLIENT)
        get() = DualTankEntityRenderer()

    override val recipe: IRecipe?
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "bsb", "scs", "sgs",
                'b', Items.BUCKET,
                'c', MachineCaseItem,
                's', "stone",
                'g', GearIronItem.oreDictName()
        )
}
