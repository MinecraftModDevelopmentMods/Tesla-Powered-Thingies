package net.ndrei.teslapoweredthingies.machines.compoundmaker

import net.minecraft.util.BlockRenderLayer
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock

@AutoRegisterBlock
object CompoundMakerBlock
    : BaseThingyBlock<CompoundMakerEntity>("compound_maker", CompoundMakerEntity::class.java) {

    override fun getRenderLayer() = BlockRenderLayer.TRANSLUCENT
}
