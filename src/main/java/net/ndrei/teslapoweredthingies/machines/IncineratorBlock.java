package net.ndrei.teslapoweredthingies.machines;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

/**
 * Created by CF on 2017-01-06.
 */
public class IncineratorBlock extends BaseThingyBlock<IncineratorEntity> {
    public IncineratorBlock() {
        super("incinerator", IncineratorEntity.class);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this, 1),
                "sfs", "scs", "sgs",
                'f', Blocks.FURNACE,
                'c', TeslaCoreLib.machineCase,
                's', Blocks.STONE,
                'g', Items.FLINT_AND_STEEL
        );
    }
}
