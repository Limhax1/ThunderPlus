package com.example.modules.VulcanBypasses;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import thunder.hack.events.impl.EventTick;
import thunder.hack.modules.Module;

public class NoFall extends Module {
    public NoFall() {
        super("Universal Nofall", Category.getCategory("VulcanBypasses"));
    }

    @EventHandler
    private void onPreTick(EventTick event) {
        if (!mc.player.isOnGround() && mc.player.fallDistance > 3f) {
            sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY() + 0.00000001, mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), false));
            mc.player.onLanding();
        }
    }
}

