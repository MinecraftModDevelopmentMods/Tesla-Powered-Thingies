package net.ndrei.teslapoweredthingies.machines.cropcloner

import net.minecraft.block.Block
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.IBlockState
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.EnumPlantType
import net.minecraftforge.common.IPlantable
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.IFluidTank
import net.ndrei.teslacorelib.capabilities.hud.HudInfoLine
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine
import java.awt.Color

/**
 * Created by CF on 2017-07-07.
 */
class CropClonerEntity : ElectricFarmMachine(CropClonerEntity::class.java.name.hashCode()) {
    var plantedThing: IBlockState? = null
        private set
    private var waterTank: IFluidTank? = null

    override fun supportsRangeAddons() = false

    override fun supportsAddons() = false

    override fun initializeInventories() {
        super.initializeInventories()

        this.waterTank = super.addFluidTank(FluidRegistry.WATER, 5000, EnumDyeColor.BLUE, "Water Tank",
                BoundingRectangle(43, 25, 18, 54))
    }

    override val inputSlots: Int
        get() = 1

    override fun acceptsInputStack(slot: Int, stack: ItemStack): Boolean {
        if (ItemStackUtil.isEmpty(stack)) {
            return false
        }

        if (stack.item is IPlantable) {
            val plant = stack.item as IPlantable
            if (plant.getPlantType(this.getWorld(), this.getPos()) == EnumPlantType.Crop) {
                return true
            }
        }
        return false
    }

    override fun performWork(): Float {
        var result = 0.0f
        // EnumFacing facing = super.getFacing();

        if (this.plantedThing != null) {
            val ageProperty = this.getAgeProperty(this.plantedThing)
            if (ageProperty != null) {
                var age = this.plantedThing!!.getValue(ageProperty)
                val ages = ageProperty.allowedValues.toTypedArray()
                if (age == ages[ages.size - 1]) {
                    val stacks = this.plantedThing!!.block.getDrops(this.getWorld(), this.getPos(), this.plantedThing!!, 0)
                    if (super.outputItems(stacks)) {
                        this.plantedThing = null
                        result += .85f
                    }
                } else {
                    this.plantedThing = this.plantedThing!!.withProperty(ageProperty, ++age)
                    result += .85f
                }
                this.onPlantedThingChanged()
            }
        }

        if (this.plantedThing == null && this.waterTank != null && this.waterTank!!.fluidAmount >= 250) {
            val stack = this.inStackHandler!!.getStackInSlot(0)
            if (!ItemStackUtil.isEmpty(stack) && stack.getItem() is IPlantable) {
                val plantable = stack.getItem() as IPlantable
                if (plantable.getPlantType(this.getWorld(), this.getPos()) == EnumPlantType.Crop) {
                    this.plantedThing = plantable.getPlant(this.getWorld(), this.getPos())
                    this.waterTank!!.drain(250, true) // TODO: <-- do this better
                    this.onPlantedThingChanged()
                }
            }
            result += .15f
        }

        return result
    }

    private fun onPlantedThingChanged() {
        if (null != this.getWorld() && null != this.getPos()) { // <-- weird, but it actually happens!!
            val state = if (this.plantedThing == null) 0 else 1
            val block = this.getWorld().getBlockState(this.getPos())
            if (block.getValue(CropClonerBlock.STATE) != state) {
                CropClonerBlock.setState(block.withProperty(CropClonerBlock.STATE, state), this.getWorld(), this.getPos())
            }
        }

        this.markDirty()
        this.forceSync()
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        if (compound.hasKey("plantDomain") && compound.hasKey("plantPath")) {
            val location = ResourceLocation(
                    compound.getString("plantDomain"),
                    compound.getString("plantPath"))
            val block = Block.REGISTRY.getObject(location)
            if (block != null) {
                this.plantedThing = block.defaultState
                this.onPlantedThingChanged()
            }
        }

        if (compound.hasKey("plantAge") && this.plantedThing != null) {
            val age = compound.getInteger("plantAge")
            val ageProperty = this.getAgeProperty(this.plantedThing)
            if (ageProperty != null) {
                this.plantedThing = this.plantedThing!!.withProperty(ageProperty, age)
                this.onPlantedThingChanged()
            }
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        var compound = compound
        compound = super.writeToNBT(compound)

        if (this.plantedThing != null) {
            val resource = this.plantedThing!!.block.registryName
            compound.setString("plantDomain", resource!!.resourceDomain)
            compound.setString("plantPath", resource.resourcePath)
            val ageProperty = this.getAgeProperty(this.plantedThing)
            if (ageProperty != null) {
                compound.setInteger("plantAge", this.plantedThing!!.getValue(ageProperty))
            }
        }

        return compound
    }

    private fun getAgeProperty(thing: IBlockState?): PropertyInteger? {
        if (thing != null) {
            for (p in thing.propertyKeys) {
                if (p is PropertyInteger && p.getName() === "age") {
                    return p
                }
            }
        }
        return null
    }

    override val hudLines: List<HudInfoLine>
        get() {
            var list = super.hudLines.toMutableList()

            if (this.plantedThing == null) {
                list.add(HudInfoLine(Color(255, 159, 51),
                        Color(255, 159, 51, 42),
                        "no seed")
                        .setTextAlignment(HudInfoLine.TextAlignment.CENTER))
            } else {
                list.add(HudInfoLine(Color.WHITE, this.plantedThing!!.block.localizedName)
                        .setTextAlignment(HudInfoLine.TextAlignment.CENTER))
                val age = this.getAgeProperty(this.plantedThing)
                if (age != null) {
                    val percent = this.plantedThing!!.getValue(age) * 100 / age.allowedValues.size
                    list.add(HudInfoLine(Color.CYAN,
                            Color(Color.GRAY.red, Color.GRAY.green, Color.GRAY.blue, 192),
                            Color(Color.CYAN.red, Color.CYAN.green, Color.CYAN.blue, 192),
                            "growth: $percent%")
                            .setProgress(percent.toFloat() / 100.0f, Color(Color.CYAN.red, Color.CYAN.green, Color.CYAN.blue, 50)))
                }
            }

            return list.toList()
        }
}
