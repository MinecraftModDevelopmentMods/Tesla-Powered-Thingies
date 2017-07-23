package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.util.FakePlayer

interface IAmHoe {
    fun hoe(player: FakePlayer, hoe: ItemStack, world: World, pos: BlockPos): Boolean
}
