package com.logitow.logimine.Event;

import com.logitow.bridge.communication.LogitowDeviceManager;
import com.logitow.bridge.event.Event;
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
import com.logitow.logimine.LogiMine;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handles LOGITOW bridge events.
 */
public class LogitowBridgeEventHandler {
    @SubscribeEvent
    public static void logitowEventReceived(LogitowBridgeEvent event) {
        //Listening for different event.
        Event bridgeEvent = event.deviceEvent;

        if(bridgeEvent instanceof DeviceConnectedEvent) {
            //Called when a logitow device is connected.
            //TODO: Show device connected notification.
            DeviceConnectedEvent deviceConnectedEvent = (DeviceConnectedEvent) bridgeEvent;

            //Adding the connected device to the unassigned devices list.
            LogiMine.unassignedDevices.add(deviceConnectedEvent.device);
        } else if(bridgeEvent instanceof DeviceDisconnectedEvent) {
            //Called when a logitow device is disconnected
            //TODO: Show device disconnected notification.
            DeviceDisconnectedEvent deviceDisconnectedEvent = (DeviceDisconnectedEvent) bridgeEvent;

            //Removing the disconnected device from the unassigned devices list if it is on it.
            if(LogiMine.unassignedDevices.contains(deviceDisconnectedEvent.device)) {
                LogiMine.unassignedDevices.remove(deviceDisconnectedEvent.device);
            }
        } else if(bridgeEvent instanceof DeviceDiscoveredEvent) {
            //Called when a logitow device is discovered.
            //TODO: Maybe give the player a list of discovered devices to connect to.
            DeviceDiscoveredEvent deviceDiscoveredEvent = (DeviceDiscoveredEvent) bridgeEvent;

            //Automatically connecting to the newly discovered devices.
            LogitowDeviceManager.current.connectDevice(deviceDiscoveredEvent.device);
        } else if (bridgeEvent instanceof DeviceLostEvent) {
            //Called when a logitow device is no longer available to connect. (out of range)
            //TODO: Update the list of discovered devices. (remove the lost device)
            DeviceLostEvent deviceLostEvent = (DeviceLostEvent) bridgeEvent;
        } else if(bridgeEvent instanceof DeviceManagerDiscoveryStartedEvent) {
            //Called when device discovery starts.
            //TODO: Maybe give the player a list of discovered devices to connect to.
            DeviceManagerDiscoveryStartedEvent deviceManagerDiscoveryStartedEvent = (DeviceManagerDiscoveryStartedEvent) bridgeEvent;
        } else if(bridgeEvent instanceof DeviceManagerDiscoveryStoppedEvent) {
            //Called when device discovery is stopped.
            DeviceManagerDiscoveryStoppedEvent deviceManagerDiscoveryStoppedEvent = (DeviceManagerDiscoveryStoppedEvent) bridgeEvent;
        } else if(bridgeEvent instanceof DeviceManagerErrorEvent) {
            //Called when there's an error with the device manager.
            //TODO: Handle the error. -> Restart the bridge.
            DeviceManagerErrorEvent deviceManagerErrorEvent = (DeviceManagerErrorEvent) bridgeEvent;
        } else if (bridgeEvent instanceof DeviceBatteryVoltageUpdateEvent) {
            //Called when the device battery voltage is updated.
            //TODO: Show battery state on screen.
            DeviceBatteryVoltageUpdateEvent deviceBatteryVoltageUpdateEvent = (DeviceBatteryVoltageUpdateEvent) bridgeEvent;
        } else if (bridgeEvent instanceof DeviceBatteryLowChargeEvent) {
            //Called when the device battery is below 10% charge.
            //TODO: Show battery low alert.
            DeviceBatteryLowChargeEvent deviceBatteryLowChargeEvent = (DeviceBatteryLowChargeEvent) bridgeEvent;
        } else if (bridgeEvent instanceof BlockOperationEvent) {
            //Called when a block state update is received from the device.
            //TODO: Move the block updating logic here.
            BlockOperationEvent blockOperationEvent = (BlockOperationEvent) bridgeEvent;
        }
    }
}
