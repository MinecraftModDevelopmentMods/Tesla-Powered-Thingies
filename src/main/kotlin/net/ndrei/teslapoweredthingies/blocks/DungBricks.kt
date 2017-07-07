package net.ndrei.teslapoweredthingies.blocks

import net.minecraft.block.material.Material
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.blocks.RegisteredBlock
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterBlock
object DungBricks : RegisteredBlock(TeslaThingiesMod.MODID, TeslaThingiesMod.creativeTab, "dung_bricks", Material.CLAY) {
    init {
        this.setHarvestLevel("pickaxe", 0)
        this.setHardness(3.0f)
    }

//    override val recipe: IRecipe?
//        get() = ShapedOreRecipe(null, ItemStack(this, 4),
//                "bb",
//                "bb",
//                'b', DungBlock
//        )
}