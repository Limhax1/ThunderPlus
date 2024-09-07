package com.example.modules.VulcanBypasses;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import thunder.hack.events.impl.EventMove;
import thunder.hack.events.impl.EventPostTick;
import thunder.hack.events.impl.EventTick;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.injection.accesors.IPlayerMoveC2SPacket;

public class VulcanSpider extends Module {
    public static boolean spoofOnGround = false;

    public VulcanSpider() {
        super("Vulcan Spider", Category.getCategory("VulcanBypasses"));
    }

    private int tick = 0;
    private boolean modify = false;
    private boolean start = false;

    private double startY = 0;
    private double lastY = 0;

    private double coff = 0.0000000000326;

    @Override
    public void onEnable() {
        tick = 0;
        start = false;
        modify = false;

        assert mc.player != null;
        startY = mc.player.getPos().y;
    }

    private boolean YGround(double height, double min, double max) {
        String yString = String.valueOf(height);
        yString = yString.substring(yString.indexOf("."));
        double y = Double.parseDouble(yString);
        return y >= min && y <= max;
    }

    private double RGround(double height) {
        String yString = String.valueOf(height);
        yString = yString.substring(yString.indexOf("."));
        return Double.parseDouble(yString);
    }

    @EventHandler
    public void onSendPacket(PacketEvent.Send event) {
        work(event.getPacket());
    }

    @EventHandler
    public void onSentPacket(PacketEvent.Send event) {
        work(event.getPacket());
    }

    private void work(Packet<?> packet) {
        if (modify) {
            if (packet instanceof PlayerMoveC2SPacket move) {
                assert mc.player != null;
                double y = mc.player.getY();
                y = move.getY(y);

                if (YGround(y, RGround(startY) - 0.1, RGround(startY) + 0.1)) {
                    ((IPlayerMoveC2SPacket) packet).setOnGround(true);
                }
                if (mc.player.isOnGround() && block) {
                    block = false;
                    startY = mc.player.getPos().y;
                    start = false;
                }
            }
        } else {
            assert mc.player != null;
            if (mc.player.isOnGround() && block) {
                block = false;
                startY = mc.player.getPos().y;
                start = false;
            }
        }
    }

    private boolean block = false;

    @EventHandler
    public void onTickEventPre(EventTick event) {
        if (modify) {
            ClientPlayerEntity player = mc.player;
            assert player != null;
            double y = player.getPos().y;
            if (lastY == y && tick > 1) {
                block = true;
            } else {
                lastY = y;
            }
        }
    }

    private TypeStarted getType(double startY) {
        TypeStarted temp = TypeStarted.Air;
        double y = RGround(startY);
        assert mc.player != null;
        if (mc.player.isOnGround()) {
            temp = TypeStarted.Block;
            assert mc.world != null;
            if (mc.world.getBlockState(mc.player.getBlockPos()).getBlock() instanceof SlabBlock) {
                temp = TypeStarted.Slab;
            }
        }
        return temp;
    }

    private enum TypeStarted
    {
        Block,
        Slab,
        Air,
    }

    private TypeStarted typeStarted = TypeStarted.Air;

    @EventHandler
    public void onTickEventPost(EventPostTick event) {
        ClientPlayerEntity player = mc.player;
        assert player != null;
        Vec3d pl_velocity = player.getVelocity();
        Vec3d pos = player.getPos();
        ClientPlayNetworkHandler h = mc.getNetworkHandler();
        modify = player.horizontalCollision;
        if (mc.player.isOnGround()) {
            block = false;
            startY = mc.player.getPos().y;
            start = false;
            typeStarted = getType(startY);
        }
        if (player.horizontalCollision) {
            if (!start) {
                start = true;
                startY = mc.player.getPos().y;
                lastY = mc.player.getY();
            }
            if (!block) {
                if (tick == 0) {
                    mc.player.setVelocity(pl_velocity.x, 0.9, pl_velocity.z);
                    tick = 1;
                } else if (tick == 1) {
                    mc.player.setVelocity(pl_velocity.x, 1 - 0.08679999325 - coff, pl_velocity.z);
                    tick = 2;
                } else if (tick == 2) {
                    mc.player.setVelocity(pl_velocity.x, 0.9 - 0.17186398826 - coff, pl_velocity.z);
                    sendMessage("Tick2");
                    tick = 3;
                } else if (tick == 3) {
                    mc.player.setVelocity(pl_velocity.x, 0.9 - coff, pl_velocity.z);
                    sendMessage("Tick3");
                    tick = 0;
                }
                switch (typeStarted) {
                    case Air -> {
                        if (mc.player.getPos().y >= startY + 3) {
                            block = true;
                        }
                    }
                    case Slab -> {
                        if (mc.player.getPos().y >= startY + 2.5) {
                            block = true;
                        }
                    }
                    case Block -> {
                        if (mc.player.getPos(). y >= startY + 3) {
                            block = true;
                        }
                    }
                }
            }
        }
        else {
            modify = false;
            tick = 0;
        }
    }
}