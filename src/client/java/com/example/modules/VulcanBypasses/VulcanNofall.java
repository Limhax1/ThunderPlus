package com.example.modules.VulcanBypasses;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.injection.accesors.IPlayerMoveC2SPacket;
import thunder.hack.setting.Setting;

public class VulcanNofall extends Module {
    private final Setting<Float> MotionY = new Setting<>("Ymotion", -3f, -8.0f, 1f);
    private final Setting<Float> Falldistance = new Setting<>("Falldistance", 4f, 0f, 10f);

    public VulcanNofall() {
        super("Vulcan Nofall", Module.Category.getCategory("VulcanBypasses"));
    }

    @EventHandler
    public void onSendPacket(PacketEvent.Send event) {
        ClientPlayerEntity player = mc.player;
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
            if (player.fallDistance > Falldistance.getValue()) {
                var vel = mc.player.getVelocity();
                ((IPlayerMoveC2SPacket) packet).setOnGround(true);
                mc.player.setVelocity(vel.x,MotionY.getValue(), vel.z);
                player.fallDistance = -Falldistance.getValue();
            }
        }
    }
}
