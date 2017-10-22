package net.ndrei.teslapoweredthingies.items

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.oredict.OreDictionary
import net.ndrei.teslacorelib.compatibility.IItemColorDelegate
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-13.
 */
class BaseAugmentedLump(val material: String, val color: Int)
    : BaseThingyItem("augmented_$material"), IItemColorDelegate {

    override final fun registerRenderer() {
        ModelLoader.setCustomModelResourceLocation(
            this,
            0,
            ModelResourceLocation(ResourceLocation(TeslaThingiesMod.MODID, "base_augmented_lump"), "inventory"))
    }

    override fun getColorFromItemStack(stack: ItemStack, tintIndex: Int)= this.color

    val lumpItem: Item? by lazy {
        val stack = this.lump
        if (stack.isEmpty) null else stack.item
    }

    val lump: ItemStack by lazy {
        OreDictionary.getOres("teslaLump${this.material.capitalize()}").firstOrNull()?.copy() ?: ItemStack.EMPTY
    }
}