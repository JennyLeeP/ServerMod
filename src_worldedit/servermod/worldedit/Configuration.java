package servermod.worldedit;

import java.io.File;

import net.minecraft.server.MinecraftServer;

import com.sk89q.worldedit.util.PropertiesConfiguration;

public class Configuration extends PropertiesConfiguration {
	public Configuration() {
		super(new File("servermod", "worldedit.properties"));
	}
	
	@Override
	public File getWorkingDirectory() {
		return new File(".");
	}
}
