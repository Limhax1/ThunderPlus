package com.example.modules.VulcanBypasses;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import thunder.hack.events.impl.EventTick;
import thunder.hack.modules.Module;
import thunder.hack.setting.Setting;

public class VulcanGlide extends Module {
    public final Setting<Double> fallDistance = new Setting<Double>("Fall Distance", 1.0, 0.0, 5.0);

    private double  fallDist = 0;
    public VulcanGlide() {
        super("VulcanGlide", Category.getCategory("VulcanBypasses"));
    }

    @EventHandler
    private void onTick (EventTick event) {
        Vec3d vel = mc.player.getVelocity();
        fallDist = fallDistance.getValue();

        if(mc.player.age % 0 == 2) {
            mc.player.setVelocity(vel.x, -0.155, vel.z);
        }
    }
}
