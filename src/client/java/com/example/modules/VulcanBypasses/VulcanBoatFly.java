package com.example.modules.VulcanBypasses;

import com.example.utils.PlayerUtils;
import com.example.events.BoatMoveEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.*;
import thunder.hack.events.impl.EventTick;
import thunder.hack.modules.Module;
import thunder.hack.setting.Setting;

public class VulcanBoatFly extends Module {

    public final Setting<Double> speed = new Setting<Double>("HorizontalSpeed", 20.0, 0.0, 79.0);
    public final Setting<Double> upwardSpeed = new Setting<Double>("UpwardSpeed", 48.0, 5.0, 48.0);
    public final Setting<Double> downwardSpeed = new Setting<Double>("DownwardSpeed", 100.0, 0.0, 90.0);


    public VulcanBoatFly() {
        super("VulcanBoatFly", Category.getCategory("VulcanBypasses"));
    }

    private static final double FALL_SPEED = 1.01;


    // don't suffocate in blocks
    private boolean moveHorizontally(double amount) {
        Box boundingBox = mc.player.getBoundingBox().union(mc.player.getVehicle().getBoundingBox());
        boundingBox = boundingBox.offset(0, amount, 0).union(boundingBox);
        if (mc.world.getBlockCollisions(null, boundingBox).iterator().hasNext()) return false;
        mc.player.getVehicle().setPosition(mc.player.getVehicle().getPos().add(0, amount, 0));
        return true;
    }

    private int verticalMoveCooldown = 0;

    @EventHandler
    private void onTick(EventTick event) {
        if (verticalMoveCooldown > 0) verticalMoveCooldown--;
        if (mc.world == null || mc.player.getVehicle() == null || mc.player.getVehicle().getControllingPassenger() != mc.player) return;
        long t = mc.world.getTime();
        if (t % 10 == 2) {
            moveHorizontally(FALL_SPEED / 2);
        }
        float multiplier = verticalMoveCooldown > 0 ? 11 : 1;
        moveHorizontally(-(FALL_SPEED / 20) * multiplier);
    }

    @EventHandler
    public void BoatMoveEvent(BoatMoveEvent event) {
        sendMessage("Ran");
        if (event.boat.getControllingPassenger() != mc.player) return;
        boolean useTimer = mc.player.input.getMovementInput().lengthSquared() != 0 && !(mc.options.jumpKey.isPressed() || mc.options.sprintKey.isPressed());
        long t = mc.world.getTime();
        event.boat.setYaw(mc.player.getYaw());

        // Horizontal movement
        Vec3d vel = PlayerUtils.getHorizontalVelocity(speed.getValue() * 5);
        double velX = t % 5 == 0 ? vel.getX() : 0;
        double velY = 0;
        double velZ = t % 5 == 0 ? vel.getZ() : 0;

        // Vertical movement
        if (mc.options.jumpKey.isPressed() && verticalMoveCooldown <= 0 && t % 5 != 0) {
            velY += upwardSpeed.getValue() / 2.5;
            verticalMoveCooldown = 8;
        }

        if (mc.options.sprintKey.isPressed() && t % 5 != 0) velY -= downwardSpeed.getValue() / 20 * 1.2;

        // Apply velocity
        Vec3d boatPosAfter = event.boat.getPos().add(velX, velY, velZ);
        ChunkPos cp = new ChunkPos(new BlockPos((int) boatPosAfter.x, (int) boatPosAfter.y, (int) boatPosAfter.z));
        if (mc.world.getChunkManager().isChunkLoaded(cp.x, cp.z)) {
            event.boat.setVelocity(velX, velY, velZ);
            sendMessage("set");
        }
    }
}
