package com.logitow.logimine.networking;

import com.logitow.bridge.communication.Device;
import com.logitow.logimine.LogiMine;
import com.logitow.logimine.blocks.ModBlocks;
import com.logitow.logimine.tiles.TileEntityBlockKey;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Handler for the assign device message.
 */
public class LogitowDeviceAssignMessageHandler implements IMessageHandler<LogitowDeviceAssignMessage, IMessage> {

    public LogitowDeviceAssignMessageHandler(){}

    /**
     * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
     * is needed.
     *
     * @param message The message
     * @param ctx
     * @return an optional return message
     */
    @Override
    public IMessage onMessage(LogitowDeviceAssignMessage message, MessageContext ctx) {
        EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
        //Execute the action on the main server thread by adding it as a scheduled task
        serverPlayer.getServerWorld().addScheduledTask(() -> {
            //Checking if the block is a key block.
            if(serverPlayer.getServerWorld().getBlockState(message.position) == ModBlocks.key_lblock.getDefaultState()) {
                //Assigning the key block.
                TileEntityBlockKey keyBlock = null;
                for (TileEntityBlockKey active :
                        LogiMine.activeKeyBlocks) {
                    if (active.getPos().equals(message.position)) {
                        keyBlock = active;
                    }
                }

                if(keyBlock != null) {
                    if(message.deviceUUID == "NULL") {
                        //Assigning the keyblock.
                        keyBlock.assignDevice(null, null);
                        serverPlayer.sendMessage(new TextComponentString("Device unassigned from the key block!"));
                    } else {
                        //Assigning the keyblock.
                        keyBlock.assignDevice(serverPlayer, Device.getConnectedFromUuid(message.deviceUUID));
                        serverPlayer.sendMessage(new TextComponentString("Device assigned to the key block!"));
                    }
                } else {
                    serverPlayer.sendMessage(new TextComponentString("The key block hasn't been initialized yet!"));
                }
            } else {
                serverPlayer.sendMessage(new TextComponentString("This is not a LOGITOW key block!"));
            }
        });

        return null;
    }
}
