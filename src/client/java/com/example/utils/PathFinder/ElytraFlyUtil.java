package com.example.utils.PathFinder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class ElytraFlyUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Random random = new Random();
    private static long lastDirectionChangeTime = 0;
    private static long lastFireworkUseTime = 0;
    private static float targetYaw = 0;
    private static float targetPitch = 0;
    private static boolean isRotating = false;
    public static double msdelay = 3000; // TODO: make a setting for this in pathfinding module.


    public static void autoFly(double minY, double maxY, double rotationSpeed) {
        if (mc.player == null || !mc.player.isFallFlying()) return;

        if (mc.player.getY() < minY) {
            targetPitch = -10;
            isRotating = true;
        } else if (mc.player.getY() > maxY) {
            targetPitch = 10;
            isRotating = true;
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastDirectionChangeTime > 5000) { // 5 másodpercenként irányváltás
                rotateToRandomAirBlockInFront();
                lastDirectionChangeTime = currentTime;
            }
        }

        if (isRotating) {
            smoothRotate(rotationSpeed);
        }

        useFireworkRocket(msdelay);
    }

    private static void rotateToRandomAirBlockInFront() {
        if (mc.player == null || mc.world == null) return;

        Vec3d playerPos = mc.player.getPos();
        Vec3d playerLook = mc.player.getRotationVector();
        World world = mc.world;
        int searchRadius = 20;

        for (int attempts = 0; attempts < 10; attempts++) {
            double distance = random.nextDouble() * searchRadius;
            double angle = (random.nextDouble() - 0.5) * Math.PI / 2;

            Vec3d targetVec = playerPos.add(
                playerLook.x * distance + playerLook.z * distance * Math.sin(angle),
                random.nextDouble() * 10 - 5,
                playerLook.z * distance - playerLook.x * distance * Math.sin(angle)
            );

            BlockPos targetPos = new BlockPos((int)targetVec.x, (int)targetVec.y, (int)targetVec.z);
            if (world.getBlockState(targetPos).isAir()) {
                setRotationTarget(targetPos);
                return;
            }
        }
    }

    private static void setRotationTarget(BlockPos pos) {
        if (mc.player == null) return;

        Vec3d vec = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        double diffX = vec.x - mc.player.getX();
        double diffY = vec.y - mc.player.getY();
        double diffZ = vec.z - mc.player.getZ();

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        targetPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        isRotating = true;
    }

    private static void smoothRotate(double rotationSpeed) {
        if (mc.player == null) return;

        float yawDifference = targetYaw - mc.player.getYaw();
        float pitchDifference = targetPitch - mc.player.getPitch();


        while (yawDifference > 180) yawDifference -= 360;
        while (yawDifference < -180) yawDifference += 360;

        mc.player.setYaw((float) (mc.player.getYaw() + Math.max(-rotationSpeed, Math.min(rotationSpeed * 3, yawDifference))));
        mc.player.setPitch((float) (mc.player.getPitch() + Math.max(-rotationSpeed, Math.min(rotationSpeed * 3, pitchDifference))));

        if (Math.abs(yawDifference) < 1 && Math.abs(pitchDifference) < 1) {
            isRotating = false;
        }
    }

    private static void useFireworkRocket(double msdelay) {
        if (mc.player == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFireworkUseTime < msdelay) return;

        int fireworkSlot = findFireworkRocket();
        if (fireworkSlot != -1) {
            int previousSlot = mc.player.getInventory().selectedSlot;
            mc.player.getInventory().selectedSlot = fireworkSlot;
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            mc.player.getInventory().selectedSlot = previousSlot;
            lastFireworkUseTime = currentTime;
        }
    }

    private static int findFireworkRocket() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.FIREWORK_ROCKET) {
                return i;
            }
        }
        return -1; // Nem találtunk rakétát
    }
}
