package com.logitow.logimine.networking;

import com.logitow.bridge.event.Event;
import com.logitow.bridge.event.device.DeviceConnectedEvent;
import com.logitow.bridge.event.device.DeviceDisconnectedEvent;
import com.logitow.bridge.event.device.battery.DeviceBatteryVoltageUpdateEvent;
import com.logitow.bridge.event.device.block.BlockOperationErrorEvent;
import com.logitow.bridge.event.device.block.BlockOperationEvent;
import com.logitow.logimine.LogiMine;
import com.logitow.logimine.event.LogitowBridgeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Forwards the logitow events from the client to the server.
 */
@SideOnly(Side.CLIENT)
public class LogitowEventForwarder {
    @SubscribeEvent
    public static void logitowEventReceived(LogitowBridgeEvent event) {
        if(event.deviceEvent instanceof DeviceConnectedEvent ||
                event.deviceEvent instanceof DeviceDisconnectedEvent ||
                event.deviceEvent instanceof BlockOperationEvent ||
                event.deviceEvent instanceof BlockOperationErrorEvent ||
                event.deviceEvent instanceof DeviceBatteryVoltageUpdateEvent) {
            forwardEvent(event.deviceEvent);
        }
    }

    /**
     * Forwards the given event to the server.
     * @param event
     */
    private static void forwardEvent(Event event) {
        LogiMine.networkWrapper.sendToServer(new LogitowEventMessage(event));
    }
}
