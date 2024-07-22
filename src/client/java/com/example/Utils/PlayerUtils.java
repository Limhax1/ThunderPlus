/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package com.example.Utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.PotionItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;

import java.awt.*;

import static thunder.hack.ThunderHack.mc;


public class PlayerUtils {
    private static final double diagonal = 1 / Math.sqrt(2);
    private static final Vec3d horizontalVelocity = new Vec3d(0, 0, 0);

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

        return horizontalVelocity;
    }
}