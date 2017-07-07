package net.ndrei.teslapoweredthingies.blocks

import net.minecraft.block.material.MapColor
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.init.MobEffects
import net.minecraft.potion.PotionEffect
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslapoweredthingies.fluids.SewageFluid

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterBlock
object SewageBlock : FiniteFluidThingyBlock(SewageFluid, MapColor.BROWN) { // BlockFluidFinite(SewageFluid, MaterialLiquid(MapColor.BROWN)) {
//    init {
//        this.setRegistryName(TeslaThingiesMod.MODID, "${SewageFluid.name}_block")
//        this.unlocalizedName = "${TeslaThingiesMod.MODID}.${SewageFluid.name}.block"
//
//        this.setCreativeTab(TeslaThingiesMod.creativeTab)
//        this.setRenderLayer(BlockRenderLayer.SOLID)
//    }

    override fun onEntityCollidedWithBlock(world: World?, pos: BlockPos?, state: IBlockState?, entity: Entity?) {
        if ((world != null) && (pos != null) && (entity is EntityLivingBase)) {
            val quanta = this.getQuantaValue(world, pos)
            if (quanta > 0) {
                entity.addPotionEffect(PotionEffect(MobEffects.REGENERATION, quanta * 100 / 15))
            }
        }
    }

//    @SideOnly(Side.CLIENT)
//    fun registerRenderer() {
//        val item = Item.getItemFromBlock(this)
//        ModelBakery.registerItemVariants(item)
//
//        val modelResourceLocation = ModelResourceLocation(TeslaThingiesMod.MODID + ":fluids", SewageFluid.name)
//        ModelLoader.setCustomMeshDefinition(item) { modelResourceLocation }
//        ModelLoader.setCustomStateMapper(this, object : StateMapperBase() {
//            override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
//                return modelResourceLocation
//            }
//        })
//    }
}