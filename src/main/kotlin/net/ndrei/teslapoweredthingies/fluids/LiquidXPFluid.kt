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
}