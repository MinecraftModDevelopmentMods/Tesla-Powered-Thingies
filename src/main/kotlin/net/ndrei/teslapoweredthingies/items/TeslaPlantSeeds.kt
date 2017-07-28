package net.ndrei.teslapoweredthingies.items

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.init.Blocks
import net.minecraft.item.ItemSeeds
import net.minecraft.item.ItemStack
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.render.ISelfRegisteringRenderer
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.blocks.TeslaPlantBlock

/**
 * Created by CF on 2017-07-10.
 */
@AutoRegisterItem
object TeslaPlantSeeds: ItemSeeds(TeslaPlantBlock, Blocks.REDSTONE_BLOCK), ISelfRegisteringRenderer {
    init {
        this.setRegistryName(TeslaThingiesMod.MODID, "tesla_plant_seeds")
        this.unlocalizedName = "${TeslaThingiesMod.MODID}.tesla_plant_seeds"
        this.creativeTab = TeslaThingiesMod.creativeTab
    }

    @Suppress("unused")
    @SideOnly(Side.CLIENT)
    override fun registerRenderer()
            = ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(this.registryName!!, "inventory"))

    fun registerSeeds() {
        MinecraftForge.addGrassSeed(ItemStack(this), 2)
    }
}
