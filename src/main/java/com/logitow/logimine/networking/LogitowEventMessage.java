package com.logitow.logimine.networking;

import com.logitow.bridge.event.Event;
import com.logitow.bridge.event.EventManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
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
        buf.writeBytes(SerializationUtils.serialize(this.event));
    }

    /**
     * Handler for the message packet.
     */
    public class LogitowEventMessageHandler implements IMessageHandler<LogitowEventMessage, IMessage> {

        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(LogitowEventMessage message, MessageContext ctx) {
            //Pushing the received event through the event system.
            EventManager.callEvent(message.event);

            return null;
        }
    }
}
