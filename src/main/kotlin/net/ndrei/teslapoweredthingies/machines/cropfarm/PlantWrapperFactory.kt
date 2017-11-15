package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.block.IGrowable
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.IPlantable

/**
 * Created by CF on 2017-07-07.
 */
object PlantWrapperFactory {
    fun getSeedWrapper(seeds: ItemStack): ISeedWrapper? {
        if (seeds.isEmpty) {
            return null
        }
        val seed = seeds.item

        if (RusticBlockStakeSeed.isSeed(seeds)) {
            return RusticBlockStakeSeed(seeds)
        }

        if (seed === Items.MELON_SEEDS || seed === Items.PUMPKIN_SEEDS) {
            return VanillaMelonSeed(seeds.copy())
        }

        if (seed === Items.REEDS) {
            return VanillaReedsSeed(seeds.copy())
        }

        if (seed === Item.getItemFromBlock(Blocks.CACTUS)) {
            return VanillaCactusSeed(seeds.copy())
        }

        if (VanillaNetherWartSeed.isSeed(seeds)) {
            return VanillaNetherWartSeed(seeds.copy())
        }

        if (seed is IPlantable) {
            return VanillaGenericSeed(seeds.copy())
        }

        return null
    }

    fun isSeed(stack: ItemStack): Boolean {
        return VanillaGenericSeed.isSeed(stack)
                || RusticBlockStakeSeed.isSeed(stack)
                || VanillaCactusSeed.isSeed(stack)
                || VanillaMelonSeed.isSeed(stack)
                || VanillaReedsSeed.isSeed(stack)
                || VanillaNetherWartSeed.isSeed(stack)
    }

    fun getPlantWrapper(world: World, pos: BlockPos): IPlantWrapper? {
        val state = world.getBlockState(pos)
        val block = state.block

        if ((block.javaClass.name.startsWith("com.infinityraider.agricraft.blocks")
            || block.javaClass.name.startsWith("rustic.common.blocks.crops"))
            && (block is IGrowable)) {
            return RightClickPlantWrapper(block, state, world, pos)
        }

        if (block === Blocks.MELON_STEM || block === Blocks.PUMPKIN_STEM) {
            return VanillaMelonPlant(block, state, world, pos)
        }

        if (block === Blocks.REEDS) {
            return VanillaTallPlant(block, state, world, pos)
        }

        if (block === Blocks.CACTUS) {
            return VanillaCactusPlant(block, state, world, pos)
        }

        if (block === Blocks.NETHER_WART) {
            return VanillaNetherWartPlant(state, world, pos)
        }

        if (ImmersiveHempPlant.isMatch(state)) {
            return ImmersiveHempPlant(world, pos)
        }

        if (block is IGrowable) {
            return VanillaGenericPlant(block, state, world, pos)
        }

        return null
    }

    fun isFertilizer(stack: ItemStack): Boolean {
        return stack.item === Items.DYE && stack.metadata == 15
    }
}
