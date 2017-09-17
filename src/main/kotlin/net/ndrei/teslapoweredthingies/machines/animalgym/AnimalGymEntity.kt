package net.ndrei.teslapoweredthingies.machines.animalgym

import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.ButtonPiece
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.SyncItemHandler
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage
import net.ndrei.teslacorelib.tileentities.ElectricGenerator
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.client.Textures
import net.ndrei.teslapoweredthingies.gui.AnimalGymInfoPiece
import net.ndrei.teslapoweredthingies.items.AnimalPackageItem
import net.ndrei.teslapoweredthingies.machines.animalfarm.AnimalFarmEntity

/**
 * Created by CF on 2017-07-08.
 */
class AnimalGymEntity : ElectricGenerator(AnimalGymEntity::class.java.name.hashCode()) {
    private lateinit var inStackHandler: ItemStackHandler
    private lateinit var outStackHandler: ItemStackHandler

    //#region inventories       methods

    override fun initializeInventories() {
        super.initializeInventories()

        this.inStackHandler = /*object :*/ SyncItemHandler(3)/* {
            override fun onContentsChanged(slot: Int) {
                this@AnimalGymEntity.markDirty()
            }
        }*/
        super.addInventory(object : ColoredItemHandler(this.inStackHandler, EnumDyeColor.GREEN, "Input Items", BoundingRectangle(61, 25, 18, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack)
                = this@AnimalGymEntity.acceptsInputStack(/*slot, */stack)

            override fun canExtractItem(slot: Int) = false
        })
        super.addInventoryToStorage(this.inStackHandler, "gym_inputs")

        this.outStackHandler = /*object :*/ SyncItemHandler(3)/* {
            override fun onContentsChanged(slot: Int) {
                this@AnimalGymEntity.markDirty()
            }
        }*/
        super.addInventory(object : ColoredItemHandler(this.outStackHandler, EnumDyeColor.PURPLE, "Output Items", BoundingRectangle(151, 25, 18, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack) =  false

            override fun canExtractItem(slot: Int): Boolean = true
        })
        super.addInventoryToStorage(this.outStackHandler, "gym_outputs")
    }

    private fun acceptsInputStack(/*slot: Int, */stack: ItemStack)
        = !stack.isEmpty && (stack.item === AnimalPackageItem) && AnimalPackageItem.hasAnimal(stack)

    //#endregion
    //#region storage           methods

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        if (compound.hasKey("athlete", Constants.NBT.TAG_COMPOUND) && compound.hasKey("athleteType", Constants.NBT.TAG_STRING)) {
            val animalType = compound.getString("athleteType")
            try {
                this.currentAnimalClass = Class.forName(animalType)
                this.currentAnimalTag = compound.getCompoundTag("athlete")
            } catch (t: Throwable) {
                TeslaThingiesMod.logger.error("Error deserializing animal gym athlete.", t)
                this.currentAnimal = null
                this.currentAnimalClass = null
                this.currentAnimalTag = null
            }

        } else {
            this.currentAnimal = null
            this.currentAnimalClass = null
            this.currentAnimalTag = null
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val nbt = super.writeToNBT(compound)

        val current = this.current
        if (current != null) {
            val animalCompound = current.writeToNBT(NBTTagCompound())
            if (animalCompound != null) {
                nbt.setString("athleteType", current.javaClass.name)
                nbt.setTag("athlete", animalCompound)
            }
        }

        return nbt
    }

    //#endregion
    //#region rendering         methods

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = super.getGuiContainerPieces(container)

        list.add(AnimalGymInfoPiece(this, 88, 25))

        list.add(object : ButtonPiece(132, 29, 8, 7) {
            override fun renderState(container: BasicTeslaGuiContainer<*>, over: Boolean, box: BoundingRectangle) {
                if (this@AnimalGymEntity.current == null) {
                    return
                }

                container.mc.textureManager.bindTexture(Textures.FARM_TEXTURES.resource)
                container.drawTexturedRect(box.left - container.guiLeft, box.top - container.guiTop,
                        56, if (over) 9 else 1, 8, 7)
            }

            override fun clicked() {
                if (this@AnimalGymEntity.current == null) {
                    return
                }

                val nbt = this@AnimalGymEntity.setupSpecialNBTMessage("PACKAGE_ITEM")
                TeslaCoreLib.network.sendToServer(SimpleNBTMessage(this@AnimalGymEntity, nbt))
            }
        })

