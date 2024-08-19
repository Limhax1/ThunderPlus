package com.example.modules.Other;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;

import java.util.concurrent.CompletableFuture;

public class PingSpoof extends Module {
    public PingSpoof() {
        super("PingSpoof", Category.getCategory("Other"));
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (1 == 2) {
            if (event.getPacket() instanceof KeepAliveC2SPacket p && false) {
                event.cancel();
                sendLater(new KeepAliveC2SPacket(p.getId()));
            } else if (event.getPacket() instanceof CommonPongC2SPacket p && false) {
                event.cancel();
                sendLater(new CommonPongC2SPacket(p.getParameter()));
            }
        } else {
            event.cancel();
            sendLater(event.getPacket());
        }
    }

    private void sendLater(Packet<?> packet) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep((long) (1000 * Math.random() - 20));
            } catch (InterruptedException ignored) {
            }
            if (mc.getNetworkHandler() == null) return;
            mc.getNetworkHandler().getConnection().send(packet, null);
        });
    }

}
