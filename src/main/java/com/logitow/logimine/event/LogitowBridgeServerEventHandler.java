package com.logitow.logimine.event;

import com.logitow.bridge.event.Event;
import com.logitow.bridge.event.device.block.BlockOperationErrorEvent;
import com.logitow.bridge.event.device.block.BlockOperationEvent;
import com.logitow.logimine.LogiMine;
import com.logitow.logimine.blocks.BlockKey;
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

            System.out.println("Handling the block operation in the mod. Block local pos: " + blockOperationEvent.operation.blockB.coordinate);

            //Passing the event to the respective assigned key block.
            BlockKey keyBlock = LogiMine.assignedDevices.get(blockOperationEvent.device.info.uuid);
            if(keyBlock != null) {
                keyBlock.onStructureUpdate(blockOperationEvent);
            } else {
                System.out.println("No keyblock assigned to the device, can't handle the block operation.");
            }
        } else if(bridgeEvent instanceof BlockOperationErrorEvent) {
            BlockOperationErrorEvent blockOperationErrorEvent = (BlockOperationErrorEvent)bridgeEvent;

            System.out.println("Handling the block structure update in the mod.");

            //Passing the event to the respective assigned key block.
            BlockKey keyBlock = LogiMine.assignedDevices.get(blockOperationErrorEvent.device.info.uuid);
            if(keyBlock != null) {
                keyBlock.rebuildStructure(blockOperationErrorEvent.structure);
            } else {
                System.out.println("No keyblock assigned to the device, can't handle the block operation.");
            }
        }
    }
}
