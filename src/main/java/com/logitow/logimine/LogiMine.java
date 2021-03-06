package com.logitow.logimine;

import com.logitow.bridge.communication.LogitowDeviceManager;
import com.logitow.bridge.event.Event;
import com.logitow.bridge.event.EventHandler;
import com.logitow.bridge.event.EventManager;
import com.logitow.bridge.event.device.*;
import com.logitow.bridge.event.device.battery.DeviceBatteryLowChargeEvent;
import com.logitow.bridge.event.device.battery.DeviceBatteryVoltageUpdateEvent;
import com.logitow.bridge.event.device.block.BlockOperationEvent;
import com.logitow.bridge.event.devicemanager.DeviceManagerDiscoveryStartedEvent;
import com.logitow.bridge.event.devicemanager.DeviceManagerDiscoveryStoppedEvent;
import com.logitow.bridge.event.devicemanager.DeviceManagerErrorEvent;
import com.logitow.logimine.blocks.ModBlocks;
import com.logitow.logimine.event.LogitowBridgeEvent;
import com.logitow.logimine.items.ModItems;
import com.logitow.logimine.networking.*;
import com.logitow.logimine.proxy.ServerProxy;
import com.logitow.logimine.tiles.TileEntityBlockKey;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;

/**
 * The core class of the mod.
 * Created by James on 14/12/2017.
 * Modified by itsMatoosh
 */
@Mod(modid = LogiMine.modId, name = LogiMine.name, version = LogiMine.version)
public class LogiMine {
    public static final String modId = "logimine";
    public static final String name = "LogiMine";
    public static final String version = "1.0.0";
    public final static SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(modId);

    public EventHandler logitowBridgeEventHandler = new EventHandler() {
        @Override
        public void onEventCalled(Event event) {
            //Posting every received event as LogitowBridgeEvent.
            MinecraftForge.EVENT_BUS.post(new LogitowBridgeEvent(event));
        }
    };

    /**
     * The currently active key blocks.
     * Server side only.
     * Updated through the assign device message.
     */
    public static ArrayList<TileEntityBlockKey> activeKeyBlocks = new ArrayList<>();

    @Mod.Instance(modId)
    public static LogiMine instance;
    public static final ModTab tab = new ModTab();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println(name + " is loading!");
    }

    /**
     * Called when the mod initializes.
     * @param event
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        //Booting the device manager.
        LogitowDeviceManager.boot();

        //Registering events with the bridge.
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceDiscoveredEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceConnectedEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceConnectionErrorEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, BlockOperationEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceDisconnectedEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceLostEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceBatteryLowChargeEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceBatteryVoltageUpdateEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceManagerDiscoveryStartedEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceManagerDiscoveryStoppedEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceManagerErrorEvent.class);
        MinecraftForge.EVENT_BUS.register(TileEntityBlockKey.class);

        proxy.registerLogitowEvents();

        //Registering packets.
        networkWrapper.registerMessage(LogitowEventMessageHandler.class, LogitowEventMessage.class, 1, Side.SERVER);
        networkWrapper.registerMessage(LogitowDeviceAssignMessageHandler.class, LogitowDeviceAssignMessage.class, 2, Side.SERVER);
        networkWrapper.registerMessage(LogitowSaveStructureMessageHandler.class, LogitowSaveStructureMessage.class, 3, Side.SERVER);
        networkWrapper.registerMessage(LogitowLoadStructureMessageHandler.class, LogitowLoadStructureMessage.class, 4, Side.SERVER);
        networkWrapper.registerMessage(LogitowStructureListMessageHandler.class, LogitowStructureListMessage.class, 5, Side.CLIENT);
        networkWrapper.registerMessage(LogitowStructureListRequestMessageHandler.class, LogitowStructureListRequestMessage.class, 6, Side.SERVER);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
    @SidedProxy(serverSide = "com.logitow.logimine.proxy.ServerProxy", clientSide = "com.logitow.logimine.proxy.ClientProxy")
    public static ServerProxy proxy;

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            ModItems.register(event.getRegistry());
            ModBlocks.registerItemBlocks(event.getRegistry());
        }
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            ModBlocks.register(event.getRegistry());
            GameRegistry.registerTileEntity(TileEntityBlockKey.class, ModBlocks.key_lblock.getRegistryName().toString());
        }
        @SubscribeEvent
        public static void registerItems(ModelRegistryEvent event) {
            ModItems.registerModels();
            ModBlocks.registerModels();
        }
    }
}
