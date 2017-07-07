package net.ndrei.teslapoweredthingies.machines.treefarm

import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
abstract class VanillaTreeBlock(protected val world: World, protected val pos: BlockPos)
    : ITreeBlockWrapper {

    override fun breakBlock(fortune: Int): List<ItemStack> {
        val state = this.world.getBlockState(this.pos)
        val stacks = state.block.getDrops(world, pos, state, fortune)

        this.world.destroyBlock(this.pos, false)

        return stacks
    }
}