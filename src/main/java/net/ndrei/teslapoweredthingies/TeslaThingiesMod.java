package net.ndrei.teslapoweredthingies;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.ndrei.teslapoweredthingies.common.BlocksRegistry;
import net.ndrei.teslapoweredthingies.common.CommonProxy;
import org.apache.logging.log4j.Logger;

@Mod(modid = TeslaThingiesMod.MODID, version = TeslaThingiesMod.VERSION, name = "Tesla Core Lib", dependencies = "after:tesla", useMetadata = true)
public class TeslaThingiesMod
{
    public static final String MODID = "teslathingies";
    public static final String VERSION = "@@VERSION@@";

    public static final ResourceLocation MACHINES_TEXTURES = new ResourceLocation(TeslaThingiesMod.MODID, "textures/gui/machines.png");
    public static final ResourceLocation JEI_TEXTURES = new ResourceLocation(TeslaThingiesMod.MODID, "textures/gui/jei.png");

    @Mod.Instance
    @SuppressWarnings("unused")
    public static TeslaThingiesMod instance;

    @SidedProxy(clientSide = "net.ndrei.teslapoweredthingies.client.ClientProxy", serverSide = "net.ndrei.teslapoweredthingies.common.CommonProxy")
    private static CommonProxy proxy;

    public static Logger logger;

    public static CreativeTabs creativeTab =  new CreativeTabs("Tesla Powered Thingies") {
        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(BlocksRegistry.fluidBurner);
        }

        @Override
        public ItemStack getTabIconItem() { return this.getIconItemStack(); }
    };

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event)
    {
        // Use forge universal bucket
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        TeslaThingiesMod.logger = event.getModLog();

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }}
