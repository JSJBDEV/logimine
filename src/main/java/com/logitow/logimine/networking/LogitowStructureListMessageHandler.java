package com.logitow.logimine.networking;

import com.logitow.logimine.client.gui.LoadStructureGui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Handles the server response message with the list of saved structures on the client.
 */
public class LogitowStructureListMessageHandler implements IMessageHandler<LogitowStructureListMessage, IMessage> {

    public LogitowStructureListMessageHandler() {}

    /**
     * Called when the received saved structures list is received.
     * @param message
     * @param ctx
     * @return
     */
    @Override
    public IMessage onMessage(LogitowStructureListMessage message, MessageContext ctx) {
        System.out.println("Received saved structures page: " + message.requestedPage.id);

        //Passing the received page to the gui.
        if(LoadStructureGui.instance != null) {
            LoadStructureGui.instance.onPageLoaded(message.requestedPage);
        }

        return null;
    }
}
