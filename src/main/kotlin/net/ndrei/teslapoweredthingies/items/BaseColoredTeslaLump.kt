package net.ndrei.teslapoweredthingies.items

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.ndrei.teslacorelib.compatibility.IItemColorDelegate
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-13.
 */
class BaseColoredTeslaLump(material: String, val color: Int)
    : BaseThingyItem("lump_$material"), IItemColorDelegate {

    override final fun registerRenderer() {
        ModelLoader.setCustomModelResourceLocation(
                this,
                0,
                ModelResourceLocation(ResourceLocation(TeslaThingiesMod.MODID, "base_colored_lump"), "inventory"))
    }

    override fun getColorFromItemStack(stack: ItemStack, tintIndex: Int)= this.color

    override fun hasEffect(stack: ItemStack?) = true
}