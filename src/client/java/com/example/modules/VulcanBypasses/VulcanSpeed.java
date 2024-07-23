/*
package com.example.modules.VulcanBypasses;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import thunder.hack.ThunderHack;
import thunder.hack.modules.Module;
import thunder.hack.modules.movement.Strafe;

import static thunder.hack.core.impl.ModuleManager.strafe;

public class VulcanSpeed extends Module {
    public VulcanSpeed(@NotNull String name, @NotNull Module.Category category) {
        super("VulcanSpeed", Category.getCategory("VulcanBypasses"));
    }

    int count = 0;
    int Ticks = 0;

    @EventHandler
    private void onTick(TickEvent.Post event) {
        ClientPlayerEntity player = mc.player;
        Vec3d v = player.getVelocity();

        if(player.isOnGround()) {
            if(mc.options.forwardKey.isPressed() || mc.options.leftKey.isPressed() || mc.options.rightKey.isPressed() || mc.options.backKey.isPressed())
                player.jump();
        }
        if(!player.isOnGround()) {
            Ticks++;
        }
        if(player.isOnGround()) {
            Ticks = 0;
        }
        if(Ticks >= StrafeTicks.get() && !player.isOnGround()) {

        }
        else{
            ChatUtils.sendPlayerMsg(".toggle strafe+ off");
        }

        if(player.fallDistance > 0.1)
            if(!player.isOnGround() && player.age % 2 == 0) {

                player.setVelocity(v.x, fallSpeed1.get(), v.z);
                player.setVelocity(v.x, fallSpeed2.get(), v.z);
                player.setVelocity(v.x, fallSpeed3.get(), v.z);
                player.setVelocity(v.x, fallSpeed4.get(), v.z);
                switch (count) {
                    case 0:
                        player.setVelocity(v.x, fallSpeed1.get(), v.z);
                        count++;
                        break;
                    case 1:
                        player.setVelocity(v.x, fallSpeed2.get(), v.z);
                        count++;
                        break;
                    case 2:
                        player.setVelocity(v.x, fallSpeed3.get(), v.z);
                        count++;
                        break;
                    case 3:
                        player.setVelocity(v.x, fallSpeed4.get(), v.z);
                        count++;
                        break;
                    default:
                        count = 0;
                        break;

                }

            };
    }

}

 */