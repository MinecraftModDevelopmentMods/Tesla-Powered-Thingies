package net.ndrei.teslapoweredthingies.items

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.init.Blocks
import net.minecraft.item.ItemSeeds
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.blocks.TeslaPlantBlock

/**
 * Created by CF on 2017-07-10.
 */
@AutoRegisterItem
object TeslaPlantSeeds: ItemSeeds(TeslaPlantBlock, Blocks.REDSTONE_BLOCK) {
    init {
        this.setRegistryName(TeslaThingiesMod.MODID, "tesla_plant_seeds")
        this.unlocalizedName = "${TeslaThingiesMod.MODID}.tesla_plant_seeds"
        this.creativeTab = TeslaThingiesMod.creativeTab
    }

    @Suppress("unused")
    @SideOnly(Side.CLIENT)
    fun registerRenderer() = ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(this.registryName!!, "inventory"))

    //    override fun onItemUse(player: EntityPlayer, worldIn: World?, pos: BlockPos?, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
//        val itemStack = player.getHeldItem(hand)
//        if ((worldIn != null) && (pos != null) && (facing == EnumFacing.UP) && worldIn.isAirBlock(pos.up()) && player.canPlayerEdit(pos.up(), facing, itemStack)) {
//            val plant = TeslaPlantBlock.defaultState
//
//            val targetPos = pos.up()
//
//            if (TeslaPlantBlock.canSustainPlant(worldIn.getBlockState(pos), worldIn, targetPos, EnumFacing.UP, TeslaPlantBlock)) {
//                worldIn.setBlockState(targetPos, plant)
//                itemStack.shrink(1)
//                return EnumActionResult.SUCCESS
//            }
//            else {
//                return EnumActionResult.FAIL
//            }
//        }
//        return EnumActionResult.PASS
//    }
}