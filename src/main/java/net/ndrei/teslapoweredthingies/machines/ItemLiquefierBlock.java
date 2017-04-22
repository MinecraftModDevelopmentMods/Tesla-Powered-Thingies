package net.ndrei.teslapoweredthingies.machines;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

/**
 * Created by CF on 2017-04-09.
 */
public class ItemLiquefierBlock extends BaseThingyBlock<FluidSolidifierEntity> {
    public ItemLiquefierBlock() {
        super("item_liquefier", ItemLiquefierEntity.class);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this, 1),
                "bob", "oco", "ogo",
                'b', Items.LAVA_BUCKET,
                'c', TeslaCoreLib.machineCase,
                'o', "obsidian", // Blocks.OBSIDIAN,
                'g', "gearIron" // TeslaCoreLib.gearIron
        );
    }
}