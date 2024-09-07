package com.example.utils.PathFinder;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class PathfinderMiner {


    public static List<BlockPos> findPath(BlockPos start, BlockPos end, World world) {
        if (start.equals(end)) {
            return Collections.singletonList(end);
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.fCost));
        Set<BlockPos> closedSet = new HashSet<>();
        Map<BlockPos, Node> allNodes = new HashMap<>();

        Node startNode = new Node(start, null, 0, heuristic(start, end));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            closedSet.add(currentNode.pos);

            if (currentNode.pos.equals(end)) {
                return reconstructPath(currentNode);
            }

            for (BlockPos neighborPos : getNeighbors(currentNode.pos, world)) {
                if (closedSet.contains(neighborPos)) continue;

                double tentativeGCost = currentNode.gCost + 1;
                Node neighborNode = allNodes.getOrDefault(neighborPos, new Node(neighborPos, null, Double.MAX_VALUE, heuristic(neighborPos, end)));

                if (tentativeGCost < neighborNode.gCost) {
                    neighborNode.parent = currentNode;
                    neighborNode.gCost = tentativeGCost;
                    neighborNode.fCost = neighborNode.gCost + neighborNode.hCost;

                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                }

                allNodes.put(neighborPos, neighborNode);
            }
        }

        return null;
    }

    private static List<BlockPos> reconstructPath(Node node) {
        List<BlockPos> path = new ArrayList<>();
        while (node != null) {
            path.add(node.pos);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private static double heuristic(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    private static List<BlockPos> getNeighbors(BlockPos pos, World world) {
        List<BlockPos> neighbors = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    // Skip the current position itself
                    if (x == 0 && y == 0 && z == 0) continue;

                    BlockPos neighborPos = pos.add(x, y, z);
                    if (isWalkable(neighborPos, world)) {
                        neighbors.add(neighborPos);
                    }
                }
            }
        }

        return neighbors;
    }

    public static boolean isWalkable(BlockPos pos, World world) {
        boolean isGroundWalkable = world.getBlockState(pos).getBlock() != (Blocks.AIR) ||
                world.getBlockState(pos).getBlock() == (Blocks.AIR) &&
                        world.getBlockState(pos).getBlock() != (Blocks.BEDROCK);


        boolean isBelowNotObstructed = !world.getBlockState(pos.down()).getBlock().equals(Blocks.AIR) &&
                !world.getBlockState(pos.down()).getBlock().equals(Blocks.WATER) &&
                !world.getBlockState(pos.down()).getBlock().equals(Blocks.LAVA);

        boolean isAboveClear = world.getBlockState(pos.up()).isAir() ||
                world.getBlockState(pos.up()).getBlock() != (Blocks.AIR);

        return isGroundWalkable && isBelowNotObstructed && isAboveClear;
    }



    private static class Node {
        BlockPos pos;
        Node parent;
        double gCost;
        double hCost;
        double fCost;

        Node(BlockPos pos, Node parent, double gCost, double hCost) {
            this.pos = pos;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
        }
    }
}
