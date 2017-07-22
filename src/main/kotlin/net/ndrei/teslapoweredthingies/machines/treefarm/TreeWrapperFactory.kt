package net.ndrei.teslapoweredthingies.machines.treefarm

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.ndrei.teslacorelib.compatibility.ItemStackUtil

/**
 * Created by CF on 2017-07-07.
 */
object TreeWrapperFactory {
    private var treeWrappers = mutableListOf<ITreeFactory>()

    init {
        TreeWrapperFactory.treeWrappers.add(VanillaTree())

        // RUSTIC
        TreeWrapperFactory.treeWrappers.add(object: BaseModTreeFactory("rustic") {
            override fun getHarvestableLeaf(world: World, pos: BlockPos, block: IBlockState): ITreeLeafWrapper? {
                if (block.block.registryName?.toString() == "rustic:leaves") {
                    return VanillaTreeLeaf(world, pos)
                }
                return null
            }
        })

        // BIOMES O' PLENTY
        TreeWrapperFactory.treeWrappers.add(object: BaseModTreeFactory("biomesoplenty") {
            override fun getHarvestableLog(world: World, pos: BlockPos, block: IBlockState): ITreeLogWrapper? {
                if (block.block.registryName?.toString()?.startsWith("biomesoplenty:log_") ?: false) {
                    return VanillaTreeLog(world, pos)
                }
                return null
            }

            override fun getHarvestableLeaf(world: World, pos: BlockPos, block: IBlockState): ITreeLeafWrapper? {
                if (block.block.registryName?.toString()?.startsWith("biomesoplenty:leaves_") ?: false) {
                    return VanillaTreeLeaf(world, pos)
                }
                return null
            }
        })

        // add generic modded tree factory
        TreeWrapperFactory.treeWrappers.add(BaseModTreeFactory(""))
    }

    fun isHarvestable(world: World, pos: BlockPos, block: IBlockState?): Boolean {
        val state = block ?: world.getBlockState(pos)
        return TreeWrapperFactory.isHarvestableLog(world, pos, state)
                || TreeWrapperFactory.isHarvestableLeaf(world, pos, state)
    }

    fun isHarvestableLog(world: World, pos: BlockPos, block: IBlockState?): Boolean {
        val state = block ?: world.getBlockState(pos)

        return TreeWrapperFactory.treeWrappers.any { it.getHarvestableLog(world, pos, state) != null }
    }

    fun isHarvestableLeaf(world: World, pos: BlockPos, block: IBlockState?): Boolean {
        val state = block ?: world.getBlockState(pos)

        return TreeWrapperFactory.treeWrappers.any { it.getHarvestableLeaf(world, pos, state) != null }
    }

    fun getBlockWrapper(world: World, pos: BlockPos, block: IBlockState?): ITreeBlockWrapper? {
        val state = block ?: world.getBlockState(pos)

        for (tree in TreeWrapperFactory.treeWrappers) {
            var wrapper: ITreeBlockWrapper? = tree.getHarvestableLog(world, pos, state)
            if (wrapper == null) {
                wrapper = tree.getHarvestableLeaf(world, pos, state)
            }

            if (wrapper != null) {
                return wrapper
            }
        }
        return null
    }

    fun getSaplingWrapper(stack: ItemStack): ITreeSaplingWrapper? {
        if (!ItemStackUtil.isEmpty(stack)) {
            for (tree in TreeWrapperFactory.treeWrappers) {
                val wrapper = tree.getPlantableSapling(stack)
                if (wrapper != null) {
                    return wrapper
                }
            }
        }
        return null
    }

    fun getSaplingWrappers(stacks: List<ItemStack>?): List<ITreeSaplingWrapper> {
        val wrappers = mutableListOf<ITreeSaplingWrapper>()

        stacks?.mapNotNullTo(wrappers) { TreeWrapperFactory.getSaplingWrapper(it) }

        return wrappers.toList()
    }
}
