package com.logitow.logimine.event;

import com.logitow.bridge.event.Event;
import com.logitow.bridge.event.device.DeviceDisconnectedEvent;
import com.logitow.bridge.event.device.block.BlockOperationErrorEvent;
import com.logitow.bridge.event.device.block.BlockOperationEvent;
import com.logitow.logimine.LogiMine;
import com.logitow.logimine.tiles.TileEntityBlockKey;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handles LOGITOW bridge events server side.
 */
public class LogitowBridgeServerEventHandler {
    @SubscribeEvent
    public static void onLogitowEvent(LogitowBridgeEvent event) {
        Event bridgeEvent = event.deviceEvent;

        if (bridgeEvent instanceof BlockOperationEvent) {
            //Called when a block state update is received from the device.
            BlockOperationEvent blockOperationEvent = (BlockOperationEvent) bridgeEvent;

            System.out.println("Handling the block operation on the server received from device: " + ((BlockOperationEvent) bridgeEvent).device.info.uuid + ". Block local pos: " + blockOperationEvent.operation.blockB.coordinate);

            //Passing the event to the respective assigned key block.
            for (TileEntityBlockKey keyBlock :
                    LogiMine.activeKeyBlocks) {
                if(keyBlock.getAssignedDevice() != null) {
                    System.out.println("Key block device: " + keyBlock.getAssignedDevice().info.uuid + " client: " + keyBlock.getWorld().isRemote);
                    if (blockOperationEvent.device.equals(keyBlock.getAssignedDevice()) && !keyBlock.getWorld().isRemote) {
                        keyBlock.onStructureUpdate(blockOperationEvent);
                        break;
                    }
                }
            }
        } else if(bridgeEvent instanceof BlockOperationErrorEvent) {
            BlockOperationErrorEvent blockOperationErrorEvent = (BlockOperationErrorEvent)bridgeEvent;

            System.out.println("Handling the block structure update in the mod.");

            //Passing the event to the respective assigned key block.
            for (TileEntityBlockKey keyBlock :
                    LogiMine.activeKeyBlocks) {
                if (blockOperationErrorEvent.device == keyBlock.getAssignedDevice()) {
                    keyBlock.clearStructure();
                    keyBlock.rebuildStructure();
                    break;
                }
            }
        } else if(bridgeEvent instanceof DeviceDisconnectedEvent) {
            //Called when a logitow device is disconnected
            DeviceDisconnectedEvent deviceDisconnectedEvent = (DeviceDisconnectedEvent) bridgeEvent;

            //Make sure all keyblocks are unassigned.
            for (TileEntityBlockKey keyBlock :
                    LogiMine.activeKeyBlocks) {
                if (keyBlock.getAssignedDevice() == deviceDisconnectedEvent.device) {
                    keyBlock.assignDevice(null, null);
                }
            }
        }
    }
}
