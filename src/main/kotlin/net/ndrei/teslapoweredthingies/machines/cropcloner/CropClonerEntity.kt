package net.ndrei.teslapoweredthingies.machines.cropcloner

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.EnumPlantType
import net.minecraftforge.common.IPlantable
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.render.HudInfoLine
import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine
import net.ndrei.teslapoweredthingies.render.CropClonerSpecialRenderer
import java.awt.Color

/**
 * Created by CF on 2017-07-07.
 */
class CropClonerEntity : ElectricFarmMachine(CropClonerEntity::class.java.name.hashCode()) {
    var plantedThing: IBlockState? = null
        private set
    private lateinit var waterTank: IFluidTank

    //#region inventory methods

    override fun supportsRangeAddons() = false

    override fun supportsAddons() = false

    override val hasWorkArea: Boolean
        get() = false

    override fun initializeInventories() {
        super.initializeInventories()

        this.waterTank = super.addFluidTank(FluidRegistry.WATER, 5000, EnumDyeColor.BLUE, "Water Tank",
                BoundingRectangle(43, 25, 18, 54))
    }

    override fun initializeInputInventory() {
        this.inStackHandler = object : ItemStackHandler(1) {
            override fun onContentsChanged(slot: Int) {
                this@CropClonerEntity.markDirty()
            }

            override fun getStackLimit(slot: Int, stack: ItemStack) = 1
        }
        this.filteredInStackHandler = object : ColoredItemHandler(this.inStackHandler!!, EnumDyeColor.GREEN, "Input Items", BoundingRectangle(115 + 18, 25, 18, 18)) {
            override fun canInsertItem(slot: Int, stack: ItemStack)
                    = super.canInsertItem (slot, stack) && this@CropClonerEntity.acceptsInputStack(slot, stack)

            override fun canExtractItem(slot: Int) = false
        }
        super.addInventory(this.filteredInStackHandler)
        super.addInventoryToStorage(this.inStackHandler!!, "inputs")
    }

    override val lockableInputInventory: Boolean
        get() = false

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

    //#endregion

    //#region gui       methods

    override val hudLines: List<HudInfoLine>
        get() {
            val list = super.hudLines.toMutableList()

            if (this.plantedThing == null) {
                list.add(HudInfoLine(Color(255, 159, 51),
                        Color(255, 159, 51, 42),
                        "no seed")
                        .setTextAlignment(HudInfoLine.TextAlignment.CENTER))
            } else {
                list.add(HudInfoLine(Color.WHITE, this.plantedThing!!.block.localizedName)
                        .setTextAlignment(HudInfoLine.TextAlignment.CENTER))
                val age = CropClonerPlantFactory.getPlant(this.plantedThing!!).getAgeProperty(this.plantedThing!!)
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

    override fun getRenderers(): MutableList<TileEntitySpecialRenderer<in TileEntity>> {
        val list = super.getRenderers()
        list.add(CropClonerSpecialRenderer)
        return list
    }

    //#endregion

    //#region storage   methods

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
            val ageProperty = CropClonerPlantFactory.getPlant(this.plantedThing!!).getAgeProperty(this.plantedThing!!)
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
            val ageProperty = CropClonerPlantFactory.getPlant(this.plantedThing!!).getAgeProperty(this.plantedThing!!)
            if (ageProperty != null) {
                compound.setInteger("plantAge", this.plantedThing!!.getValue(ageProperty))
            }
        }

        return compound
    }

    //#endregion

    override fun performWork(): Float {
        var result = 0.0f

        val planted = this.plantedThing
        if (planted != null) {
            val wrapper = CropClonerPlantFactory.getPlant(planted)
            val ageProperty = wrapper.getAgeProperty(planted)
            if (ageProperty != null) {
                val age = planted.getValue(ageProperty)
                val ages = ageProperty.allowedValues.toTypedArray()
                if (age == ages[ages.size - 1]) {
                    val stacks = wrapper.getDrops(this.getWorld(), this.getPos(), planted)
                    if (super.outputItems(stacks)) {
                        this.plantedThing = null
                        result += .85f
                    }
                } else {
                    this.plantedThing = wrapper.grow(planted, ageProperty, this.getWorld().rand)
                    result += .75f
                }
                this.onPlantedThingChanged()
            }
        }

        if (this.plantedThing == null && this.waterTank.fluidAmount >= 250) {
            val stack = this.inStackHandler!!.getStackInSlot(0)
            if (!stack.isEmpty && (stack.item is IPlantable)) {
                val plantable = stack.item as IPlantable
                if (plantable.getPlantType(this.getWorld(), this.getPos()) == EnumPlantType.Crop) {
                    this.plantedThing = plantable.getPlant(this.getWorld(), this.getPos())
                    this.waterTank.drain(250, true) // TODO: <-- do this better
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
}
