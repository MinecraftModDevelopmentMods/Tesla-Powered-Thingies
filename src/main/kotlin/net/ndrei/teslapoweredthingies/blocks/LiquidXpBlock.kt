package net.ndrei.teslapoweredthingies.blocks

import net.minecraft.block.material.MapColor
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.init.MobEffects
import net.minecraft.potion.PotionEffect
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslapoweredthingies.fluids.LiquidXPFluid

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterBlock
object LiquidXpBlock
    : FiniteFluidThingyBlock(LiquidXPFluid, MapColor.LIME) {

    override fun onEntityCollision(worldIn: World?, pos: BlockPos?, state: IBlockState, entityIn: Entity?) {
        if ((worldIn != null) && (pos != null) && (entityIn is EntityLivingBase)) {
            val quanta = this.getQuantaValue(worldIn, pos)
            if (quanta > 0) {
                entityIn.addPotionEffect(PotionEffect(MobEffects.REGENERATION, quanta * 100 / 15))
            }
        }
    }
}