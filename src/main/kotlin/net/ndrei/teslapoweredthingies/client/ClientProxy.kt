package net.ndrei.teslapoweredthingies.client

import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.CommonProxy

/**
 * Created by CF on 2017-06-30.
 */
@Suppress("unused")
class ClientProxy : CommonProxy(Side.CLIENT) {
    override fun preInit(e: FMLPreInitializationEvent) {
        super.preInit(e)
        TeslaThingiesMod.logger.info("ClientProxy::preInit")

        OBJLoader.INSTANCE.addDomain(TeslaThingiesMod.MODID)
    }
}
