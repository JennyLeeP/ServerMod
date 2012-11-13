package servermod.core;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;

import servermod.api.provider.Registry;
import servermod.command.*;
import servermod.provider.*;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "ServerMod", name = "ServerMod", version = ServerMod.VERSION)
public class ServerMod {
	public static final String VERSION = "2.0";
	
	@Instance("ServerMod")
	public static ServerMod instance;
	public static MinecraftServer server;
	
	public Logger log = Logger.getLogger("ServerMod");
	public Settings settings = new Settings(new File(new File("servermod", "config"), "servermod.cfg"), "ServerMod Core configuration file");
	
	@ServerStarting
	public void onServerStarting(FMLServerStartingEvent event) {
		server = event.getServer();
		log.setParent(FMLLog.getLogger());
		
		Registry.registerPastebinProvider("pastebin", new PastebinCom());
		Registry.registerPastebinProvider("forge", new PastebinStikked("http://paste.minecraftforge.net/api"));
		Registry.registerPastebinProvider("ubuntu", new PastebinUbuntu());
		
		event.registerServerCommand(new CommandKill());
		event.registerServerCommand(new CommandTps());
		event.registerServerCommand(new CommandSay());
		
		settings.addSetting("require-op-tps", false, "Require op for the /tps command");
		
		try {
			settings.load();
		} catch (Throwable e) {
			log.log(Level.WARNING, "Failed to load the configuration file", e);
		}
		try {
			settings.save();
		} catch (Throwable e) {
			log.log(Level.WARNING, "Failed to save the configuration file", e);
		}
	}
}
