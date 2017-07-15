package net.ndrei.teslapoweredthingies.machines.cropcloner

import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.ndrei.teslapoweredthingies.blocks.TeslaPlantBlock

/**
 * Created by CF on 2017-07-15.
 */
object CropClonerPlantFactory {
    fun getPlant(state: IBlockState): ICropClonerPlant
        = when (state.block) {
            TeslaPlantBlock ->  TeslaPlantCloner
            Blocks.MELON_STEM -> MelonPlantCloner
            Blocks.PUMPKIN_STEM -> PumpkinPlantCloner
            else -> VanillaCropClonerPlant
        }
}