package net.ndrei.teslapoweredthingies.render.bakery

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

interface ISelfRenderingBlock {
    fun getTextures(): List<ResourceLocation>
    fun getRegistryName(): ResourceLocation? // to be compatible with the Block method

    fun getBakeries(layer: BlockRenderLayer?, state: IBlockState?, stack: ItemStack?, side: EnumFacing?, rand: Long): List<IBakery>

    fun getParticleTexture(): ResourceLocation? {
        return this.getTextures().firstOrNull()
    }
}
