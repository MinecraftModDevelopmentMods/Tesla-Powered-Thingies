package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-07.
 */
class AgricraftPlantWrapper(block: Block, state: IBlockState, world: World, pos: BlockPos)
    : VanillaGenericPlant(block, state, world, pos) {

    override fun harvest(fortune: Int): List<ItemStack> {
        val player = TeslaThingiesMod.getFakePlayer(super.world)
        if (player != null) {
            player.setHeldItem(EnumHand.MAIN_HAND, ItemStackUtil.emptyStack)
            player.setHeldItem(EnumHand.OFF_HAND, ItemStackUtil.emptyStack)
            super.block.onBlockActivated(super.world, super.pos, super.state, player, EnumHand.OFF_HAND,
                    EnumFacing.UP, .5f, .5f, .5f)
        }
        return this.harvestDrops()
    }

    override fun useFertilizer(fertilizer: ItemStack): Int {
        val player = TeslaThingiesMod.getFakePlayer(super.world)
        val stack = fertilizer.copy()
        if (player != null) {
            player.setHeldItem(EnumHand.MAIN_HAND, stack)
            player.setHeldItem(EnumHand.OFF_HAND, ItemStackUtil.emptyStack)
        }
        return if (super.block.onBlockActivated(super.world, super.pos, super.state, player, EnumHand.OFF_HAND,
                EnumFacing.UP, .5f, .5f, .5f
        ))
            1
        else
            0
    }
}
