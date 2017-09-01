package net.ndrei.teslapoweredthingies.items

import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

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

//    override val recipe: IRecipe
//        get() = ShapedOreRecipe(null, ItemStack(this, 1),
//                "xyx",
//                "yzy",
//                "xyx",
//                'x', "plankWood",
//                'y', Blocks.IRON_BARS,
//                'z', "dustRedstone"
//        )

    override fun getUnlocalizedName(stack: ItemStack): String {
        if (this.hasAnimal(stack)) {
            return this.unlocalizedName + "_full"
        }
        return this.unlocalizedName
    }

    override fun addInformation(stack: ItemStack?, worldIn: World?, tooltip: MutableList<String>?, flagIn: ITooltipFlag?) {
        super.addInformation(stack, worldIn, tooltip, flagIn)

        if ((tooltip != null) && (stack != null)) {
            val nbt = if (stack.isEmpty) null else stack.tagCompound
            if ((nbt != null) && nbt.getInteger("hasAnimal") == 1) {
                tooltip.add(ChatFormatting.AQUA.toString() + "Contains Animal")
                val className = nbt.getString("animalClass")
                tooltip.add(ChatFormatting.DARK_AQUA.toString() + className.substring(className.lastIndexOf(".") + 1))
                if (nbt.hasKey("animalHealth", Constants.NBT.TAG_FLOAT)) {
                    tooltip.add(ChatFormatting.BLUE.toString() + "Health: " + String.format("%.2f", nbt.getFloat("animalHealth")))
                }
            } else {
                tooltip.add(ChatFormatting.DARK_GRAY.toString() + "No Animal")
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
