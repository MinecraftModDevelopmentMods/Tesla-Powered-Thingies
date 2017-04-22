package net.ndrei.teslapoweredthingies.common;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslapoweredthingies.items.BaseThingyItem;

/**
 * Created by CF on 2017-01-06.
 */
public class ItemsRegistry {
    public static BaseThingyItem ASH;

    static void registerItems() {
        GameRegistry.register(ItemsRegistry.ASH = new BaseThingyItem("ash"));
        CraftingManager.getInstance().addRecipe(new ShapedOreRecipe(new ItemStack(Items.DYE, 1, 15),
                "xx", "xx",
                'x', ItemsRegistry.ASH
        ));
    }
}
