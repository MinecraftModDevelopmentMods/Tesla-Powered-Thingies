package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.util.FakePlayer

object VanillaHoe: IAmHoe {
    override fun hoe(player: FakePlayer, hoe: ItemStack, world: World, pos: BlockPos)
            =  EnumActionResult.SUCCESS == hoe.onItemUse(player, world, pos, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 1.0f, 0.5f)
}
