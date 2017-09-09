package net.ndrei.teslapoweredthingies.machines.animalfarm

import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityCow
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraftforge.common.util.Constants
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

/**
 * Created by CF on 2017-07-07.
 */
class MooFluidCow(private val cow: EntityCow)
    : IAnimalWrapper {

    override val animal: EntityAnimal
        get() = this.cow

    //#region unsupported methods

    override fun mate(player: EntityPlayer, stack: ItemStack, wrapper: IAnimalWrapper): Int {
        return 0
    }

    override fun shearable(): Boolean {
        return false
    }

    override fun canBeShearedWith(stack: ItemStack): Boolean {
        return false
    }

    override fun shear(stack: ItemStack, fortune: Int): List<ItemStack> {
        return listOf()
    }

    override fun canBeBowled(): Boolean {
        return false
    }

    override fun bowl(): ItemStack {
        return ItemStack.EMPTY
    }

    //#endregion

    //#region breeding

    override fun breedable(): Boolean {
        return true
    }

    override fun isFood(stack: ItemStack): Boolean {
        return this.cow.isBreedingItem(stack)
    }

    override fun canMateWith(wrapper: IAnimalWrapper): Boolean {
        return this.cow.canMateWith(wrapper.animal)
    }

    //#endregion

    //#region "milking"

    override fun canBeMilked(): Boolean {
        val nbt = this.cow.serializeNBT()
        return when {
            nbt.hasKey("NextUseCooldown", Constants.NBT.TAG_INT) -> nbt.getInteger("NextUseCooldown") == 0
            nbt.hasKey("CurrentUseCooldown", Constants.NBT.TAG_INT) -> nbt.getInteger("CurrentUseCooldown") == 0
            else -> false
        }
    }

    override fun milk(): ItemStack {
        val player = TeslaThingiesMod.getFakePlayer(this.cow.entityWorld)
        if (player != null) {
            player.activeHand = EnumHand.MAIN_HAND
            player.setHeldItem(EnumHand.MAIN_HAND, ItemStack(Items.BUCKET))
            if (this.cow.processInteract(player, EnumHand.MAIN_HAND)) {
                return player.heldItemMainhand
            }
        }
        return ItemStack.EMPTY
    }

    //#endregion
}
