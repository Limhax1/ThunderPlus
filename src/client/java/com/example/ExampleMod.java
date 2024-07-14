package com.example;

import com.example.commands.ExampleCommand;
import com.example.hud.ExampleHudElement;
import com.example.modules.Radio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thunder.hack.api.IAddon;
import thunder.hack.cmd.Command;
import thunder.hack.gui.hud.HudElement;
import thunder.hack.modules.Module;

import java.util.Arrays;
import java.util.List;

public class ExampleMod implements IAddon {
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	public void onInitialize() {
		LOGGER.info("I simply bypass");
	}

	@Override
	public List<Module> getModules() {
		// Return a list of your modules here
		return Arrays.asList(new Radio());
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
		return "ExampleMod";
	}

	@Override
	public String getAuthor() {
		return "Limhax";
	}

	@Override
	public String getRepo() {
		return "https://github.com/Limhax1/ThunderPlus";
	}
}