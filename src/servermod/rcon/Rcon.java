package servermod.rcon;

import java.io.File;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import servermod.core.Settings;

import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = "ServerMod|RCON", name = "ServerMod RCON", version = "1.0", dependencies = "required-after:ServerMod")
public class Rcon {
	@Instance("ServerMod|RCON")
	public static Rcon instance;
	public static MinecraftServer server;
	
	protected Settings settings = new Settings(new File(new File("servermod", "config"), "rcon.cfg"), "ServerMod RCON configuration file\nThis is an improved RCON which can be access via Telnet and authenticates via Minecraft usernames.");
	protected Logger log = Logger.getLogger("ServerMod");
	private ServerSocket socket;
	
	@ServerStarting
	public void onServerStarting(FMLServerStartingEvent event) {
		server = event.getServer();
		Loader.instance().getIndexedModList().get("ServerMod|RCON").getMetadata().parent = "ServerMod";
		
		settings.addSetting("enable", false, "Enable RCON");
		settings.addSetting("port", 63280, "Port to use for the RCON");
		
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
		
		if (settings.getBoolean("enable")) {
			try {
				socket = new ServerSocket(settings.getInt("port"));
			} catch (Throwable e) {
				log.log(Level.SEVERE, "Failed to bind RCON", e);
				return;
			}
			
			new RconListenThread(socket).start();
		}
	}
	
	@ServerStopping
	public void onServerStopping(FMLServerStoppingEvent event) {
		try {
			socket.close();
		} catch (Throwable e) {}
	}
}
