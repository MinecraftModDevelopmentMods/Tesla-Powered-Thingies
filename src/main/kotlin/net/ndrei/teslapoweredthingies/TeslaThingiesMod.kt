package net.ndrei.teslapoweredthingies

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLConstructionEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.ndrei.teslapoweredthingies.common.CommonProxy
import net.ndrei.teslapoweredthingies.machines.FluidBurnerBlock
import org.apache.logging.log4j.Logger

/**
 * Created by CF on 2017-06-30.
 */
@Mod(modid = TeslaThingiesMod.MODID, /*version = TeslaThingiesMod.VERSION, name = "Tesla Power Thingies", dependencies = "after:tesla,teslacorelib",*/ useMetadata = true,
        modLanguage = "kotlin", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
class TeslaThingiesMod {
    @Mod.EventHandler
    fun construction(event: FMLConstructionEvent) {
        // Use forge universal bucket
        FluidRegistry.enableUniversalBucket()
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        TeslaThingiesMod.logger = event.modLog

        proxy.preInit(event)
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        proxy.init(e)
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        proxy.postInit(e)
    }

    companion object {
        const val MODID = "teslathingies"
        const val VERSION = "@VERSION@"

        val MACHINES_TEXTURES = ResourceLocation(MODID, "textures/gui/machines.png")
        val JEI_TEXTURES = ResourceLocation(MODID, "textures/gui/jei.png")

        @Mod.Instance
        lateinit var instance: TeslaThingiesMod

        @SidedProxy(clientSide = "net.ndrei.teslapoweredthingies.client.ClientProxy", serverSide = "net.ndrei.teslapoweredthingies.common.CommonProxy")
        lateinit var proxy: CommonProxy

        lateinit var logger: Logger

        var creativeTab: CreativeTabs = object : CreativeTabs("Tesla Powered Thingies") {
            override fun getIconItemStack(): ItemStack {
                return ItemStack(FluidBurnerBlock)
            }

            override fun getTabIconItem(): ItemStack {
                return this.iconItemStack
            }
        }
    }
}
