package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

class RusticBlockStakeSeed(override val seeds: ItemStack) : ISeedWrapper {
    override fun canPlantHere(world: World, pos: BlockPos): Boolean {
        val block = world.getBlockState(pos)?.block ?: return false
        if ((this.seeds.item.javaClass.name == "rustic.common.items.ItemStakeCropSeed")
            && (block.javaClass.name == "rustic.common.blocks.crops.BlockCropStake")) {
            return true
        }
        return false
    }

    override fun plant(world: World, pos: BlockPos): IBlockState {
        if (this.canPlantHere(world, pos)) {
            val player = TeslaThingiesMod.getFakePlayer(world)
            if (player != null) {
                player.setItemInUse(this.seeds.copy())
                val state = world.getBlockState(pos)
                state?.block
                    ?.onBlockActivated(world, pos, state, player, EnumHand.MAIN_HAND, EnumFacing.UP, .5f, .5f, .5f)
            }
        }
        return world.getBlockState(pos)
    }


    companion object {
        fun isSeed(stack: ItemStack)
            = !stack.isEmpty && (stack.item.javaClass.name == "rustic.common.items.ItemStakeCropSeed")
    }
}
