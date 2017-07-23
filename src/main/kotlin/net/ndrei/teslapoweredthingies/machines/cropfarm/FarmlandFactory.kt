package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.block.BlockFarmland
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.IFluidTank

object FarmlandFactory {
    fun getFarmland(world: World, pos: BlockPos): IAmFarmland? {
        val state = world.getBlockState(pos)
        if (state.block is BlockFarmland) {
            return object: IAmFarmland {
                @Suppress("NAME_SHADOWING")
                override fun moisturize(water: IFluidTank, world: World, pos: BlockPos): Boolean {
                    val state = world.getBlockState(pos)
                    val moisture = state.getValue(BlockFarmland.MOISTURE)
                    val fluidNeeded = Math.min(2, 7 - moisture) * 15
                    if ((fluidNeeded > 0) && (water.fluidAmount >= fluidNeeded)) {
                        world.setBlockState(pos, state.withProperty(BlockFarmland.MOISTURE, Math.min(7, moisture + 2)))
                        water.drain(fluidNeeded, true)
                        return true
                    }

                    return false
                }
            }
        }

        return null
    }
}