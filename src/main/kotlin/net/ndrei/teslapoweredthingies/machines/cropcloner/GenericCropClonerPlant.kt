package net.ndrei.teslapoweredthingies.machines.cropcloner

import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

/**
 * Created by CF on 2017-07-15.
 */
abstract class GenericCropClonerPlant: ICropClonerPlant {
    override fun getAgeProperty(thing: IBlockState): PropertyInteger? {
        for (p in thing.propertyKeys) {
            if (p is PropertyInteger && p.getName() === "age") {
                return p
            }
        }
        return null
    }

    override fun getDrops(world: World, pos: BlockPos, state: IBlockState): List<ItemStack> {
        val stacks = NonNullList.create<ItemStack>()
        state.block.getDrops(stacks, world, pos, state, 0)
        return stacks.toList()
    }

    override fun grow(thing: IBlockState, ageProperty: PropertyInteger, rand: Random): IBlockState {
        if (rand.nextInt(3) == 1) {
            return thing.withProperty(ageProperty, thing.getValue(ageProperty) + 1)
        }
        return thing
    }
}