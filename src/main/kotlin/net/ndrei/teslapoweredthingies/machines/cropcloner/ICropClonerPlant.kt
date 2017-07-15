package net.ndrei.teslapoweredthingies.machines.cropcloner

import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

/**
 * Created by CF on 2017-07-15.
 */
interface ICropClonerPlant {
    fun getAgeProperty(thing: IBlockState): PropertyInteger?
    fun getDrops(world: World, pos: BlockPos, state: IBlockState): List<ItemStack>
    fun grow(thing: IBlockState, ageProperty: PropertyInteger, rand: Random): IBlockState
}