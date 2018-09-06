package net.ndrei.teslapoweredthingies.machines.liquidxpstorage

import net.minecraft.block.state.IBlockState
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.GearRegistry
import net.ndrei.teslacorelib.MATERIAL_IRON
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.items.MachineCaseItem
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterBlock
object LiquidXPStorageBlock
    : BaseThingyBlock<LiquidXPStorageEntity>("liquidxp_storage", LiquidXPStorageEntity::class.java) {

    override val recipe: IRecipe
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                " s ",
                "wcw",
                "wgw",
                's', Items.EXPERIENCE_BOTTLE,
                'c', MachineCaseItem,
                'w', "plankWood",
                'g', GearRegistry.getMaterial(MATERIAL_IRON)?.oreDictName ?: "gearIron"
        )

//    override val specialRenderer: TileEntitySpecialRenderer<LiquidXPStorageEntity>
//        @SideOnly(Side.CLIENT)
//        get() = LiquidXPStorageSpecialRenderer()
//
    override fun getRenderLayer() = BlockRenderLayer.TRANSLUCENT

    override fun doesSideBlockRendering(state: IBlockState, world: IBlockAccess, pos: BlockPos, face: EnumFacing)
            = (face == EnumFacing.DOWN)
}