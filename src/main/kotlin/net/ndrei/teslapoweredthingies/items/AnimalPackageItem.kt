package net.ndrei.teslapoweredthingies.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.localization.localizeModString
import net.ndrei.teslacorelib.localization.makeTextComponent
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.integrations.GUI_ANIMAL_PACKAGE
import net.ndrei.teslapoweredthingies.integrations.localize

/**
 * Created by CF on 2017-07-07.
 */
@AutoRegisterItem
object AnimalPackageItem : BaseThingyItem("animal_package") {
    init {
        this.addPropertyOverride(ResourceLocation("hasAnimal"), { stack, _, _ ->
            val nbt = if (stack.isEmpty) null else stack.tagCompound
            if (nbt != null && nbt.getInteger("hasAnimal") == 1)
                1f
            else
                0f
        })
    }

    override fun getTranslationKey(stack: ItemStack): String {
        if (this.hasAnimal(stack)) {
            return this.translationKey + "_full"
        }
        return this.translationKey
    }

    override fun addInformation(stack: ItemStack?, worldIn: World?, tooltip: MutableList<String>?, flagIn: ITooltipFlag?) {
        super.addInformation(stack, worldIn, tooltip, flagIn)

        if ((tooltip != null) && (stack != null)) {
            val nbt = if (stack.isEmpty) null else stack.tagCompound
            if ((nbt != null) && nbt.getInteger("hasAnimal") == 1) {
                tooltip.add(localize(GUI_ANIMAL_PACKAGE, "Contains Animal") {
                    +TextFormatting.AQUA
                })
                val entityName = nbt.getString("animalName").let {
                    if (it.isNullOrBlank()) {
                        nbt.getString("animalClass").let { it.substring(it.lastIndexOf(".") + 1) }
                    } else it!!
                }
                tooltip.add(localizeModString(entityName) {
                    +TextFormatting.DARK_AQUA
                }.formattedText)
                if (nbt.hasKey("animalHealth", Constants.NBT.TAG_FLOAT)) {
                    tooltip.add(localize(GUI_ANIMAL_PACKAGE, "health") {
                        +TextFormatting.BLUE
                        +String.format("%.2f", nbt.getFloat("animalHealth")).makeTextComponent()
                    })
                }
            } else {
                tooltip.add(localize(GUI_ANIMAL_PACKAGE, "no_animal") {
                    +TextFormatting.DARK_GRAY
                })
            }
        }
    }

    fun hasAnimal(stack: ItemStack): Boolean {
        val nbt = if (stack.isEmpty) null else stack.tagCompound
        return nbt != null && nbt.getInteger("hasAnimal") == 1
    }

    fun unpackage(world: World?, stack: ItemStack): EntityAnimal? {
        if (stack.isEmpty || stack.item !== AnimalPackageItem
                || !hasAnimal(stack)) {
            return null
        }

        val compound = stack.tagCompound ?: return null

        val animal = compound.getCompoundTag("animal")
        val animalClass = compound.getString("animalClass")
        try {
            val cea = Class.forName(animalClass)
            val thing = cea.getConstructor(World::class.java).newInstance(world)
            if (thing is EntityAnimal) {
                val ea = thing
                if (animal.hasKey("Attributes", 9) && world != null && world.isRemote) {
                    // this is the reverse of what EntityLiving does... so that attributes work on client side
                    SharedMonsterAttributes.setAttributeModifiers(ea.attributeMap, animal.getTagList("Attributes", 10))
                }
                ea.readEntityFromNBT(animal)

                return ea
            }
        } catch (e: Throwable) {
            TeslaThingiesMod.logger.warn("Error creating animal '$animalClass'.", e)
        }

        return null
    }

    override fun getItemStackLimit(stack: ItemStack): Int {
        return if (this.hasAnimal(stack)) 1 else 16
    }
}
