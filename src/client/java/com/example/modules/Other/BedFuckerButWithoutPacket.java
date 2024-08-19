package com.example.modules.Other;

import com.example.utils.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thunder.hack.events.impl.EventTick;
import thunder.hack.features.modules.Module;

public class BedFuckerButWithoutPacket extends Module {
    private int breakCooldown = 0;

    public BedFuckerButWithoutPacket() {
        super("BedFucker", Category.getCategory("Other"));
    }


    @EventHandler
    private void onTick(EventTick event) {
        if (breakCooldown > 0) {
            breakCooldown--;
            return; // If the cooldown is active, skip this tick
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        BlockPos playerPos = mc.player.getBlockPos();

        // Check if the attack key is not already pressed and there is a glass block within range
        boolean foundGlass = false;
        for (int xOffset = -3; xOffset <= 3; xOffset++) {
            for (int yOffset = -3; yOffset <= 3; yOffset++) {
                for (int zOffset = -3; zOffset <= 3; zOffset++) {

                    BlockPos blockPos = playerPos.add(xOffset, yOffset, zOffset);
                    BlockPos blockPos1 = playerPos.add(xOffset, yOffset + 1, zOffset);
                    Block block = mc.world.getBlockState(blockPos).getBlock();
                    Block block1 = mc.world.getBlockState(blockPos1).getBlock();
                    // Check if the block is glass
                    if (block == Blocks.WHITE_BED || block == Blocks.RED_BED || block == Blocks.MAGENTA_BED || block == Blocks.BLUE_BED || block == Blocks.GREEN_BED || block == Blocks.BROWN_BED || block == Blocks.YELLOW_BED || block == Blocks.BLACK_BED || block == Blocks.LIGHT_BLUE_BED || block == Blocks.GRAY_BED ||block == Blocks.LIGHT_GRAY_BED || block == Blocks.ORANGE_BED || block == Blocks.LIME_BED || block == Blocks.PINK_BED || block == Blocks.PURPLE_BED || block == Blocks.CYAN_BED) {
                        foundGlass = true;

                        // Calculate the direction to face the block
                        Vec3d blockCenter = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() -1, blockPos.getZ() + 0.5);
                        Vec3d playerToBlock = blockCenter.subtract(mc.player.getPos()).normalize();

                        // Calculate yaw from the direction vector
                        double yaw = Math.toDegrees(Math.atan2(playerToBlock.z, playerToBlock.x)) - 90;
                        double yaw1 = Math.toDegrees(Math.atan2(playerToBlock.z, playerToBlock.x)) - 90;

                        // Calculate pitch from the direction vector
                        double pitch = Math.toDegrees(Math.atan2(playerToBlock.y, Math.sqrt(playerToBlock.x * playerToBlock.x + playerToBlock.z * playerToBlock.z)));
                        double pitch1 = Math.toDegrees(Math.atan2(playerToBlock.y, Math.sqrt(playerToBlock.x * playerToBlock.x + playerToBlock.z * playerToBlock.z)));

                        // Adjust the pitch angle slightly lower
                        pitch -= 0.5;

                        // Set the player rotation

                        // Simulate holding down left click (start breaking block)
                        if (block1 != Blocks.AIR) {
                            pitch1 = -pitch1;
                            mc.interactionManager.updateBlockBreakingProgress(blockPos1, mc.player.getHorizontalFacing());
                            mc.player.swingHand(Hand.MAIN_HAND);
                            //mc.player.setYaw((float) yaw1);
                            //mc.player.setPitch((float) pitch1);
                        } else {
                            pitch = -pitch;
                            mc.interactionManager.updateBlockBreakingProgress(blockPos, mc.player.getHorizontalFacing());
                            mc.player.swingHand(Hand.MAIN_HAND);
                            //mc.player.setYaw((float) yaw);
                            //mc.player.setPitch((float) pitch);
                        }

                        if (block1 != Blocks.AIR) {
                            pitch = -pitch;
                            pitch1 = -pitch1;
                            //BlockUtils.breakBlock(blockPos1, true);
                        } else

                        // Set the cooldown to 20 ticks (1 second)
                        breakCooldown = 0;
                        return; // Break out of the loop since we only want to break one block at a time
                    }
                }
            }
        }

        // Lmao glass detector 2020 special $$$
        if (!foundGlass) {
            return;
        }
    }
}
