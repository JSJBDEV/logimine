package com.logitow.logimine.networking;

import com.logitow.bridge.communication.Device;
import com.logitow.logimine.LogiMine;
import com.logitow.logimine.blocks.ModBlocks;
import com.logitow.logimine.tiles.TileEntityBlockKey;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Handler for the assign device message.
 */
public class LogitowDeviceAssignMessageHandler implements IMessageHandler<LogitowDeviceAssignMessage, IMessage> {

    public static final String TEXT_DEVICE_MANAGER_ASSIGNED = "logitow.devicemanager.assigneddevice";
    public static final String TEXT_DEVICE_MANAGER_UNASSIGNED = "logitow.devicemanager.unassigneddevice";
    public static final ITextComponent TEXT_DEVICE_MANAGER_KEYBLOCK_NOT_INITIALIZED = new TextComponentTranslation("logitow.devicemanager.keyblocknotinitialized");
    public static final ITextComponent TEXT_DEVICE_MANAGER_BLOCK_IS_NOT_KEY = new TextComponentTranslation("logitow.devicemanager.blockisnotkeyblock");

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
                    if (!active.getWorld().isRemote && active.getPos().equals(message.position)) {
                        keyBlock = active;
                    }
                }

                if(keyBlock != null) {
                    if(message.deviceUUID.equals("NULL")) {
                        //Assigning the keyblock.
                        serverPlayer.sendMessage(new TextComponentTranslation(TEXT_DEVICE_MANAGER_UNASSIGNED, keyBlock.getAssignedDevice().info.friendlyName));
                        keyBlock.assignDevice(null, null);
                    } else {
                        //Assigning the keyblock.
                        keyBlock.assignDevice(serverPlayer, Device.getConnectedFromUuid(message.deviceUUID));
                        serverPlayer.sendMessage(new TextComponentTranslation(TEXT_DEVICE_MANAGER_ASSIGNED, keyBlock.getAssignedDevice().info.friendlyName));
                    }
                } else {
                    serverPlayer.sendMessage(TEXT_DEVICE_MANAGER_KEYBLOCK_NOT_INITIALIZED);
                }
            } else {
                serverPlayer.sendMessage(TEXT_DEVICE_MANAGER_BLOCK_IS_NOT_KEY);
            }
        });

        return null;
    }
}
