package com.example.modules.VulcanBypasses;

import com.example.utils.MoveUtil;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import thunder.hack.ThunderHack;
import thunder.hack.events.impl.EventTick;
import thunder.hack.features.modules.Module;


public class VulcanSpeed extends Module {
    public VulcanSpeed() {
        super("VulcanSpeed", Module.Category.getCategory("VulcanBypasses"));
    }

    int count = 0;
    int Ticks = 0;

    @EventHandler
    private void onTick(EventTick event) {
        if (mc.player.isOnGround()) {
            if(mc.options.forwardKey.isPressed() || mc.options.backKey.isPressed() || mc.options.rightKey.isPressed() || mc.options.leftKey.isPressed()) {
                mc.player.jump();
            }
        }

        if(!mc.player.isOnGround() && mc.player.age % 14 == 0) {
            MoveUtil.strafe(0.595);
            mc.player.setVelocity(mc.player.getVelocity().getX(), -0.37, mc.player.getVelocity().getZ());
        }

        if (mc.player.isOnGround()) {
            MoveUtil.strafe(0.48);
        }
    }
}
