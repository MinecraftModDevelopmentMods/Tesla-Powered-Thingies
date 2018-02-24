package net.ndrei.teslapoweredthingies.machines.portabletank

import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

object SimpleTankItem : ItemBlock(SimpleTankBlock) {
    init {
        this.registryName = SimpleTankBlock.registryName
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?)=
        SimpleTankCaps(stack, 1, SimpleTankEntity.TANK_CAPACITY)
}
