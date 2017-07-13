package net.ndrei.teslapoweredthingies.machines.animalreleaser

import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.utils.BlockPosUtils
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.items.AnimalPackageItem
import net.ndrei.teslapoweredthingies.machines.ANIMAL_FARM_WORK_AREA_COLOR
import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine

/**
 * Created by CF on 2017-07-07.
 */
class AnimalReleaserEntity
    : ElectricFarmMachine(AnimalReleaserEntity::class.java.name.hashCode()) {

    override fun acceptsInputStack(slot: Int, stack: ItemStack): Boolean {
        if (stack.isEmpty)
            return true

        // test for animal package
        if (stack.item.registryName == AnimalPackageItem.registryName) {
            return AnimalPackageItem.hasAnimal(stack)
        }
        return false
    }

    override fun supportsAddons() = false

    override fun getWorkAreaColor(): Int = ANIMAL_FARM_WORK_AREA_COLOR

    override fun performWork(): Float {
        var stack = ItemStackUtil.emptyStack
        var stackIndex = 0
        while (stackIndex < this.inStackHandler!!.getSlots()) {
            stack = this.inStackHandler!!.extractItem(stackIndex, 1, true)
            if (!ItemStackUtil.isEmpty(stack)) {
                break
            }
            stackIndex++
        }
        if (!ItemStackUtil.isEmpty(stack)) {
            val stackCopy = stack.copy()
            if (stackCopy.item is AnimalPackageItem && stackCopy.hasTagCompound()) {
                val compound = stackCopy.tagCompound
                compound!!.removeTag("hasAnimal")
                if (compound.hasKey("animal") && compound.hasKey("animalClass")) {
                    val animal = compound.getCompoundTag("animal")
                    val animalClass = compound.getString("animalClass")
                    try {
                        val cea = Class.forName(animalClass)
                        val thing = cea.getConstructor(World::class.java).newInstance(this.getWorld())
                        if (thing is EntityAnimal) {
                            val ea = thing
                            ea.readEntityFromNBT(animal)

                            val facing = super.facing.opposite
                            val cube = BlockPosUtils.getCube(this.getPos().offset(facing, 1), facing, this.range - 1, 1)
                            val pos = cube.getRandomInside(this.getWorld().rand)
                            ea.setPosition(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())

                            stackCopy.tagCompound = null
                            if (super.outputItems(stackCopy)) {
                                this.inStackHandler!!.extractItem(stackIndex, 1, false)
                                this.getWorld().spawnEntity(ea)
                                return 1.0f
                            }
                        }
                    } catch (e: Throwable) {
                        TeslaThingiesMod.logger.warn("Error creating animal '$animalClass'.", e)
                    }

                }
            }
        }
        return 0.0f
    }
}
