package net.ndrei.teslapoweredthingies

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLConstructionEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.ndrei.teslacorelib.config.ModConfigHandler
import net.ndrei.teslacorelib.config.TeslaCoreLibConfig
import net.ndrei.teslacorelib.items.gears.CoreGearType
import net.ndrei.teslapoweredthingies.common.CommonProxy
import net.ndrei.teslapoweredthingies.common.TeslaFakePlayer
import net.ndrei.teslapoweredthingies.items.TeslaPlantSeeds
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerBlock
import org.apache.logging.log4j.Logger

/**
 * Created by CF on 2017-06-30.
 */
@Mod(modid = MOD_ID, version = MOD_VERSION, name = MOD_NAME,
        dependencies = "$MOD_DEPENDENCIES;after:basemetals;after:modernmetals;", acceptedMinecraftVersions = MOD_MC_VERSION,
        useMetadata = true, modLanguage = "kotlin", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object TeslaThingiesMod {
    const val MODID = MOD_ID

    @SidedProxy(clientSide = "net.ndrei.teslapoweredthingies.client.ClientProxy", serverSide = "net.ndrei.teslapoweredthingies.common.CommonProxy")
    lateinit var proxy: CommonProxy
    lateinit var logger: Logger

    lateinit var config: ModConfigHandler

    val creativeTab: CreativeTabs = object : CreativeTabs("Tesla Powered Thingies") {
        override fun getIconItemStack() = ItemStack(FluidBurnerBlock)
        override fun getTabIconItem() = this.iconItemStack
    }

    @Mod.EventHandler
    fun construction(event: FMLConstructionEvent) {
        // Use forge universal bucket
        FluidRegistry.enableUniversalBucket()

        arrayOf(
            TeslaCoreLibConfig.REGISTER_GEARS,
            "${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${CoreGearType.WOOD.material}",
            "${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${CoreGearType.STONE.material}",
            "${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${CoreGearType.IRON.material}",
            "${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${CoreGearType.GOLD.material}",
            "${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${CoreGearType.DIAMOND.material}",
            TeslaCoreLibConfig.REGISTER_BATTERY,
            TeslaCoreLibConfig.REGISTER_MACHINE_CASE,
            TeslaCoreLibConfig.REGISTER_ADDONS,
            TeslaCoreLibConfig.REGISTER_SPEED_ADDONS,
            TeslaCoreLibConfig.REGISTER_ENERGY_ADDONS,
            TeslaCoreLibConfig.REGISTER_POWDERS
        ).forEach {
            TeslaCoreLibConfig.setDefaultFlag(it, true)
        }
    }

    @Mod.EventHandler
    fun construct(event: FMLConstructionEvent) {
        this.proxy.construction(event)
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        TeslaThingiesMod.logger = event.modLog
        TeslaThingiesMod.config = ModConfigHandler(MOD_ID, this.javaClass, this.logger, event.modConfigurationDirectory)

        this.proxy.preInit(event)
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        this.proxy.init(e)

        TeslaPlantSeeds.registerSeeds()
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        this.proxy.postInit(e)
    }

    fun getFakePlayer(world: World?): TeslaFakePlayer? {
        return TeslaFakePlayer.getPlayer((world as? WorldServer) ?: return null)
    }
}
