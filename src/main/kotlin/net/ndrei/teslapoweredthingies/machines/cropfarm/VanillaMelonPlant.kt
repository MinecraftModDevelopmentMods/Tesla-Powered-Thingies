package net.ndrei.teslapoweredthingies.machines.cropfarm

import com.google.common.collect.Lists
import net.minecraft.block.Block
import net.minecraft.block.IGrowable
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
class VanillaMelonPlant(private val block: Block, private val state: IBlockState, private val world: World, private val pos: BlockPos)
    : IPlantWrapper {

    override fun canBeHarvested(): Boolean {
        val north = this.world.getBlockState(this.pos.north())
        if (MELON_BLOCKS.contains(north.block)) {
            return true
        }
        val east = this.world.getBlockState(this.pos.east())
        if (MELON_BLOCKS.contains(east.block)) {
            return true
        }
        val south = this.world.getBlockState(this.pos.south())
        if (MELON_BLOCKS.contains(south.block)) {
            return true
        }
        val west = this.world.getBlockState(this.pos.west())
        if (MELON_BLOCKS.contains(west.block)) {
            return true
        }
        return false
    }

    override fun harvest(fortune: Int): List<ItemStack> {
        val loot = Lists.newArrayList<ItemStack>()
        val north = this.world.getBlockState(this.pos.north())
        if (MELON_BLOCKS.contains(north.block)) {
            loot.add(ItemStack(Item.getItemFromBlock(north.block)))
            this.world.setBlockState(this.pos.north(), Blocks.AIR.defaultState)
        }
        val east = this.world.getBlockState(this.pos.east())
        if (MELON_BLOCKS.contains(east.block)) {
            loot.add(ItemStack(Item.getItemFromBlock(east.block)))
            this.world.setBlockState(this.pos.east(), Blocks.AIR.defaultState)
        }
        val south = this.world.getBlockState(this.pos.south())
        if (MELON_BLOCKS.contains(south.block)) {
            loot.add(ItemStack(Item.getItemFromBlock(south.block)))
            this.world.setBlockState(this.pos.south(), Blocks.AIR.defaultState)
        }
        val west = this.world.getBlockState(this.pos.west())
        if (MELON_BLOCKS.contains(west.block)) {
            loot.add(ItemStack(Item.getItemFromBlock(west.block)))
            this.world.setBlockState(this.pos.west(), Blocks.AIR.defaultState)
        }
        return loot
    }

    override fun canBlockNeighbours(): Boolean {
        return true
    }

    override fun blocksNeighbour(pos: BlockPos): Boolean {
        return pos == this.pos.offset(EnumFacing.EAST)
                || pos == this.pos.offset(EnumFacing.NORTH)
                || pos == this.pos.offset(EnumFacing.SOUTH)
                || pos == this.pos.offset(EnumFacing.WEST)
    }

    override fun canUseFertilizer(): Boolean {
        if (this.block is IGrowable) {
            return (this.block as IGrowable).canGrow(this.world, this.pos, this.state, false)
        }
        return false
    }

    override fun useFertilizer(fertilizer: ItemStack): Int {
        return VanillaGenericPlant.useFertilizer(this.world, this.pos, fertilizer)
    }

    companion object {
        private val MELON_BLOCKS = Lists.newArrayList(Blocks.MELON_BLOCK, Blocks.PUMPKIN)
    }
}
