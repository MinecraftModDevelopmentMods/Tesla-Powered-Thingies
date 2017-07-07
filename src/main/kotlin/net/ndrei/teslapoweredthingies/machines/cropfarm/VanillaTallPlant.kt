package net.ndrei.teslapoweredthingies.machines.cropfarm

import com.google.common.collect.Lists
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by CF on 2017-07-07.
 */
open class VanillaTallPlant(protected val block: Block, protected val state: IBlockState, protected val world: World, protected val pos: BlockPos)
    : IPlantWrapper {

    override fun canBeHarvested(): Boolean {
        return PLANT_BLOCKS.contains(this.world.getBlockState(this.pos.up()).block)
    }

    override fun harvest(fortune: Int): List<ItemStack> {
        val loot = Lists.newArrayList<ItemStack>()
        this.harvestUp(this.pos.up(), loot, fortune)
        return loot
    }

    protected fun harvestUp(pos: BlockPos, loot: MutableList<ItemStack>, fortune: Int) {
        val state = this.world.getBlockState(pos)
        if (!PLANT_BLOCKS.contains(state.block)) {
            return
        }

        this.harvestUp(pos.up(), loot, fortune)
        loot.addAll(state.block.getDrops(this.world, this.pos, state, fortune))
        this.world.setBlockToAir(pos)
    }

    override fun canBlockNeighbours() = false

    override fun blocksNeighbour(pos: BlockPos) = false

    override fun canUseFertilizer() = false

    override fun useFertilizer(fertilizer: ItemStack) = 0

    companion object {
        private val PLANT_BLOCKS = listOf(Blocks.CACTUS, Blocks.REEDS)
    }
}
