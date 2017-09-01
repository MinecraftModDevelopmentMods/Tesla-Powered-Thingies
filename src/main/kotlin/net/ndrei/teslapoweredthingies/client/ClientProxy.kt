package net.ndrei.teslapoweredthingies.client

import net.minecraft.util.ResourceLocation
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
    val TANK_MODEL = ResourceLocation(TeslaThingiesMod.MODID, "multi_tank")

    override fun preInit(ev: FMLPreInitializationEvent) {
        super.preInit(ev)
        TeslaThingiesMod.logger.info("ClientProxy::preInit")

        OBJLoader.INSTANCE.addDomain(TeslaThingiesMod.MODID)

//        ModelLoaderRegistry.registerLoader(object : ICustomModelLoader {
//            override fun onResourceManagerReload(resourceManager: IResourceManager) {}
//
//            override fun accepts(modelLocation: ResourceLocation): Boolean {
//                if (modelLocation.resourceDomain == TeslaThingiesMod.MODID && modelLocation.resourcePath.contains("multi_tank")) {
//                    return true
//                }
//                return false
//            }
//
//            override fun loadModel(modelLocation: ResourceLocation): IModel {
//                if (modelLocation is ModelResourceLocation) {
//                    return MultiTankModel()
//                }
//                return MultiTankInventoryModel(MultiTankModel())
//            }
//        })
    }

//    override fun postInit(ev: FMLPostInitializationEvent) {
//        super.postInit(ev)
//
////        ClientRegistry.registerTileEntity(FakeMultiTankEntity::class.java, "teslathingies:fake_tile", MultiTankEntityRenderer)
////        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(MultiTankBlock), 0, FakeMultiTankEntity::class.java)
//    }

//    @SubscribeEvent
//    fun stitchEvent(ev: TextureStitchEvent) {
//        if (ev.map == Minecraft.getMinecraft().textureMapBlocks) {
//            ev.map.registerSprite(Textures.MULTI_TANK_SIDE.resource)
//        }
//    }
}
