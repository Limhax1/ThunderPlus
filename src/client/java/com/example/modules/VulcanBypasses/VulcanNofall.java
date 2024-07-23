package com.example.modules.VulcanBypasses;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.injection.accesors.IPlayerMoveC2SPacket;
import thunder.hack.modules.Module;
import thunder.hack.setting.Setting;

public class VulcanNofall extends Module {
    public final Setting<Integer> MotionY = new Setting<>("YMotion", -4, -10, 2);

    public VulcanNofall() {
        super("Vulcan Nofall", Category.getCategory("VulcanBypasses"));
    }

    @EventHandler
    public void onSendPacket(PacketEvent.Send event) {
        ClientPlayerEntity player = mc.player;
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
            if (player.fallDistance > 6) {
                var vel = mc.player.getVelocity();
                ((IPlayerMoveC2SPacket) packet).setOnGround(true);
                mc.player.setVelocity(vel.x,-0.1, vel.z);
                player.fallDistance = 0f;
            }
        }
    }
}
