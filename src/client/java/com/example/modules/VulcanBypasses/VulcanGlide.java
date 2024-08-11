package com.example.modules.VulcanBypasses;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import thunder.hack.events.impl.EventTick;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class VulcanGlide extends Module {
    public final Setting<Double> fallDistance = new Setting<Double>("Fall Distance", 1.0, 0.0, 5.0);

    public VulcanGlide() {
        super("VulcanGlide", Module.Category.getCategory("VulcanBypasses"));
    }

    @EventHandler
    private void onTick(EventTick event) {
        ClientPlayerEntity player = mc.player;
        Vec3d v = player.getVelocity();
        if(player.fallDistance > fallDistance.getValue())
            if(!player.isOnGround() && player.age % 2 == 0) {
                player.setVelocity(v.x, -0.155, v.z);
            };

    }
}

