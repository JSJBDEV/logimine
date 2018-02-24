package com.logitow.logimine.Event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class LogitowBridgeEvent extends Event {

    /**
     * The logitow-bridge event.
     *
     * Created by itsMatoosh
     */
    public com.logitow.bridge.event.Event deviceEvent;

    @Override
    public boolean isCancelable() {
        return false;
    }

    public LogitowBridgeEvent(com.logitow.bridge.event.Event event) {
        super();
        deviceEvent = event;
    }
}
