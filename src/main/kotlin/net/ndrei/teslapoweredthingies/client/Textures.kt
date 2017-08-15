package net.ndrei.teslapoweredthingies.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureAtlasSprite
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
    JEI_TEXTURES_2("textures/gui/jei-2.png"),
    JUST_NOISE("textures/blocks/just_noise.png"),
    MULTI_TANK_SIDE("blocks/multi_tank-side"),
    INSIDE_TANK("textures/blocks/inside_tank.png");

    private val _resource = ResourceLocation(TeslaThingiesMod.MODID, path)

    val resource
        get() = this._resource

    val sprite: TextureAtlasSprite
        get() = Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(this._resource.toString())
                ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite

    fun bind(container: BasicTeslaGuiContainer<*>?) {
        (container?.mc ?: Minecraft.getMinecraft()).textureManager.bindTexture(this.resource)
    }

}
