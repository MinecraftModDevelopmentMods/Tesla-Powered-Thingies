package net.ndrei.teslapoweredthingies.machines.itemcompoundproducer

import net.minecraft.util.BlockRenderLayer
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock

/**
 * Created by CF on 2017-07-13.
 */
@AutoRegisterBlock
object ItemCompoundProducerBlock
    : BaseThingyBlock<ItemCompoundProducerEntity>("item_compound_producer", ItemCompoundProducerEntity::class.java) {

    override fun getRenderLayer() = BlockRenderLayer.TRANSLUCENT
}