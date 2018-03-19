package com.logitow.logimine.networking;

import com.logitow.logimine.LogiMine;
import com.logitow.logimine.tiles.TileEntityBlockKey;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

/**
 * Handles the save structure messages.
 */
public class LogitowSaveStructureMessageHandler implements IMessageHandler<LogitowSaveStructureMessage, IMessage> {

    final ITextComponent TEXT_KEYBLOCK_DOESNT_EXIST = new TextComponentTranslation("logitow.savestructuremanager.keyblockdoesntexist");
    final ITextComponent TEXT_NO_ATTACHED_STRUCTURE = new TextComponentTranslation("logitow.savestructuremanager.nostructureattached");
    final ITextComponent TEXT_SAVING_ERROR = new TextComponentTranslation("logitow.savestructuremanager.savingerror");
    final String TEXT_SAVING_SUCCESFUL_KEY = "logitow.savestructuremanager.savingsuccessful";

    public LogitowSaveStructureMessageHandler(){}

    @Override
    public IMessage onMessage(LogitowSaveStructureMessage message, MessageContext ctx) {
        //Null checks.
        if(message.name == null || message.name == "") {
            return null;
        }
        if(message.keyBlock == null) {
            return null;
        }

        //Getting the sending player.
        EntityPlayerMP serverPlayer = ctx.getServerHandler().player;

        //Execute the action on the main server thread by adding it as a scheduled task
        serverPlayer.getServerWorld().addScheduledTask(() -> {
            //Getting the keyblock entity.
            TileEntityBlockKey usedKeyBlock = null;
            for (TileEntityBlockKey keyBlock :
                    LogiMine.activeKeyBlocks) {
                if(keyBlock.getWorld() != null && !keyBlock.getWorld().isRemote && keyBlock.getPos().equals(message.keyBlock)) {
                    usedKeyBlock = keyBlock;
                    break;
                }
            }

            //checking
            if(usedKeyBlock == null) {
                serverPlayer.sendMessage(TEXT_KEYBLOCK_DOESNT_EXIST);
                return;
            }
            if(usedKeyBlock.getAssignedStructure() == null) {
                serverPlayer.sendMessage(TEXT_NO_ATTACHED_STRUCTURE);
                return;
            }

            //adding player uuid to the name.
            String strucName = message.name;
            strucName = strucName.replace('^','v');
            strucName = strucName + "^" + serverPlayer.getUniqueID().toString();

            //setting custom name
            usedKeyBlock.getAssignedStructure().customName = strucName;

            //saving structure
            try {
                usedKeyBlock.getAssignedStructure().saveToFile();
            } catch (IOException e) {
                serverPlayer.sendMessage(TEXT_SAVING_ERROR);
                return;
            }

            //saved succesfully
            serverPlayer.sendMessage(new TextComponentTranslation(TEXT_SAVING_SUCCESFUL_KEY, message.name));
        });

        //No reply
        return null;
    }
}
