package com.logitow.logimine;

import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;
import com.logitow.bridge.event.Event;
import com.logitow.bridge.event.EventHandler;
import com.logitow.bridge.event.EventManager;
import com.logitow.bridge.event.device.DeviceConnectedEvent;
import com.logitow.bridge.event.device.DeviceDisconnectedEvent;
import com.logitow.bridge.event.device.DeviceDiscoveredEvent;
import com.logitow.bridge.event.device.DeviceLostEvent;
import com.logitow.bridge.event.device.battery.DeviceBatteryLowChargeEvent;
import com.logitow.bridge.event.device.battery.DeviceBatteryVoltageUpdateEvent;
import com.logitow.bridge.event.device.block.BlockOperationEvent;
import com.logitow.bridge.event.devicemanager.DeviceManagerDiscoveryStartedEvent;
import com.logitow.bridge.event.devicemanager.DeviceManagerDiscoveryStoppedEvent;
import com.logitow.bridge.event.devicemanager.DeviceManagerErrorEvent;
import com.logitow.logimine.Blocks.ModBlocks;
import com.logitow.logimine.Event.LogitowBridgeEvent;
import com.logitow.logimine.Items.ModItems;
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

import java.util.ArrayList;

/**
 * The core class of the mod.
 * Created by James on 14/12/2017.
 */
@Mod(modid = LogiMine.modId, name = LogiMine.name, version = LogiMine.version)
public class LogiMine {
    public static final String modId = "logimine";
    public static final String name = "LogiMine";
    public static final String version = "1.0.0";

    public EventHandler logitowBridgeEventHandler = new EventHandler() {
        @Override
        public void onEventCalled(Event event) {
            //Posting every received event as LogitowBridgeEvent.
            MinecraftForge.EVENT_BUS.post(new LogitowBridgeEvent(event));
        }
    };

    /**
     * List of devices with their base blocks unassigned.
     */
    public static ArrayList<Device> unassignedDevices = new ArrayList<>();

    @Mod.Instance(modId)
    public static LogiMine instance;
    public static final ModTab tab = new ModTab();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println(name + " is loading!");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        //Booting the device manager.
        LogitowDeviceManager.boot();

        //Registering events with the bridge.
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceDiscoveredEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceConnectedEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, BlockOperationEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceDisconnectedEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceLostEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceBatteryLowChargeEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceBatteryVoltageUpdateEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceManagerDiscoveryStartedEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceManagerDiscoveryStoppedEvent.class);
        EventManager.registerHandler(logitowBridgeEventHandler, DeviceManagerErrorEvent.class);

        //Registering the mod side bridge event.
        MinecraftForge.EVENT_BUS.register(MyStaticForgeEventHandler.class);

        //Starting device search.
        //TODO: Call this when the world loads.
        LogitowDeviceManager.current.startDeviceDiscovery();
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
