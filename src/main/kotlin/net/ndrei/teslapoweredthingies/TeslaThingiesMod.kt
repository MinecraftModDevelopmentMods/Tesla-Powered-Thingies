package net.ndrei.teslapoweredthingies

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLConstructionEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.ndrei.teslacorelib.config.ModConfigHandler
import net.ndrei.teslapoweredthingies.common.CommonProxy
import net.ndrei.teslapoweredthingies.items.TeslaPlantSeeds
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerBlock
import org.apache.logging.log4j.Logger

/**
 * Created by CF on 2017-06-30.
 */
@Mod(modid = MOD_ID, version = MOD_VERSION, name = MOD_NAME,
        dependencies = MOD_DEPENDENCIES, acceptedMinecraftVersions = MOD_MC_VERSION,
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
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        TeslaThingiesMod.logger = event.modLog
        TeslaThingiesMod.config = ModConfigHandler(MOD_ID, this.javaClass, this.logger, event.modConfigurationDirectory)

        proxy.preInit(event)
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        proxy.init(e)

        TeslaPlantSeeds.registerSeeds()
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        proxy.postInit(e)
    }

    private val fakePlayers = mutableMapOf<String, FakePlayer>()

    fun getFakePlayer(world: World?): FakePlayer? {
        val key = if (world != null && world.provider != null)
            String.format("%d", world.provider.dimension)
        else
            null
        if (key != null) {
            if (fakePlayers.containsKey(key)) {
                return fakePlayers[key]
            }

            if (world is WorldServer) {
                val player = FakePlayerFactory.getMinecraft(world) // FakePlayer(world, )
                fakePlayers[key] = player
                return player
            }
        }
        return null
    }
}
