package net.ndrei.teslapoweredthingies.machines;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslapoweredthingies.gui.DualTankEntityRenderer;

/**
 * Created by CF on 2017-01-11.
 */
public class FluidSolidifierBlock extends BaseThingyBlock<FluidSolidifierEntity> {
    public FluidSolidifierBlock() {
        super("fluid_solidifier", FluidSolidifierEntity.class);
    }

    @SideOnly(Side.CLIENT)
    public void registerRenderer() {
        super.registerRenderer();

        ClientRegistry.bindTileEntitySpecialRenderer(FluidSolidifierEntity.class, new DualTankEntityRenderer());
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this, 1),
                "bob", "oco", "ogo",
                'b', Items.BUCKET,
                'c', TeslaCoreLib.machineCase,
                'o', "obsidian", // Blocks.OBSIDIAN,
                'g', "gearIron" // TeslaCoreLib.gearIron
        );
    }
}
