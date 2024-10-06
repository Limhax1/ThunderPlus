package com.example.modules.Other;

import com.example.utils.BlockUtils;
import com.example.utils.PathFinder.ElytraFlyUtil;
import com.example.utils.PathFinder.Pathfinder;
import com.example.utils.PathFinder.PathfinderMiner;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.EventBreakBlock;
import thunder.hack.events.impl.EventPostTick;
import thunder.hack.events.impl.EventTick;
import thunder.hack.features.modules.Module;
import thunder.hack.gui.notification.Notification;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.setting.impl.PositionSetting;
import thunder.hack.utility.render.BlockAnimationUtility;

import java.util.List;

import static com.example.utils.PathFinder.Pathfinder.isWalkable;


public class OldPathfindingModule extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private BlockPos targetPos;
    public static List<BlockPos> path;
    public  static int pathIndex;
    private double diffYaw = 0;
    private double diffPitch = 0;
    private BlockPos lastPos;
    private long lastMoveTime;
    public boolean blockmainrots = false;
    private BlockPos lastBrokenPos = null;
    private BlockPos currentBreakingBlock = null;
    private long breakStartTime = 0;
    private long lastPathfindingTime = 0;
    private static final long PATHFINDING_COOLDOWN = 1000; // 1 másodperc
    private boolean isElytraFlying = false;


    public final Setting<Integer> CoordX = new Setting<>("CoordX", 100, -999999, 999999);
    public final Setting<Integer> CoordY = new Setting<>("CoordY", 100, -64, 270);
    public final Setting<Integer> CoordZ = new Setting<>("CoordZ", 100, -999999, 999999);
    public final Setting<Float> YOffset = new Setting<>("YOffset", 1.0f, -2.0f, 2.0f);
    public final Setting<Float> yawSpeed = new Setting<>("YawSpeed", 1.0f, 0.0f, 2.0f);
    public final Setting<Float> pitchSpeed = new Setting<>("PitchSpeed", 1.0f, 0.0f, 2.0f);
    public final Setting<Float> randommult = new Setting<>("RandomMult", 1.0f, 0.0f, 2.0f);
    public final Setting<Float> MaxMistake = new Setting<>("MaxInaccuracy", 0.1f, 0.050f, 1.0f);
    public final Setting<Float> radius = new Setting<>("Radius", 30.0f, 10.0f, 100.0f);
    public final Setting<Float> maxdegree = new Setting<>("MaxYawDegree", 20.0f, 1.0f, 100.0f);
    public static Setting<String> block1 = new Setting<>("Block", "iron_ore");
    public static Setting<Boolean> crashbruh = new Setting<>("Please turn this on and off after changing block", true);
    public static Setting<Boolean> miningmode = new Setting<>("Miningmode", true);
    public static Setting<Boolean> render = new Setting<>("Render", true);
    public static Setting<Boolean> debug = new Setting<>("Debug Messages", true);
    public static Setting<Boolean> elytraFlyEnabled = new Setting<>("Elytra Fly", true);
    public final Setting<Float> elytraMinY = new Setting<>("elytraMinY", 100.0f, 1.0f, 300.0f);
    public final Setting<Float> elytraMaxY = new Setting<>("elytraMaxY", 120.0f, 1.0f, 300.0f);
    public final Setting<Float> rotationSpeed = new Setting<>("elytrarotationspeed", 5.0f, 0.1f, 10.0f);
    private final Setting<ColorSetting> linedebug = new Setting<>("Color", new ColorSetting(0xFFC589FF));
    private final Setting<ColorSetting> lineColor = new Setting<>("Color", new ColorSetting(0xFF90EEC8));
    //private final Setting<ColorSetting> lineColor1 = new Setting<>("Color", new ColorSetting(0xFF00FFFF));
    private final Setting<ColorSetting> sideColor = new Setting<>("Line Color", new ColorSetting(0xFFC589FF));
    //private final Setting<ColorSetting> sideColor1 = new Setting<>("Color", new ColorSetting(0xFF00FFFF));


    public OldPathfindingModule() {
        super("OldPathFinder", Category.getCategory("Other"));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (elytraFlyEnabled.getValue()) {
            isElytraFlying = true;
        } else {
            if  (miningmode != null && miningmode.getValue()) {
                targetPos = findNearestGemstone();
                startPathfinding(targetPos);
            } else if(miningmode != null && !miningmode.getValue()) {
                targetPos = new BlockPos(CoordX.getValue() , CoordY.getValue(), CoordZ.getValue());
                startPathfinding(targetPos);
            }
        }
    }

    public void startPathfinding(BlockPos target) {
        this.targetPos = target;
        this.pathIndex = 0;


        if(target == null || mc.world.getBlockState(target.down()).getBlock() == Blocks.AIR) {
            if(debug.getValue()) {
                sendMessage("No path found, disabling");
            }
            return;
        }

        if (mc.world != null && miningmode != null && !miningmode.getValue()) {
            sendMessage("Pathfinding from: " + mc.player.getBlockPos().toShortString() + " to: " + targetPos.toShortString());
            this.path = Pathfinder.findPath(mc.player.getBlockPos(), targetPos, mc.world);
            if (path == null || path.isEmpty()) {
                Managers.NOTIFICATION.publicity("Pathfinder", "No Path found, disabling", 4, Notification.Type.ERROR);

                toggle();
            } else {
                Managers.NOTIFICATION.publicity("Pathfinder", "Path Found!", 4, Notification.Type.ERROR);
            }
        } else if(mc.world != null && miningmode != null && miningmode.getValue()) {
            if(debug.getValue()) {
                sendMessage("Pathfinding from: " + mc.player.getBlockPos().toShortString() + " to: " + targetPos.toShortString());
            }
            this.path = PathfinderMiner.findPath(mc.player.getBlockPos(), targetPos, mc.world);
            if (path == null || path.isEmpty()) {
                if(debug.getValue()) {
                    sendMessage("No path found, disabling");
                }
                toggle();
            } else {
                if(debug.getValue()) {
                    sendMessage("Path found! (Length: " + path.size() + ")");
                }
            }
        }
        else {
            if(debug.getValue()) {
                sendMessage("World is null, disabling");
            }
            toggle();
        }
    }

    @EventHandler
    public void onRender2D(DrawContext context) {
        if (mc.player == null || mc.world == null) return;

        if (path == null || pathIndex >= path.size()) {
            // Az aktuális út véget ért, indítsunk új keresést
            tryStartNewPathfinding();
            return;
        }

        if (isElytraFlying) {
            return;
        }

        try {
            BlockPos currentPos = mc.player.getBlockPos();
            BlockPos nextPos = path.get(pathIndex);

            if(BlockUtils.breaking) {
                blockmainrots = true;
            } else {
                blockmainrots = false;
            }

            if(hasBeenStandingStillForTooLong() && mc.player.age % 200 == 0 && !mc.interactionManager.isBreakingBlock()) {
                Managers.NOTIFICATION.publicity("Pathfinder", "Seems like you got stuck, recalculating", 4, Notification.Type.ERROR);
                stopPathfinding();
                startPathfinding(targetPos);
                mc.player.jump();
            }

            if (nextPos.getY() == currentPos.getY() + 1) {
                if (mc.player.isOnGround()) {
                    BlockPos posAbovePlayer = mc.player.getBlockPos().add(0, 2, 0);
                    BlockPos posAboveNext = nextPos.add(0, 1, 0);

                    if (mc.world.getBlockState(posAbovePlayer).getBlock() == Blocks.AIR && mc.world.getBlockState(posAboveNext).getBlock() == Blocks.AIR && !BlockUtils.breaking && diffYaw > -maxdegree.getValue() && diffYaw < maxdegree.getValue()) {
                        if(!mc.interactionManager.isBreakingBlock()) {
                            mc.options.jumpKey.setPressed(true);
                        }
                    } else {
                        if (mc.world.getBlockState(posAbovePlayer).getBlock() != Blocks.AIR && miningmode != null && !miningmode.getValue()) {
                            rotateToBlockonTick(mc, posAbovePlayer);
                            tryBreakBlock(posAbovePlayer);
                        } else if (mc.world.getBlockState(posAboveNext).getBlock() != Blocks.AIR && miningmode != null && !miningmode.getValue()) {
                            rotateToBlockonTick(mc, posAboveNext);
                            tryBreakBlock(posAboveNext);

                        }
                    }
                }
            } else if(nextPos.getY() == currentPos.getY()) {
                mc.options.jumpKey.setPressed(false);
            } else if(nextPos.getY() >= currentPos.getY() + 2){
                if(debug.getValue()) {
                    sendMessage("Mismatch detected in the path, recalculating path");
                }
                stopPathfinding();
                startPathfinding(targetPos);
            }

            if (nextPos.getY() < currentPos.getY()) {
                if (nextPos.getY() == currentPos.getY() - 1) {
                    BlockPos posBelowNext = nextPos.add(0, 1, 0);
                    BlockPos posAboveNext = nextPos.add(0, 2, 0);

                    if (mc.world.getBlockState(posAboveNext).getBlock() != Blocks.AIR && miningmode != null && !miningmode.getValue()) {
                        rotateToBlockonTick(mc, posAboveNext);
                        tryBreakBlock(posAboveNext);
                    } else if (mc.world.getBlockState(posBelowNext).getBlock() != Blocks.AIR && miningmode != null && !miningmode.getValue()) {
                        rotateToBlockonTick(mc, posBelowNext);
                        tryBreakBlock(posBelowNext);
                    }
                }
            }

            boolean isDiagonal = (currentPos.getX() != nextPos.getX()) && (currentPos.getZ() != nextPos.getZ());

            if (isDiagonal) {
                BlockPos corner1 = new BlockPos(nextPos.getX(), nextPos.getY(), currentPos.getZ());
                BlockPos corner2 = new BlockPos(currentPos.getX(), nextPos.getY(), nextPos.getZ());
                //RenderUtils.renderTickingBlock(corner2, sideColor1.get(), linedebug.get(), ShapeMode.Both, 0, 40, true, false);
                //RenderUtils.renderTickingBlock(corner2, sideColor1.get(), linedebug.get(), ShapeMode.Both, 0, 40, true, false);
                if (nextPos.getY() == currentPos.getY() - 1 && miningmode != null && !miningmode.getValue()) {
                    if (mc.world.getBlockState(corner1).isAir() && mc.world.getBlockState(corner1.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(corner1.add(0, 2, 0)).getBlock() != Blocks.AIR && mc.world.getBlockState(nextPos).getBlock() == Blocks.AIR) {
                        rotateToBlockonTick(mc, corner1.add(0, 2, 0));
                        tryBreakBlock(corner1.add(0, 2, 0));
                    } else if (mc.world.getBlockState(corner2).isAir() && mc.world.getBlockState(corner2.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(corner2.add(0, 2, 0)).getBlock() != Blocks.AIR && mc.world.getBlockState(nextPos).getBlock() == Blocks.AIR) {
                        rotateToBlockonTick(mc, corner2.add(0, 2, 0));
                        tryBreakBlock(corner2.add(0, 2, 0));

                    }
                }
                if (!isWalkable(corner1, mc.world) || !isWalkable(corner2, mc.world)) {
                    if (isWalkable(corner1, mc.world)) {
                        nextPos = corner1;
                    } else if (isWalkable(corner2, mc.world)) {
                        nextPos = corner2;
                    } else {
                        BlockPos blockToBreak = mc.world.getBlockState(corner2.add(0, 1, 0)).getBlock() != Blocks.AIR ? corner2.add(0, 1, 0) : corner2;
                        if (mc.world.getBlockState(blockToBreak).getBlock() != Blocks.AIR && mc.world.getBlockState(mc.player.getBlockPos().add(0, 1, 0)).getBlock() == Blocks.AIR && mc.world.getBlockState(nextPos.add(0, 2, 0)).getBlock() == Blocks.AIR && mc.world.getBlockState(nextPos).getBlock() == Blocks.AIR || mc.world.getBlockState(nextPos).getBlock() == Blocks.SHORT_GRASS || mc.world.getBlockState(nextPos).getBlock() == Blocks.TALL_GRASS) {
                            rotateToBlockonTick(mc, blockToBreak);
                            tryBreakBlock(blockToBreak);
                        } else if(miningmode != null && !miningmode.getValue() && mc.world.getBlockState(blockToBreak).getBlock() != Blocks.AIR && mc.world.getBlockState(mc.player.getBlockPos().add(0, 2, 0)).getBlock() == Blocks.AIR && mc.world.getBlockState(nextPos.add(0, 2, 0)).getBlock() != Blocks.AIR && mc.world.getBlockState(nextPos).getBlock() == Blocks.AIR) {
                            rotateToBlockonTick(mc, nextPos.add(0, 2, 0));
                            tryBreakBlock(nextPos.add(0, 2, 0));
                        } else if(mc.world.getBlockState(nextPos).isAir() && mc.world.getBlockState(nextPos.up()).isAir() && mc.world.getBlockState(corner2.add(0, 1, 0)).getBlock() != Blocks.AIR || mc.world.getBlockState(corner1.add(0, 1, 0)).getBlock() != Blocks.AIR) {
                            if(mc.world.getBlockState(corner1).isAir() && mc.world.getBlockState(corner1.up()).getBlock() != Blocks.AIR) {
                            }
                        }
                    }
                }
            }

            if(miningmode != null && miningmode.getValue() && mc.world.getBlockState(nextPos).getBlock() != Blocks.AIR && mc.world.getBlockState(nextPos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(nextPos).getBlock() != Blocks.BEDROCK) {
                rotateToBlockonTick(mc, nextPos);
                tryBreakBlock(nextPos);

            } else if (miningmode != null && miningmode.getValue() && mc.world.getBlockState(nextPos.up()).getBlock() != Blocks.AIR) {
                rotateToBlockonTick(mc, nextPos.up());
                tryBreakBlock(nextPos.up());
            } else if(hasBeenStandingStillForTooLong() && mc.world.getBlockState(path.get(pathIndex)).getBlock() != Blocks.AIR) {
                rotateToBlockonTick(mc, path.get(pathIndex));
                tryBreakBlock(path.get(pathIndex));
            } else if(hasBeenStandingStillForTooLong() && mc.world.getBlockState(path.get(pathIndex).up()).getBlock() != Blocks.AIR) {
                rotateToBlockonTick(mc, path.get(pathIndex).up());
                tryBreakBlock(path.get(pathIndex).up());
            }

            if(mc.world.getBlockState(nextPos.add(0, 2, 0)).getBlock() != Blocks.AIR  && mc.world.getBlockState(nextPos).getBlock() == Blocks.AIR && mc.world.getBlockState(nextPos.up()).getBlock() == Blocks.AIR && miningmode != null && miningmode.getValue()) {
                rotateToBlockonTick(mc, nextPos.add(0, 2, 0));
                tryBreakBlock(nextPos.add(0, 2, 0));
            }


            Vec3d nextPosVec = new Vec3d(nextPos.getX() + 0.5, nextPos.getY(), nextPos.getZ() + 0.5);
            if (mc.player.getPos().squaredDistanceTo(nextPosVec) < MaxMistake.getValue()) {
                pathIndex++;
            }
        } catch (Exception e) {
            // Naplózza a hibát vagy kezelje megfelelően
        }


        if (path == null || pathIndex >= path.size()) {
            return;
        }

        BlockPos nextPos = path.get(pathIndex);
        Vec3d nextPosVec = new Vec3d(nextPos.getX() + 0.5, nextPos.getY(), nextPos.getZ() + 0.5);
        if(!blockmainrots) {
            rotateToBlock(mc, path.get(pathIndex));
            moveTowards(nextPosVec);
        } else {
            mc.options.forwardKey.setPressed(false);
        }


        if (mc.player.getPos().squaredDistanceTo(nextPosVec) < MaxMistake.getValue()) {
            pathIndex++;
        }

        renderPath();
    }

    private void moveTowards(Vec3d target) {
        if(diffYaw > -maxdegree.getValue() && diffYaw < maxdegree.getValue()) {
            mc.options.forwardKey.setPressed(true);
        } else {
            mc.options.forwardKey.setPressed(false);
        }
    }

    public void rotateToBlock(MinecraftClient mc, BlockPos targetPos) {
        Vec3d blockCenter = new Vec3d(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
        Vec3d playerToBlock = blockCenter.subtract(mc.player.getPos());

        double yaw = Math.toDegrees(Math.atan2(playerToBlock.getZ(), playerToBlock.getX())) - 90;
        double pitch = -Math.toDegrees(Math.atan2(playerToBlock.getY(), Math.sqrt(playerToBlock.getX() * playerToBlock.getX() + playerToBlock.getZ() * playerToBlock.getZ())));

        double currentYaw = mc.player.getYaw();
        double currentPitch = mc.player.getPitch();
        diffYaw = MathHelper.wrapDegrees(yaw - currentYaw);
        diffPitch = pitch - currentPitch;

        double stepYaw = diffYaw * yawSpeed.getValue() / 10;
        double stepPitch = diffPitch * pitchSpeed.getValue() / 10;

        double newYaw = currentYaw + stepYaw;
        double newPitch = currentPitch + stepPitch;

        mc.player.setYaw((float) ((float) newYaw + Math.random() * randommult.getValue()));
        mc.player.setPitch((float) ((float) newPitch + Math.random() * randommult.getValue()));
    }

    public void rotateToBlockonTick(MinecraftClient mc, BlockPos targetPos) {
        Vec3d blockCenter = new Vec3d(targetPos.getX() + 0.5, targetPos.getY() - YOffset.getValue(), targetPos.getZ() + 0.5);
        Vec3d playerToBlock = blockCenter.subtract(mc.player.getPos());

        double yaw = Math.toDegrees(Math.atan2(playerToBlock.getZ(), playerToBlock.getX())) - 90;
        double pitch = -Math.toDegrees(Math.atan2(playerToBlock.getY(), Math.sqrt(playerToBlock.getX() * playerToBlock.getX() + playerToBlock.getZ() * playerToBlock.getZ())));

        double currentYaw = mc.player.getYaw();
        double currentPitch = mc.player.getPitch();
        diffYaw = MathHelper.wrapDegrees(yaw - currentYaw);
        diffPitch = pitch - currentPitch;

        double stepYaw = diffYaw * yawSpeed.getValue() / 10;
        double stepPitch = diffPitch * pitchSpeed.getValue() / 10;

        double newYaw = currentYaw + stepYaw;
        double newPitch = currentPitch + stepPitch;
        mc.player.setYaw((float) ((float) newYaw + Math.random() * randommult.getValue()));
        mc.player.setPitch((float) ((float) newPitch + Math.random() * randommult.getValue()));
    }

    public void renderPath() {
        if (path == null) return;
        for (int i = pathIndex; i < path.size(); i++) {
            BlockPos pos = path.get(i);
            if(render.getValue()) {
                //RenderUtils.renderTickingBlock(pos.add(0, 1,0), sideColor.get(), lineColor.get(), ShapeMode.Lines, 0, 20, true, false);
                BlockAnimationUtility.renderBlock(pos, sideColor.getValue().getColorObject(), 0, linedebug.getValue().getColorObject(), BlockAnimationUtility.BlockAnimationMode.Fade, BlockAnimationUtility.BlockRenderMode.Line);
                BlockAnimationUtility.renderBlock(targetPos, lineColor.getValue().getColorObject(), 0, lineColor.getValue().getColorObject(), BlockAnimationUtility.BlockAnimationMode.Fade, BlockAnimationUtility.BlockRenderMode.Line);
            }
        }
    }
    private boolean hasBeenStandingStillForTooLong() {
        BlockPos currentPos = mc.player.getBlockPos();

        if (!currentPos.equals(lastPos)) {
            lastPos = currentPos;
            lastMoveTime = System.currentTimeMillis();
        }

        return (System.currentTimeMillis() - lastMoveTime) > 2500;
    }

    public BlockPos findNearestGemstone() {
        BlockPos searchPos = lastBrokenPos != null ? lastBrokenPos : mc.player.getBlockPos();
        double closestDistanceSq = Double.MAX_VALUE;
        BlockPos closestPos = null;

        double radius = this.radius.getValue();

        for (int x = (int) -radius; x <= radius; x++) {
            for (int y = (int) -radius; y <= radius; y++) {
                for (int z = (int) -radius; z <= radius; z++) {
                    BlockPos pos = searchPos.add(x, y, z);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (!block.getName().getString().equalsIgnoreCase(block1.getValue().replace("_", " "))) {
                        continue;
                    }

                    if (render.getValue()) {
                        BlockAnimationUtility.renderBlock(pos, sideColor.getValue().getColorObject(), 1, linedebug.getValue().getColorObject(), BlockAnimationUtility.BlockAnimationMode.Fade, BlockAnimationUtility.BlockRenderMode.Line);
                    }

                    double distanceSq = searchPos.getSquaredDistance(pos);
                    if (distanceSq < closestDistanceSq) {
                        closestDistanceSq = distanceSq;
                        closestPos = pos;
                    }
                }
            }
        }
        return closestPos;
    }

    public void stopPathfinding() {
        path = null;
        pathIndex = 0;
    }

    private void tryBreakBlock(BlockPos pos) {
        if (currentBreakingBlock == null || !currentBreakingBlock.equals(pos)) {
            currentBreakingBlock = pos;
            breakStartTime = System.currentTimeMillis();
        }

        if (mc.world.getBlockState(pos).isAir()) {
            currentBreakingBlock = null;
            mc.options.attackKey.setPressed(false);
            blockmainrots = false;
            return;
        }

        rotateToBlockonTick(mc, pos);
        if(render.getValue()) {
            BlockAnimationUtility.renderBlock(pos, sideColor.getValue().getColorObject(), 1, linedebug.getValue().getColorObject(), BlockAnimationUtility.BlockAnimationMode.Fade, BlockAnimationUtility.BlockRenderMode.Line);
        }
        mc.options.attackKey.setPressed(true);
        blockmainrots = true;

        if (System.currentTimeMillis() - breakStartTime > 2000 && !mc.interactionManager.isBreakingBlock()) {
            if(debug.getValue()) {
                sendMessage("Breaking the block took too long, retrying");
            }
            currentBreakingBlock = null;
            mc.options.attackKey.setPressed(false);
            blockmainrots = false;
        }
    }

    @EventHandler
    public void onBreakBlock(EventBreakBlock event) {
        if (event.getPos().equals(currentBreakingBlock)) {
            currentBreakingBlock = null;
            mc.options.attackKey.setPressed(false);
            blockmainrots = false;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        isElytraFlying = false;
        mc.options.attackKey.setPressed(false);
        if(debug.getValue()) {
            sendMessage("false");
        }
    }

    private void tryStartNewPathfinding() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPathfindingTime > PATHFINDING_COOLDOWN) {
            if (!isTargetStillOre()) {
                stopPathfinding();
                startPathfinding(findNearestGemstone());
                lastPathfindingTime = currentTime;
            }
        }
    }

    private boolean isTargetStillOre() {
        if (path == null || path.isEmpty()) return false;
        BlockPos target = path.get(path.size() - 1);
        return isOreBlock(mc.world.getBlockState(target).getBlock());
    }

    private boolean isOreBlock(Block block) {
        // Implementálja az ércblokk ellenőrzését
        // Például: return block == Blocks.IRON_ORE;
        return false;
    }

    @EventHandler
    private void onTick(EventPostTick event) {
        if (elytraFlyEnabled.getValue() && mc.player != null) {
            if (mc.player.getInventory().getArmorStack(2).getItem().toString().contains("elytra")) {
                if (!mc.player.isFallFlying()) {
                    mc.options.jumpKey.setPressed(true);
                } else {
                    mc.options.jumpKey.setPressed(false);
                    isElytraFlying = true;
                    ElytraFlyUtil.autoFly(elytraMinY.getValue(), elytraMaxY.getValue(), rotationSpeed.getValue());
                }
            } else {
                isElytraFlying = false;
            }
        } else {
            isElytraFlying = false;
        }
    }
}