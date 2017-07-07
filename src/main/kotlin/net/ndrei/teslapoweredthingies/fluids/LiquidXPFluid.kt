package net.ndrei.teslapoweredthingies.fluids

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import net.ndrei.teslacorelib.annotations.AutoRegisterFluid
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterFluid
object LiquidXPFluid
    : Fluid("tf-liquidxp"
        , ResourceLocation(TeslaThingiesMod.MODID, "blocks/liquidxp_still")
        , ResourceLocation(TeslaThingiesMod.MODID, "blocks/liquidxp_flow")) {

    init {
        super.setLuminosity(20)
        super.setDensity(800)
        super.setViscosity(1500)
    }

    override fun getUnlocalizedName(): String {
        return "fluid.${TeslaThingiesMod.MODID}.${this.unlocalizedName}"
    }

//    fun register() {
//        FluidRegistry.registerFluid(this)
//        FluidRegistry.addBucketForFluid(this)
//    }
//
//    @SideOnly(Side.CLIENT)
//    fun registerRenderer() {
//        val block = LiquidXpBlock
//        val item = Item.getItemFromBlock(block as Block)
//        assert(item === Items.AIR)
//
//        ModelBakery.registerItemVariants(item)
//
//        val modelResourceLocation = ModelResourceLocation(TeslaThingiesMod.MODID + ":fluids", this.name)
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