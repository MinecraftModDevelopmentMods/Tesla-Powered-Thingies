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
import net.ndrei.teslapoweredthingies.fluids.MoltenTeslaFluid

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterBlock
object MoltenTeslaBlock
    : FiniteFluidThingyBlock(MoltenTeslaFluid, MapColor.CYAN) {

    override fun onEntityCollidedWithBlock(world: World?, pos: BlockPos?, state: IBlockState?, entity: Entity?) {
        if ((world != null) && (pos != null) && (entity is EntityLivingBase)) {
            val quanta = this.getQuantaValue(world, pos)
            if (quanta > 0) {
                entity.addPotionEffect(PotionEffect(MobEffects.WITHER, quanta * 100 / 5))
                entity.addPotionEffect(PotionEffect(MobEffects.GLOWING, quanta * 100 / 5))
            }
        }
    }
}