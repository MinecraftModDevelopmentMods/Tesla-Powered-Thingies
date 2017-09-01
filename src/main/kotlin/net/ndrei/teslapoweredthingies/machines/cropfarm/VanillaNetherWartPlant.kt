package net.ndrei.teslapoweredthingies.machines.cropfarm

import com.google.common.collect.Lists
import net.minecraft.block.BlockNetherWart
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.ndrei.teslacorelib.utils.BlockPosUtils
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-07.
 */
class VanillaNetherWartPlant(private val state: IBlockState, private val world: World, private val pos: BlockPos)
    : IPlantWrapper {

    override fun canBeHarvested(): Boolean {
        return this.state.getValue(BlockNetherWart.AGE) == 3
    }

    override fun harvest(fortune: Int): List<ItemStack> {
        // return Blocks.NETHER_WART.getDrops(this.world, this.pos, this.state, fortune);
        val player = TeslaThingiesMod.getFakePlayer(this.world)
        this.state.block.harvestBlock(this.world, player, this.pos, this.state, null, ItemStack.EMPTY)
        this.world.setBlockState(this.pos, this.state.block.defaultState)
        this.world.destroyBlock(this.pos, false) // <-- to force replanting

        val items = Lists.newArrayList<ItemStack>()
        val aabb = BlockPosUtils
                .getCube(this.pos, null, 1, 1)
                .boundingBox
        val entities = world.getEntitiesWithinAABB(EntityItem::class.java, aabb)
        for (ei in entities) {
            items.add(ei.item)
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
        return false
    }

    override fun useFertilizer(fertilizer: ItemStack): Int {
        return 0
    }
}
