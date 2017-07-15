package net.ndrei.teslapoweredthingies.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.MaterialLiquid
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.BlockRenderLayer
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fluids.BlockFluidFinite
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.blocks.ISelfRegisteringBlock
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-07.
 */
abstract class FiniteFluidThingyBlock(fluid: Fluid, color: MapColor)
    :  BlockFluidFinite(fluid, MaterialLiquid(color)), ISelfRegisteringBlock {
    init {
        this.setRegistryName(TeslaThingiesMod.MODID, "${this.fluid.name}_block")
        this.unlocalizedName = "${TeslaThingiesMod.MODID}.${this.fluid.name}.block"

        this.setCreativeTab(TeslaThingiesMod.creativeTab)
        this.setRenderLayer(BlockRenderLayer.SOLID)
    }

    override fun register(blockRegistry: IForgeRegistry<Block>, itemRegistry: IForgeRegistry<Item>) {
        blockRegistry.register(this)
        val item = ItemBlock(this)
        item.registryName = this.registryName
        itemRegistry.register(item)
    }

    @Suppress("unused")
    @SideOnly(Side.CLIENT)
    fun registerRenderer() {
        val item = Item.getItemFromBlock(this)
        ModelBakery.registerItemVariants(item)

        val modelResourceLocation = ModelResourceLocation(TeslaThingiesMod.MODID + ":fluids", this.fluid.name)
        ModelLoader.setCustomMeshDefinition(item) { modelResourceLocation }
        ModelLoader.setCustomStateMapper(this, object : StateMapperBase() {
            override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
                return modelResourceLocation
            }
        })
    }
}