        return list
    }

    override fun processClientMessage(messageType: String?, compound: NBTTagCompound): SimpleNBTMessage? {
        if (messageType != null && messageType == "PACKAGE_ITEM") {
            this.packageCurrent()
        }

        return super.processClientMessage(messageType, compound)
    }

    //#endregion

    private val teslaPerHeart = 8400
    private val teslaSpeedMultiplier = 320f

    private var currentAnimalClass: Class<*>? = null
    private var currentAnimalTag: NBTTagCompound? = null
    private var currentAnimal: EntityAnimal? = null

    private val current: EntityAnimal?
        get() {
            if (this.currentAnimalClass != null && this.currentAnimalTag != null) {
                try {
                    val thing = this.currentAnimalClass!!.getConstructor(World::class.java).newInstance(this.getWorld())
                    if (thing is EntityAnimal) {
                        val animalCompound = this.currentAnimalTag
                        if (animalCompound!!.hasKey("Attributes", 9) && this.getWorld().isRemote) {
                            // this is the reverse of what EntityLiving does... so that attributes work on client side
                            // try to unpackage a new animal
                            SharedMonsterAttributes.setAttributeModifiers(thing.attributeMap,
                                    animalCompound.getTagList("Attributes", 10))
                        }
                        thing.deserializeNBT(animalCompound)
                        this.currentAnimal = thing
                        this.markDirty()
                    }
                } catch (t: Throwable) {
                    TeslaThingiesMod.logger.error("Error deserializing animal gym athlete.", t)
                    this.currentAnimal = null
                    this.markDirty()
                } finally {
                    this.currentAnimalClass = null
                    this.currentAnimalTag = null
                    this.markDirty()
                }
            }

            if (this.currentAnimal == null) {
                for (index in 0 until this.inStackHandler.slots) {
                    val stack = this.inStackHandler.getStackInSlot(index)
                    if (!stack.isEmpty && stack.item === AnimalPackageItem && AnimalPackageItem.hasAnimal(stack)) {
                        this.inStackHandler.setStackInSlot(index, ItemStack.EMPTY)
                        this.currentAnimal = AnimalPackageItem.unpackage(this.getWorld(), stack)
                        this.forceSync()
                        return if (this.currentAnimal == null) null else this.current
                    }
                }

                return null
            }

            return this.currentAnimal
        }

    private fun packageCurrent(): Boolean {
        val ea = this.current ?: return false

        val remaining = ItemHandlerHelper.insertItem(this.outStackHandler,
                AnimalFarmEntity.packageAnimal(null, ea),
                false)
        if (remaining.isEmpty) {
            this.currentAnimal = null
            this.forceSync()
            this.markDirty()
            return true
        }
        return false
    }

    override fun consumeFuel(): Long {
        var ea: EntityAnimal = this.current ?: return 0

        if (ea.health <= 2.0) {
            // see if the animal is lucky to live
            if (this.getWorld().rand.nextFloat() > .2) {
                // 80% chance of surviving
                if (this.packageCurrent()) {
                    ea = this.current ?: return 0
                }
            }
        }

        ea.health = ea.health - 1.0f
        if (ea.health < 0.001f) {
            // sometimes it happens :S
            this.currentAnimal = null
            this.forceSync()
            this.markDirty()
            return 0
        }
        return this.teslaPerHeart.toLong()
    }

    override val energyFillRate: Long
        get() = Math.round(this.teslaSpeedMultiplier * this.speedForCurrent).toLong()

    val currentAnimalType: String
        get() {
            val a = this.current
            return if (a == null) "n/a" else a.name
        }

    fun getCurrentAnimal(): EntityAnimal? {
        return this.current
    }

    private val speedForCurrent: Float
        get() {
            val a = this.current
            return a?.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)?.baseValue?.toFloat() ?: 0.0f
        }

    val powerPerTick: Float
        get() = this.teslaSpeedMultiplier * this.speedForCurrent

    private val enduranceForCurrent: Float
        get() {
            val a = this.current
            return a?.health ?: 0.0f
        }

//    val lifespanForCurrent: Int
//        get() = Math.round(this.enduranceForCurrent * 60.0f * 20.0f)

    val maxPowerForCurrent: Int
        get() = Math.round(this.teslaPerHeart * this.enduranceForCurrent)
}
