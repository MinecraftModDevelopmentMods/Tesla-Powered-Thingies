package net.ndrei.teslapoweredthingies.machines.treefarm

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-07.
 */
object TreeWrapperFactory {
    private var treeWrappers = mutableListOf<ITreeFactory>()

    init {
        TreeWrapperFactory.treeWrappers.add(VanillaTree())

        // FORESTRY
        TreeWrapperFactory.treeWrappers.add(object: BaseModTreeFactory("forestry") {
            override fun getPlantableSapling(stack: ItemStack): ITreeSaplingWrapper? {
                if (stack.item.javaClass.name.endsWith("ItemGermlingGE")) {
                    return object: VanillaSapling(stack) {
                        override fun canPlant(world: World, pos: BlockPos): Boolean {
                            return true // TODO: find a way to test this
                        }

                        override fun plant(world: World, pos: BlockPos): Int {
                            val player = TeslaThingiesMod.getFakePlayer(world)
                            if (player != null) {
                                player.setPosition(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                                player.rotationYaw = 90.0f
                                player.rotationPitch = 90.0f
                                player.setItemInUse(stack.copy())
                                val result = stack.useItemRightClick(world, player, EnumHand.MAIN_HAND)
                                if (result.type == EnumActionResult.SUCCESS) {
                                    return stack.count - result.result.count
                                }
                            }
                            return 0
                        }
                    }
                }
                return super.getPlantableSapling(stack)
            }
        })

        // add recipe based trees
        TreeWrapperFactory.treeWrappers.add(TreeFarmRegistry)

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
        if (!stack.isEmpty) {
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
