package com.example.modules.VulcanBypasses;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import thunder.hack.events.impl.EventMove;
import thunder.hack.events.impl.EventPlaceBlock;
import thunder.hack.events.impl.EventTick;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.injection.accesors.IPlayerMoveC2SPacket;

import static thunder.hack.ThunderHack.mc;

public class VulcantSpider extends Module {
    public static boolean spoofOnGround = false;

    public VulcantSpider() {
        super("Vulcan Spider", Category.getCategory("VulcanBypasses"));
    }

    @EventHandler
    public void onMove(EventPlaceBlock event) {
        mc.player.setVelocity(0, 3.5, 0);
    }


    @EventHandler
    public void onMove(EventTick event) {
        if (!mc.player.isOnGround() && mc.player.age % 20 == 0) {
        }
    }
}

