package com.example.modules.Other;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;

public class AAC1910 extends Module {
    public AAC1910() {
        super("AACDisabler", Category.getCategory("Other"));
    }

        @EventHandler
        public void onPacketSend(PacketEvent.Send event) {
            if (event.getPacket() instanceof PlayerMoveC2SPacket) {
                PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket) event.getPacket();

                // Create a new packet with the same data but adjusted Y position
                PlayerMoveC2SPacket newPacket = new PlayerMoveC2SPacket.Full(
                        packet.getX(mc.player.getX()),
                        packet.getY(mc.player.getY()) + 7.0E-9, // Adjust Y position here
                        packet.getZ(mc.player.getZ()),
                        packet.getYaw(mc.player.getYaw()),
                        packet.getPitch(mc.player.getPitch()),
                        packet.isOnGround()
                );

                // Send the new packet
                mc.player.networkHandler.sendPacket(newPacket);

                // Send the PlayerInputC2SPacket as well
                mc.player.networkHandler.sendPacket(
                        new PlayerInputC2SPacket(
                                mc.player.sidewaysSpeed,
                                mc.player.forwardSpeed,
                                true,
                                mc.player.input.sneaking
                        )
                );

            }
        }
    }

