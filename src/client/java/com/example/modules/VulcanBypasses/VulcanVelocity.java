package com.example.modules.VulcanBypasses;

import com.example.utils.DamageBoostUtil;
import meteordevelopment.orbit.EventHandler;
import thunder.hack.events.impl.EventTick;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class VulcanVelocity extends Module {
    public final Setting<Integer> Ticks = new Setting<>("Ticks", 1, 0, 20);
    private final Setting<modeEn> mode = new Setting<>("Mode", modeEn.Vulcan);
    public double counter = 0;

    public VulcanVelocity() {
        super("Velocity+", Module.Category.getCategory("VulcanBypasses"));
    }


    // Add this field to track the ticks since the player took damage
    private int ticksSinceDamage = 0;

    @EventHandler
    public void onTick(EventTick event) {
        if (DamageBoostUtil.isHurtTime()) {
            ticksSinceDamage = 0;
        }

        // Increment the counter each tick
        if (ticksSinceDamage < Integer.MAX_VALUE) {
            ticksSinceDamage++;
        }

        // Handle the velocity change in the tick method or wherever appropriate
        handleVelocityChange();
    }

    private void handleVelocityChange() {
        double motionX = mc.player.getVelocity().getX();
        double motionY = mc.player.getVelocity().getY();
        double motionZ = mc.player.getVelocity().getZ();

        // Apply the velocity change only if 2 ticks have passed since damage
            mc.player.setVelocity(-mc.player.getVelocity().x , mc.player.getVelocity().y *1.05, -mc.player.getVelocity().z);
    }

    public enum modeEn {
        Multiserver, Vulcan, Cancel, Sunrise, Custom, Redirect, OldGrim, Jump, GrimNew
    }
}


