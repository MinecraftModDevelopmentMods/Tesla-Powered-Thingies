package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.IFluidTank

interface IAmFarmland {
    fun moisturize(water: IFluidTank, world: World, pos: BlockPos): Boolean
}