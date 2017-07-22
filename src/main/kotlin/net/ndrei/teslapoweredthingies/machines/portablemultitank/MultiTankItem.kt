package net.ndrei.teslapoweredthingies.machines.portablemultitank

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object MultiTankItem : ItemBlock(MultiTankBlock) {
    init {
        this.registryName = MultiTankBlock.registryName
    }

    fun createItemStack(world: World, pos: BlockPos, state: IBlockState) {

    }
}