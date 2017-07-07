package net.ndrei.teslapoweredthingies.blocks

import net.minecraft.block.material.Material
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.blocks.RegisteredBlock
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterBlock
object DungBlock : RegisteredBlock(TeslaThingiesMod.MODID, TeslaThingiesMod.creativeTab, "dung_block", Material.CLAY) {
    init {
        this.setHarvestLevel("pickaxe", 0)
        this.setHardness(1.0f)
    }

//    override val recipe: IRecipe?
//        get() = ShapedOreRecipe(null, ItemStack(this, 9),
//                "hs",
//                "wc",
//                'h', Blocks.HAY_BLOCK,
//                's', FluidUtil.getFilledBucket(FluidStack(SewageFluid, Fluid.BUCKET_VOLUME)),
//                'c', Blocks.CLAY,
//                'w', Items.WATER_BUCKET
//        )
}
