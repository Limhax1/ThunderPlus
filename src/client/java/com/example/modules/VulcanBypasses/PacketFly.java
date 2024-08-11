package com.example.modules.VulcanBypasses;

import com.example.utils.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import thunder.hack.events.impl.EventMove;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

import java.util.HashSet;

public class PacketFly extends Module {
    private final HashSet<PlayerMoveC2SPacket> packets = new HashSet<>();
    public final Setting<Double> horizontalspeed = new Setting<Double>("speed", 4.6, 0.0, 5.0);
    public final Setting<Boolean> Vulcanfly = new Setting<>("VulcanFly", true);


    private int flightCounter = 0;
    private int teleportID = 0;

    public PacketFly() {
        super("Vulcan-Inf Fly", Module.Category.getCategory("VulcanBypasses"));
    }

    @EventHandler
    public void onSendMovementPackets(EventMove event) {
        mc.player.setVelocity(0.0, 0.0, 0.0);
        double speed = 0.0;
        boolean checkCollisionBoxes = checkHitBoxes();

        speed = mc.player.input.jumping && (checkCollisionBoxes || !(mc.player.input.movementForward != 0.0 || mc.player.input.movementSideways != 0.0)) ? (false && !checkCollisionBoxes ? (resetCounter(10) ? -0.032 : 0 / 20) :0 / 20) : (mc.player.input.sneaking ? 0 / -20 : (!checkCollisionBoxes ? (resetCounter(4) ? (false ? -0.04 : 0.0) : 0.0) : 0.0));

        Vec3d horizontal = PlayerUtils.getHorizontalVelocity(horizontalspeed.getValue());

        mc.player.setVelocity(horizontal.x, speed, horizontal.z);
        sendPackets(mc.player.getVelocity().x, mc.player.getVelocity().y, mc.player.getVelocity().z, false);
    }

    @EventHandler
    public void onMove(EventMove event) {
        if(Vulcanfly.getValue() && mc.player.age % 5 == 0) {
            mc.options.forwardKey.setPressed(true);
        }
        else {
            mc.options.forwardKey.setPressed(false);
        }

        if(Vulcanfly.getValue() && mc.player.age % 20 == 0 && mc.options.useKey.isPressed()) {
            mc.player.networkHandler.sendChatMessage("@vclip -5");
        }

        if(Vulcanfly.getValue() && mc.player.age % 20 == 0 && mc.options.jumpKey.isPressed()) {
            mc.player.networkHandler.sendChatMessage("@vclip 10");
        }
    }

    @EventHandler
    public void onPacketSent(PacketEvent.Send event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket && !packets.remove((PlayerMoveC2SPacket) event.getPacket())) {
            mc.player.sendMessage(Text.of("ran"));
            event.cancel();
        }
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket && !(mc.player == null || mc.world == null)) {
            PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket) event.getPacket();
        }
    }

    private boolean checkHitBoxes() {
        return !mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().stretch(-0.0625, -0.0625, -0.0625)).iterator().hasNext();
    }

    private boolean resetCounter(int counter) {
        if (++flightCounter >= counter) {
            flightCounter = 0;
            return true;
        }
        return false;
    }

    private void sendPackets(double x, double y, double z, boolean teleport) {
        Vec3d vec = new Vec3d(x, y, z);
        Vec3d position = mc.player.getPos().add(vec);
        Vec3d outOfBoundsVec = outOfBoundsVec(vec, position);
        packetSender(new PlayerMoveC2SPacket.PositionAndOnGround(position.x, position.y, position.z, mc.player.isOnGround()));
        teleportPacket(position, teleport);
    }

    private void teleportPacket(Vec3d pos, boolean shouldTeleport) {
        if (shouldTeleport) {
            mc.player.networkHandler.sendPacket(new TeleportConfirmC2SPacket(++teleportID));
        }
    }

    private Vec3d outOfBoundsVec(Vec3d offset, Vec3d position) {
        return position.add(0.0, 0.0, 0.0);
    }

    private void packetSender(PlayerMoveC2SPacket packet) {
        packets.add(packet);
        mc.player.networkHandler.sendPacket(packet);
    }
}
