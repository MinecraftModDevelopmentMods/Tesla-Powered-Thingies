package net.ndrei.teslapoweredthingies.machines.electricbutcher

import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraftforge.items.ItemHandlerHelper
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.IAnimalAgeFilterAcceptor
import net.ndrei.teslapoweredthingies.items.BaseAnimalFilterItem
import net.ndrei.teslapoweredthingies.machines.BUTCHER_FARM_WORK_AREA_COLOR
import net.ndrei.teslapoweredthingies.machines.BaseXPCollectingMachine

/**
 * Created by CF on 2017-07-07.
 */
class ElectricButcherEntity
    : BaseXPCollectingMachine(ElectricButcherEntity::class.java.name.hashCode())
        , IAnimalAgeFilterAcceptor {

    override val inputSlots: Int
        get() = 1

    override fun acceptsInputStack(slot: Int, stack: ItemStack): Boolean {
        if (stack.isEmpty)
            return true

        if (slot == 0) {
            // test for weapon
            val map = stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND)
            if (map.containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.name)) {
                val modifiers = map.get(SharedMonsterAttributes.ATTACK_DAMAGE.name)
                for (modifier in modifiers) {
                    if (modifier.amount > 0) {
                        return true
                    }
                }
            }
        }

        return false
    }

    override fun acceptsFilter(item: BaseAnimalFilterItem): Boolean {
        return !super.hasAddon(BaseAnimalFilterItem::class.java)
    }

    override fun getWorkAreaColor(): Int = BUTCHER_FARM_WORK_AREA_COLOR

    override fun performWorkInternal(): Float {
        var result = 0.0f

        val facing = super.facing
        val cube = this.getWorkArea(facing.opposite, 1)
        val aabb = cube.boundingBox

        //region attack animal

        val stack = this.inStackHandler!!.getStackInSlot(0)
        if (!stack.isEmpty) {
            // find animal
            val list = this.getWorld().getEntitiesWithinAABB(EntityAnimal::class.java, aabb)
            val filter = super.getAddon(BaseAnimalFilterItem::class.java)
            var animalToHurt: EntityAnimal? = null
            if (list != null && list!!.size > 0) {
                for (i in list!!.indices) {
                    val thingy = list!!.get(i)

                    if (/*(animalToHurt == null) && */filter == null || filter!!.canProcess(this, i, thingy)) {
                        animalToHurt = thingy
                        break
                    }
                }
            }
            if (animalToHurt != null) {
                val player = TeslaThingiesMod.getFakePlayer(this.getWorld())
                if (player != null) {
                    player.setItemInUse(stack.copy())
                    val health = animalToHurt.health
                    player.attackTargetEntityWithCurrentItem(animalToHurt)
                    animalToHurt.health -= (health - animalToHurt.health) * 4 // to speed things up

                    val weapon = player.getHeldItem(EnumHand.MAIN_HAND)
                    this.inStackHandler!!.setStackInSlot(0, if (weapon.isEmpty) ItemStack.EMPTY else weapon.copy())

                    player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY)
                    result += .9f
                }
            }
        }

        //endregion

        //region collect loot

        val items = this.getWorld().getEntitiesWithinAABB(EntityItem::class.java, aabb)
        var pickedUpLoot = false
        if (!items.isEmpty()) {
            for (item in items) {
                val original = item.item
                // TODO: add a method for picking up EntityItems at super class
                val remaining = ItemHandlerHelper.insertItem(this.outStackHandler, original, false)
                if (remaining.isEmpty) {
                    this.getWorld().removeEntity(item)
                    pickedUpLoot = true
                } else if (remaining.count != original.count) {
                    item.item = remaining
                    pickedUpLoot = true
                }
            }
        }
        if (pickedUpLoot) {
            result += .1f
        }

        //endregion

        return result
    }
}
