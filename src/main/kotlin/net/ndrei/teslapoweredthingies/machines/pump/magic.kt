package net.ndrei.teslapoweredthingies.machines.pump

import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidBlock

internal fun IBlockState.getFluidWrapper(): IFluidBlock? {
    if (this.block is IFluidBlock)
        return this.block as? IFluidBlock
    else if (this.block is BlockLiquid) {
        return object: IFluidBlock {
            private lateinit var state: IBlockState
            private lateinit var block: BlockLiquid

            override fun place(world: World?, pos: BlockPos?, fluidStack: FluidStack, doPlace: Boolean): Int {
                TODO("not implemented")
            }

            override fun drain(world: World, pos: BlockPos, doDrain: Boolean): FluidStack? {
                val stack = if (world.isBlockLoaded(pos)) {
                    val state = world.getBlockState(pos)
                    if (state.block is BlockLiquid) {
                        val level = state.getValue(BlockLiquid.LEVEL)
                        if (level == 0) {
                            val fluid = FluidStack(this.fluid, Fluid.BUCKET_VOLUME)
                            if (doDrain) {
                                world.setBlockToAir(pos)
                            }
                            fluid
                        } else null
                    } else null
                } else null
                return stack
            }

            override fun getFluid(): Fluid = when (block.getMaterial(this.state)) {
                Material.WATER -> FluidRegistry.WATER
            /*Material.LAVA*/ else -> FluidRegistry.LAVA
            }

            override fun getFilledPercentage(world: World, pos: BlockPos) =
                BlockLiquid.getLiquidHeightPercent(this.block.getMetaFromState(world.getBlockState(pos)))

            // only "source" blocks can be drained
            override fun canDrain(world: World, pos: BlockPos) =
                world.isBlockLoaded(pos) && world.getBlockState(pos).let {
                    (it.block is BlockLiquid) && (it.getValue(BlockLiquid.LEVEL) == 0)
                }

            fun initialize(block: BlockLiquid, state: IBlockState) = this.also {
                it.block = block
                it.state = state
            }
        }.initialize(this.block as BlockLiquid, this)
    }
    return null
}
