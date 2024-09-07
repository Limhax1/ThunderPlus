package com.example;

import com.example.commands.ExampleCommand;
import com.example.hud.ExampleHudElement;
import com.example.modules.Other.*;
import com.example.modules.VulcanBypasses.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thunder.hack.api.IAddon;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.Module;

import java.util.Arrays;
import java.util.List;

public class ExampleMod implements IAddon {
	public static final Logger LOGGER = LoggerFactory.getLogger("ThunderPlus");

	@Override
	public void onInitialize() {
		LOGGER.info("I simply bypass");
	}

	@Override
	public List<Module> getModules() {
		return Arrays.asList(new Radio(), new VulcanSpeed(), new VulcanBoatFly(), new VulcanBoatFly2(), new VulcanNofall(), new VulcanVelocity(), new VulcanGlide(), new PacketFly(), new PingSpoof(), new AAC1910(), new VerusCombat(), new BedFuckerButWithoutPacket(), new VulcanSpider(), new VulcantSpider(), new PathfindingModule());
	}

	@Override
	public List<Command> getCommands() {
		return Arrays.asList(new ExampleCommand());
	}

	@Override
	public List<HudElement> getHudElements() {
		return Arrays.asList(new ExampleHudElement());
	}

	@Override
	public String getPackage() {
		return "com.example";
	}

	@Override
	public String getName() {
		return "Thunder+";
	}

	@Override
	public String getAuthor() {
		return "Limhax";
	}

	@Override
	public String getRepo() {
		return("https://github.com/Limhax1/ThunderPlus/");
	}

	@Override
	public String getVersion() {
		return "0.3";
	}
}