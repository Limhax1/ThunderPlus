package com.example.commands;

import com.example.modules.Other.PathfindingModule;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import thunder.hack.features.cmd.Command;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ExampleCommand extends Command {
    public ExampleCommand() {
        super("pathfinder");
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("setX")
                .then(arg("value", IntegerArgumentType.integer())
                        .executes(context -> {
                            int value = IntegerArgumentType.getInteger(context, "value");
                            setCoordX(value);
                            sendMessage("X coordinate has been set to: " + value);
                            return SINGLE_SUCCESS;
                        })
                )
        );

        builder.then(literal("setY")
                .then(arg("value", IntegerArgumentType.integer())
                        .executes(context -> {
                            int value = IntegerArgumentType.getInteger(context, "value");
                            setCoordY(value);
                            sendMessage("Y coordinate has been set to: " + value);
                            return SINGLE_SUCCESS;
                        })
                )
        );

        builder.then(literal("setZ")
                .then(arg("value", IntegerArgumentType.integer())
                        .executes(context -> {
                            int value = IntegerArgumentType.getInteger(context, "value");
                            setCoordZ(value);
                            sendMessage("Z coordinate has been set to: " + value);
                            return SINGLE_SUCCESS;
                        })
                )
        );

        builder.executes(context -> {
            sendMessage("Usage: .pathfinder <setX | setY | setZ> <Value>");
            return SINGLE_SUCCESS;
        });
    }

    private void setCoordX(int value) {
        PathfindingModule.CoordX.setValue(value);
    }

    private void setCoordY(int value) {
        PathfindingModule.CoordY.setValue(value);
    }

    private void setCoordZ(int value) {
        PathfindingModule.CoordZ.setValue(value);
    }
}