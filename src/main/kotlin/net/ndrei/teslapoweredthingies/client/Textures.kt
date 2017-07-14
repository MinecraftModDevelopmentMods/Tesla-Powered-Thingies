package net.ndrei.teslapoweredthingies.client

import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-07.
 */
@SideOnly(Side.CLIENT)
enum class Textures(path: String) {
    MACHINES_TEXTURES("textures/gui/machines.png"),
    FARM_TEXTURES("textures/gui/farm_machines.png"),
    MOST_TEXTURES("textures/gui/most_machines.png"),
    JEI_TEXTURES("textures/gui/jei.png"),
    JUST_NOISE("textures/blocks/just_noise.png"),
    INSIDE_TANK("textures/blocks/inside_tank.png");

    private val _resource = ResourceLocation(TeslaThingiesMod.MODID, path)

    val resource
        get() = this._resource

    fun bind(container: BasicTeslaGuiContainer<*>?) {
        (container?.mc ?: Minecraft.getMinecraft()).textureManager.bindTexture(this.resource)
    }
}
