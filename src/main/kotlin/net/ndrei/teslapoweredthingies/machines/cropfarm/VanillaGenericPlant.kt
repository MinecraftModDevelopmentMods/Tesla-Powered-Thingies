package net.ndrei.teslapoweredthingies.machines.cropfarm

import com.google.common.collect.Lists
import net.minecraft.block.Block
import net.minecraft.block.IGrowable
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.BlockPosUtils

/**
 * Created by CF on 2017-07-07.
 */
open class VanillaGenericPlant(protected val block: Block, protected val state: IBlockState, protected val world: World, protected val pos: BlockPos)
    : IPlantWrapper {

    private val growable: IGrowable = this.block as IGrowable

    override fun canBeHarvested(): Boolean {
        return !this.growable.canGrow(this.world, this.pos, this.state, false)
    }

    override fun harvest(fortune: Int): List<ItemStack> {
        val player = TeslaThingiesMod.getFakePlayer(this.world)
        this.state.block.harvestBlock(this.world, player, this.pos, this.state, null, ItemStackUtil.emptyStack)
        this.world.setBlockState(this.pos, this.state.block.defaultState)
        this.world.destroyBlock(this.pos, false) // <-- to force replanting

        return this.harvestDrops()
    }

    protected fun harvestDrops(): List<ItemStack> {
        val items = Lists.newArrayList<ItemStack>()
        val aabb = BlockPosUtils
                .getCube(this.pos, null, 1, 1)
                .boundingBox
        val entities = world.getEntitiesWithinAABB(EntityItem::class.java, aabb)
        for (ei in entities) {
            items.add(ei.item) // .getEntityItem())
            this.world.removeEntity(ei)
        }
        return items
    }

    override fun canBlockNeighbours(): Boolean {
        return false
    }

    override fun blocksNeighbour(pos: BlockPos): Boolean {
        return false
    }

    override fun canUseFertilizer(): Boolean {
        return !this.canBeHarvested()
    }

    override fun useFertilizer(fertilizer: ItemStack): Int {
        return VanillaGenericPlant.useFertilizer(this.world, this.pos, fertilizer)
    }

    companion object {
        fun useFertilizer(world: World, pos: BlockPos, fertilizer: ItemStack): Int {
            val player = TeslaThingiesMod.getFakePlayer(world)
            if (player != null) {
                player.setHeldItem(EnumHand.MAIN_HAND, fertilizer.copy())
                val result = player.getHeldItem(EnumHand.MAIN_HAND).onItemUse(player, world, pos, EnumHand.MAIN_HAND, EnumFacing.UP, .5f, .5f, .5f)
                player.setHeldItem(EnumHand.MAIN_HAND, ItemStackUtil.emptyStack)
                return 1
            }
            return 0
        }
    }
}
