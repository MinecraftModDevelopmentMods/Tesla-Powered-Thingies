package net.ndrei.teslapoweredthingies.fluids

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import net.ndrei.teslacorelib.annotations.AutoRegisterFluid
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterFluid
object MoltenTeslaFluid
    : Fluid("tf-molten_tesla"
        , ResourceLocation(TeslaThingiesMod.MODID, "blocks/molten_tesla_still")
        , ResourceLocation(TeslaThingiesMod.MODID, "blocks/molten_tesla_flow")) {

    init {
        super.setLuminosity(15)
        super.setViscosity(6000)
        super.setDensity(3000)
        super.setTemperature(1500)
    }

    override fun getUnlocalizedName(): String {
        return "fluid.${TeslaThingiesMod.MODID}.${this.unlocalizedName}"
    }
}
