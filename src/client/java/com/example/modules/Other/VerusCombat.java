package com.example.modules.Other;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import thunder.hack.events.impl.EventAttack;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;

public class VerusCombat extends Module {
    public VerusCombat() {
        super("VerusCancel", Category.getCategory("Other"));
    }

    private boolean waitUntilCombat = true;  // Configuration for waiting until combat occurs
    private boolean b = false;  // A toggle state for ping alteration
    private boolean combatOccurred = false;  // Flag indicating if combat has occurred

    // Handler for attack events
    @EventHandler
    public void onAttack(EventAttack event) {
        combatOccurred = true;
    }

    // Handler for world change events

    // Handler for packet events
    @EventHandler
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CommonPongC2SPacket) {
            event.cancel();
        }
    }
    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof CommonPingS2CPacket) {
            event.cancel();
        }
    }
}
