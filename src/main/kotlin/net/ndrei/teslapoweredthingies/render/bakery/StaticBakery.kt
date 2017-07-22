package net.ndrei.teslapoweredthingies.render.bakery

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

class StaticBakery(val bakery: IBakery) : IBakery {
    private var quads: MutableList<BakedQuad>? = null

    override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat): MutableList<BakedQuad> {
        if (this.quads == null) {
            this.quads = this.bakery.getQuads(state, stack, side, vertexFormat)
        }
        return this.quads!!
    }
}