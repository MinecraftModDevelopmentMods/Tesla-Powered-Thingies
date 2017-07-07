package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
class VanillaCactusPlant(block: Block, state: IBlockState, world: World, pos: BlockPos)
    : VanillaTallPlant(block, state, world, pos) {

    override fun canBlockNeighbours()
            = true

    override fun blocksNeighbour(pos: BlockPos): Boolean {
        return pos == super.pos.offset(EnumFacing.EAST)
                || pos == super.pos.offset(EnumFacing.NORTH)
                || pos == super.pos.offset(EnumFacing.SOUTH)
                || pos == super.pos.offset(EnumFacing.WEST)
    }
}