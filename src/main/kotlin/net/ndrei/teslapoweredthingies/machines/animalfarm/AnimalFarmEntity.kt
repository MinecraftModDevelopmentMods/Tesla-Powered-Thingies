package net.ndrei.teslapoweredthingies.machines.animalfarm

import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagFloat
import net.minecraft.nbt.NBTTagInt
import net.minecraft.nbt.NBTTagString
import net.minecraftforge.items.ItemHandlerHelper
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.IAnimalAgeFilterAcceptor
import net.ndrei.teslapoweredthingies.items.AnimalPackageItem
import net.ndrei.teslapoweredthingies.items.BaseAnimalFilterItem
import net.ndrei.teslapoweredthingies.machines.BaseXPCollectingMachine

/**
 * Created by CF on 2017-07-06.
 */
class AnimalFarmEntity
    : BaseXPCollectingMachine(AnimalFarmEntity::class.java.name.hashCode()), IAnimalAgeFilterAcceptor {

    private val ENERGY_PACKAGE = .9f
    private val ENERGY_FEED = .1f
    private val ENERGY_SHEAR = .3f
    private val ENERGY_MILK = .3f

    override fun acceptsInputStack(slot: Int, stack: ItemStack): Boolean {
        if (ItemStackUtil.isEmpty(stack))
            return true

        // test for animal package
        if (stack.item.registryName == AnimalPackageItem.getRegistryName()) {
            return !AnimalPackageItem.hasAnimal(stack)
        }

        return AnimalFarmEntity.foodItems.contains(stack.item)
    }

    override fun performWorkInternal(): Float {
        var result = 0.0f
        val toProcess = mutableListOf<IAnimalWrapper>()
        var animalToPackage: IAnimalWrapper? = null

        //region find animals

        val facing = super.facing
        val cube = this.getWorkArea(facing.opposite, 1)
        val aabb = cube.boundingBox

        // find animal
        val animals = this.getWorld().getEntitiesWithinAABB(EntityAnimal::class.java, aabb)
        val filter = super.getAddon(BaseAnimalFilterItem::class.java)
        if (animals.size > 0) {
            for (i in animals.indices) {
                val thingy = AnimalWrapperFactory.getAnimalWrapper(animals[i])

                if ((animalToPackage == null) && ((filter == null) || filter.canProcess(this, i, thingy.animal))) {
                    animalToPackage = thingy
                } else if (thingy.breedable()) {
                    toProcess.add(thingy)
                }
            }
        }

        //endregion

        //region process package

        if (animalToPackage != null) {
            var packageStack: ItemStack? = null
            var packageSlot = 0
            for (ti in 0..this.inStackHandler!!.getSlots() - 1) {
                packageStack = this.inStackHandler!!.extractItem(ti, 1, true)
                if (!ItemStackUtil.isEmpty(packageStack) && packageStack.item is AnimalPackageItem && !AnimalPackageItem.hasAnimal(packageStack)) {
                    packageSlot = ti
                    break
                }
                packageStack = null
            }
            if (!ItemStackUtil.isEmpty(packageStack)) {
                val animal = animalToPackage.animal
                val stackCopy = AnimalFarmEntity.packageAnimal(packageStack, animal)

                if (!ItemStackUtil.isEmpty(stackCopy) && super.outputItems(stackCopy)) {
                    this.getWorld().removeEntity(animalToPackage.animal)
                    this.inStackHandler!!.extractItem(packageSlot, 1, false)
                    animalToPackage = null
                    result += ENERGY_PACKAGE
                }
            }
        }

        if (animalToPackage != null) {
            toProcess.add(animalToPackage)
        }

        //endregion

        val minEnergy = Math.min(ENERGY_FEED, Math.min(ENERGY_MILK, ENERGY_SHEAR))
        if (toProcess.size >= 2 && 1.0f - result >= minEnergy) {
            for (i in toProcess.indices) {
                val wrapper = toProcess[i]
                if (wrapper.breedable() && 1.0f - result >= ENERGY_FEED) {
                    //region breed this thing

                    val potentialFood = ItemStackUtil.getCombinedInventory(this.inStackHandler!!)

                    var foodStack: ItemStack? = null
                    for (f in potentialFood.indices) {
                        val tempFood = potentialFood[f]
                        if (wrapper.isFood(tempFood)) {
                            foodStack = tempFood
                            break
                        }
                    }
                    if ((foodStack != null) && !foodStack.isEmpty) {
                        for (j in i + 1..toProcess.size - 1) {
                            val toMateWith = toProcess[j]
                            if (toMateWith.breedable() && toMateWith.isFood(foodStack) && wrapper.canMateWith(toMateWith)) {
                                val foodUsed = wrapper.mate(TeslaThingiesMod.getFakePlayer(this.getWorld())!!, foodStack, toMateWith)
                                if (foodUsed > 0 && foodUsed <= ItemStackUtil.getSize(foodStack)) {
                                    ItemStackUtil.extractFromCombinedInventory(this.inStackHandler!!, foodStack, foodUsed)
                                    ItemStackUtil.shrink(foodStack, foodUsed)
                                    result += ENERGY_FEED
                                    break
                                }
                            }
                        }
                    }

                    //endregion
                }

                if (wrapper.shearable() && 1.0f - result >= ENERGY_SHEAR) {
                    //region shear this unfortunate animal

                    var shearsSlot = -1
                    for (s in 0..this.inStackHandler!!.getSlots() - 1) {
                        if (wrapper.canBeShearedWith(this.inStackHandler!!.getStackInSlot(s))) {
                            shearsSlot = s
                            break
                        }
                    }
                    if (shearsSlot >= 0) {
                        val shears = this.inStackHandler!!.getStackInSlot(shearsSlot)
                        val loot = wrapper.shear(shears, 0)
                        if (loot != null && loot!!.size > 0) {
                            super.outputItems(loot) // TODO: test if successful

                            if (shears.attemptDamageItem(1, this.getWorld().rand, TeslaThingiesMod.getFakePlayer(this.getWorld())!!)) {
                                this.inStackHandler!!.setStackInSlot(shearsSlot, ItemStackUtil.emptyStack)
                            }

                            result += ENERGY_SHEAR
                        }
                    }

                    //endregion
                }

                if (wrapper.canBeMilked() && 1.0f - result >= ENERGY_MILK) {
                    //region no milk left behind!

                    for (b in 0..this.inStackHandler!!.getSlots() - 1) {
                        val stack = this.inStackHandler!!.extractItem(b, 1, true)
                        if (ItemStackUtil.getSize(stack) == 1 && stack.getItem() === Items.BUCKET) {
                            val milk = wrapper.milk()
                            if (super.outputItems(milk)) {
                                this.inStackHandler!!.extractItem(b, 1, false)
                                result += ENERGY_MILK
                                break
                            }
                        }
                    }

                    //endregion
                }

                //region mushroom stew best stew

                if (wrapper.canBeBowled() && 1.0f - result >= ENERGY_MILK) {
                    for (b in 0..this.inStackHandler!!.getSlots() - 1) {
                        val stack = this.inStackHandler!!.extractItem(b, 1, true)
                        if (ItemStackUtil.getSize(stack) == 1 && stack.getItem() === Items.BOWL) {
                            val stew = wrapper.bowl()
                            if (!ItemStackUtil.isEmpty(stew) && super.outputItems(stew)) {
                                this.inStackHandler!!.extractItem(b, 1, false)
                                result += ENERGY_MILK
                                break
                            }
                        }
                    }
                }

                //endregion

                if (1.0f - result < minEnergy) {
                    break // no more energy
                }
            }
        }

        //region collect loot

        if (result <= .9f) {
            val items = this.getWorld().getEntitiesWithinAABB(EntityItem::class.java, aabb)
            if (!items.isEmpty()) {
                for (item in items) {
                    val original = item.item
                    // TODO: add a method for picking up EntityItems at super class
                    val remaining = ItemHandlerHelper.insertItem(this.outStackHandler, original, false)
                    var pickedUpLoot = false
                    if (ItemStackUtil.isEmpty(remaining)) {
                        this.getWorld().removeEntity(item)
                        pickedUpLoot = true
                    } else if (ItemStackUtil.getSize(remaining) != ItemStackUtil.getSize(original)) {
                        item.item = remaining
                        pickedUpLoot = true
                    }

                    if (pickedUpLoot) {
                        result += 1.0f
                        if (result > .9f) {
                            break
                        }
                    }
                }
            }
        }

        //endregion

        return result
    }

    override fun acceptsFilter(item: BaseAnimalFilterItem): Boolean {
        return !super.hasAddon(BaseAnimalFilterItem::class.java)
    }

    companion object {
        private val foodItems = mutableListOf<Item>()

        init {
            AnimalFarmEntity.foodItems.add(Items.SHEARS)
            AnimalFarmEntity.foodItems.add(Items.BUCKET)
            AnimalFarmEntity.foodItems.add(Items.BOWL)
            // ^^ not really food :D

            AnimalWrapperFactory.populateFoodItems(AnimalFarmEntity.foodItems)
        }

        internal fun packageAnimal(packageStack: ItemStack?, animal: EntityAnimal): ItemStack {
            val stackCopy = if (packageStack == null) ItemStack(AnimalPackageItem) else packageStack.copy()

            stackCopy.setTagInfo("hasAnimal", NBTTagInt(1))
            val animalCompound = NBTTagCompound()
            animal.writeEntityToNBT(animalCompound)
            stackCopy.setTagInfo("animal", animalCompound)
            stackCopy.setTagInfo("animalClass", NBTTagString(animal.javaClass.name))
            stackCopy.setTagInfo("animalHealth", NBTTagFloat(animal.health))
            return stackCopy
        }
    }
}