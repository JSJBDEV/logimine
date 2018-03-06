package com.logitow.logimine.networking;

import com.logitow.logimine.event.LogitowBridgeEvent;
import com.logitow.logimine.event.LogitowBridgeServerEventHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Handler for the message packet.
 */
public class LogitowEventMessageHandler implements IMessageHandler<LogitowEventMessage, IMessage> {

    public LogitowEventMessageHandler(){}

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
        LogitowBridgeServerEventHandler.onLogitowEvent(new LogitowBridgeEvent(message.event));

        return null;
    }
}
