package net.ndrei.teslapoweredthingies.render.bakery

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

class CombinedBakery(val bakeries: Iterable<IBakery>): IBakery {
    constructor(vararg bakeries: IBakery)
        : this(bakeries.asIterable())

    override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat)
        = this.bakeries.fold(mutableListOf<BakedQuad>()) { list, bakery -> list.also { it.addAll(bakery.getQuads(state, stack, side, vertexFormat)) } }
}
