package net.ndrei.teslapoweredthingies.machines;

import net.minecraft.init.Blocks;
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
 * Created by CF on 2017-01-09.
 */
public class FluidBurnerBlock extends BaseThingyBlock<FluidBurnerEntity> {
    public FluidBurnerBlock() {
        super("fluid_burner", FluidBurnerEntity.class);
    }

    @SideOnly(Side.CLIENT)
    public void registerRenderer() {
        super.registerRenderer();

        ClientRegistry.bindTileEntitySpecialRenderer(FluidBurnerEntity.class, new DualTankEntityRenderer());
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this, 1),
                "bsb", "scs", "sgs",
                'b', Items.BUCKET,
                'c', TeslaCoreLib.machineCase,
                's', Blocks.STONE,
                'g', TeslaCoreLib.gearIron);
    }
}
