package net.ndrei.teslapoweredthingies.fluids

import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.ndrei.teslacorelib.annotations.AutoRegisterFluid
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterFluid
object RedstoneFluid
    : Fluid("redstone"
        , ResourceLocation(TeslaThingiesMod.MODID, "blocks/redstone_still")
        , ResourceLocation(TeslaThingiesMod.MODID, "blocks/redstone_flow")) {

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    override fun getUnlocalizedName(): String {
        return "fluid.${TeslaThingiesMod.MODID}.${this.unlocalizedName}"
    }

    @SubscribeEvent
    fun onTextureStitch(ev : TextureStitchEvent) {
        ev.map.registerSprite(this.flowing)
        ev.map.registerSprite(this.still)
    }
}
