package net.ndrei.teslapoweredthingies.machines.cropcloner

import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.IBlockState
import java.util.*

/**
 * Created by CF on 2017-07-15.
 */
object TeslaPlantCloner : GenericCropClonerPlant() {
    override fun grow(thing: IBlockState, ageProperty: PropertyInteger, rand: Random): IBlockState {
        if (rand.nextInt(10) == 1) {
            return thing.withProperty(ageProperty, thing.getValue(ageProperty) + 1)
        }
        return thing
    }
}