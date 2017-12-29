package com.logitow.logimine;

import com.logitow.logimine.Blocks.ModBlocks;
import com.logitow.logimine.Items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by James on 14/12/2017.
 */
@Mod(modid = LogiMine.modId, name = LogiMine.name, version = LogiMine.version)
public class LogiMine {
    public static final String modId = "logimine";
    public static final String name = "LogiMine";
    public static final String version = "1.0.0";

    @Mod.Instance(modId)
    public static LogiMine instance;
    public static final ModTab tab = new ModTab();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println(name + " is loading!");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
    @SidedProxy(serverSide = "com.logitow.logimine.Proxy.CommonProxy", clientSide = "com.logitow.logimine.Proxy.ClientProxy")
    public static com.logitow.logimine.Proxy.CommonProxy proxy;

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            ModItems.register(event.getRegistry());
            ModBlocks.registerItemBlocks(event.getRegistry());
        }
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) { ModBlocks.register(event.getRegistry());
        }
        @SubscribeEvent
        public static void registerItems(ModelRegistryEvent event) {
            ModItems.registerModels();
            ModBlocks.registerModels();
        }




    }
}
