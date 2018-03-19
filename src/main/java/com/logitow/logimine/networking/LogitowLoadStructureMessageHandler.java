package com.logitow.logimine.networking;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Handles the load structure message on the server.
 */
public class LogitowLoadStructureMessageHandler implements IMessageHandler<LogitowLoadStructureMessage, IMessage> {
    @Override
    public IMessage onMessage(LogitowLoadStructureMessage message, MessageContext ctx) {
        return null;
    }
}
