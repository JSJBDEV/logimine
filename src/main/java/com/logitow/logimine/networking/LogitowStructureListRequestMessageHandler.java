package com.logitow.logimine.networking;

import com.logitow.bridge.build.Structure;
import com.logitow.logimine.client.gui.StructuresPage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.File;

/**
 * Handles structure list request messages.
 */
public class LogitowStructureListRequestMessageHandler implements IMessageHandler<LogitowStructureListRequestMessage, LogitowStructureListMessage> {
    @Override
    public LogitowStructureListMessage onMessage(LogitowStructureListRequestMessage message, MessageContext ctx) {
        int startIndex = message.requestedPage*6;
        int endIndex = startIndex + 5;

        //Getting the 6 structures.
        StructuresPage requestedPage = new StructuresPage();
        requestedPage.id = message.requestedPage;
        int i = 0;
        for (File file :
                Structure.getStructureSaveDir().listFiles()) {
            String fileName = file.getName().split("\\.")[0];
            if(!fileName.contains("^")) continue;
            if(i >= startIndex && i <= endIndex) {
                requestedPage.structures.add(fileName);
            }
            i++;
        }
        if(requestedPage.structures.size() <= 0) {
            return null;
        }

        return new LogitowStructureListMessage(requestedPage); //Responding with the requested list.
    }
}
