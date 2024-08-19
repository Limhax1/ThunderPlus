package com.example.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

import static thunder.hack.ThunderHack.mc;

public class PlayerUtils {
    private static final double diagonal = 1 / Math.sqrt(2);

    public static Color getPlayerColor(PlayerEntity entity, Color defaultColor) {
        return defaultColor;
    }

    public static Vec3d getHorizontalVelocity(double bps) {
        float yaw = mc.player.getYaw();

        Vec3d forward = Vec3d.fromPolar(0, yaw);
        Vec3d right = Vec3d.fromPolar(0, yaw + 90);
        double velX = 0;
        double velZ = 0;

        boolean a = false;
        if (mc.player.input.pressingForward) {
            velX += forward.x / 20 * bps;
            velZ += forward.z / 20 * bps;
            a = true;
        }
        if (mc.player.input.pressingBack) {
            velX -= forward.x / 20 * bps;
            velZ -= forward.z / 20 * bps;
            a = true;
        }

        boolean b = false;
        if (mc.player.input.pressingRight) {
            velX += right.x / 20 * bps;
            velZ += right.z / 20 * bps;
            b = true;
        }
        if (mc.player.input.pressingLeft) {
            velX -= right.x / 20 * bps;
            velZ -= right.z / 20 * bps;
            b = true;
        }

        if (a && b) {
            velX *= diagonal;
            velZ *= diagonal;
        }

        // Instead of mutating an existing Vec3d, return a new one with the calculated velocity.
        return new Vec3d(velX, 0, velZ);
    }
}
