package com.hugeflow.aire.controller;

import com.squareup.otto.Bus;

/**
 * Created by moltak on 14. 12. 25..
 * Blow air event bus
 */
public class BlowAirEventBus {
    private static Bus bus;

    private BlowAirEventBus() {}

    public static Bus getInstance() {
        if(bus == null) {
            synchronized (BlowAirEventBus.class) {
                if(bus == null) {
                    bus = new Bus("blow_event_bus");
                }
            }
        }
        return bus;
    }
}
