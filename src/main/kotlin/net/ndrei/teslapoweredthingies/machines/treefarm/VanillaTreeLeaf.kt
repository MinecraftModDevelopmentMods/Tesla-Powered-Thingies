package net.ndrei.teslapoweredthingies.machines.treefarm

import com.google.common.collect.Lists
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
class VanillaTreeLeaf internal constructor(world: World, pos: BlockPos)
    : VanillaTreeBlock(world, pos), ITreeLeafWrapper {

    override fun shearBlock(): List<ItemStack> {
        val stacks = Lists.newArrayList<ItemStack>()

        val block = this.world.getBlockState(this.pos).block
        if (block === Blocks.LEAVES || block === Blocks.LEAVES2) {
            stacks.add(ItemStack(block))
        }
        this.world.destroyBlock(this.pos, false)

        return stacks
    }
}