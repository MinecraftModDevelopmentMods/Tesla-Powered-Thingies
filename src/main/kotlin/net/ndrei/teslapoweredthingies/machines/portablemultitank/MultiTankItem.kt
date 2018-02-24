package net.ndrei.teslapoweredthingies.machines.portablemultitank

import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.ndrei.teslapoweredthingies.machines.portabletank.SimpleTankCaps

object MultiTankItem : ItemBlock(MultiTankBlock) {
    init {
        this.registryName = MultiTankBlock.registryName
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?)=
        SimpleTankCaps(stack, 4, MultiTankEntity.TANK_CAPACITY)
}
