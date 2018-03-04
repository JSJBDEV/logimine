package com.logitow.logimine.networking;

import com.logitow.bridge.event.Event;
import com.logitow.bridge.event.device.DeviceConnectedEvent;
import com.logitow.bridge.event.device.DeviceDisconnectedEvent;
import com.logitow.bridge.event.device.battery.DeviceBatteryVoltageUpdateEvent;
import com.logitow.bridge.event.device.block.BlockOperationEvent;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.apache.commons.lang3.SerializationUtils;

/**
 * Packet with structure update info.
 * Sent from client to server.
 */
public class LogitowEventMessage implements IMessage {

    /**
     * Event transmitted.
     */
    public Event event;

    public LogitowEventMessage(){}

    /**
     * Creates the update packet given the block operation event.
     * @param event
     */
    public LogitowEventMessage(Event event) {
        this.event = event;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        this.event = SerializationUtils.deserialize(buf.array());
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        if(this.event instanceof DeviceConnectedEvent) {
            DeviceConnectedEvent connectedEvent = (DeviceConnectedEvent)event;
            buf.writeInt(0);
            //connectedEvent.device.info.

        } else if(this.event instanceof DeviceDisconnectedEvent) {
            buf.writeInt(1);
        } else if(this.event instanceof BlockOperationEvent) {
            buf.writeInt(2);
        } else if(this.event instanceof DeviceBatteryVoltageUpdateEvent) {
            buf.writeInt(3);
        }
    }
}
