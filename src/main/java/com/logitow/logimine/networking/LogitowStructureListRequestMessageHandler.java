package com.logitow.logimine.networking;

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Handles structure list request messages.
 */
public class LogitowStructureListRequestMessageHandler implements IMessageHandler<LogitowStructureListRequestMessage, LogitowStructureListMessage> {
    @Override
    public LogitowStructureListMessage onMessage(LogitowStructureListRequestMessage message, MessageContext ctx) {
        return null; //TODO: Respond with the requested list.
    }
}
