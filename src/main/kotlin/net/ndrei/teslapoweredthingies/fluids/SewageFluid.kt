package net.ndrei.teslapoweredthingies.fluids

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import net.ndrei.teslacorelib.annotations.AutoRegisterFluid
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterFluid
object SewageFluid
    : Fluid("tf-sewage"
        , ResourceLocation(TeslaThingiesMod.MODID, "blocks/sewage_still")
        , ResourceLocation(TeslaThingiesMod.MODID, "blocks/sewage_flow")) {

    init {
        super.setViscosity(10000)
        super.setDensity(50000)
    }

    override fun getUnlocalizedName(): String {
        return "fluid.${TeslaThingiesMod.MODID}.${this.unlocalizedName}"
    }

//    fun register() {
//        FluidRegistry.registerFluid(this)
//        FluidRegistry.addBucketForFluid(this)
//    }

//    @SideOnly(Side.CLIENT)
//    fun registerRenderer() {
//        val block = BlocksRegistry.sewageBlock
//        val item = Item.getItemFromBlock(block as Block)
//        assert(item === Items.AIR)
//
//        ModelBakery.registerItemVariants(item)
//
//        val modelResourceLocation = ModelResourceLocation(MekfarmMod.MODID + ":fluids", this.name)
//
//        ModelLoader.setCustomMeshDefinition(item) { modelResourceLocation }
//
//        ModelLoader.setCustomStateMapper(block as Block, object : StateMapperBase() {
//            override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
//                return modelResourceLocation
//            }
//        })
//    }
}
