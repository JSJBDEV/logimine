package com.logitow.logimine.networking;

import com.logitow.bridge.build.Structure;
import com.logitow.logimine.LogiMine;
import com.logitow.logimine.blocks.ModBlocks;
import com.logitow.logimine.tiles.TileEntityBlockKey;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

/**
 * Handles the load structure message on the server.
 */
public class LogitowLoadStructureMessageHandler implements IMessageHandler<LogitowLoadStructureMessage, IMessage> {

    public final String TEXT_STRUCTURE_LOADED_KEY = "logitow.loadstructuremanager.structureloaded";
    public final ITextComponent TEXT_STRUCTURE_COULDNT_LOAD = new TextComponentTranslation("logitow.loadstructuremanager.couldntload");

    @Override
    public IMessage onMessage(LogitowLoadStructureMessage message, MessageContext ctx) {
        //Checking vars.
        if(message.keyBlock == null || message.structureName == null || message.structureName == "") {
            return null;
        }

        //Getting player.
        EntityPlayerMP entityPlayer = ctx.getServerHandler().player;

        //Checking if there is a key block on position.
        if(entityPlayer.getServerWorld().getBlockState(message.keyBlock) != ModBlocks.key_lblock.getDefaultState()) {
            entityPlayer.sendMessage(LogitowDeviceAssignMessageHandler.TEXT_DEVICE_MANAGER_BLOCK_IS_NOT_KEY);
            return null;
        }

        //Getting the structure.
        Structure loadedStructure;
        try {
            loadedStructure = Structure.loadByName(message.structureName);
        } catch (IOException e) {
            entityPlayer.sendMessage(TEXT_STRUCTURE_COULDNT_LOAD);
            return null;
        }
        if(loadedStructure == null) {
            entityPlayer.sendMessage(TEXT_STRUCTURE_COULDNT_LOAD);
        }

        //Assigning the key block.
        TileEntityBlockKey keyBlock = null;
        for (TileEntityBlockKey active :
                LogiMine.activeKeyBlocks) {
            if (!active.getWorld().isRemote && active.getPos().equals(message.keyBlock)) {
                keyBlock = active;
            }
        }

        //Assigning the block.
        if(keyBlock != null) {
            //Assigning the keyblock.
            keyBlock.assignStructure(loadedStructure);
            entityPlayer.sendMessage(new TextComponentTranslation(TEXT_STRUCTURE_LOADED_KEY, loadedStructure.getNameFormatted()));
        } else {
            entityPlayer.sendMessage(LogitowDeviceAssignMessageHandler.TEXT_DEVICE_MANAGER_KEYBLOCK_NOT_INITIALIZED);
        }

        return null;
    }
}
