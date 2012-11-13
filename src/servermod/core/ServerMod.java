package servermod.core;

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
	
	@ServerStarting
	public void onServerStarting(FMLServerStartingEvent event) {
		server = event.getServer();
		
		log.setParent(FMLLog.getLogger());
		
		Registry.registerPastebinProvider("pastebin", new PastebinCom());
		Registry.registerPastebinProvider("forge", new PastebinStikked("http://paste.minecraftforge.net/api"));
		Registry.registerPastebinProvider("ubuntu", new PastebinUbuntu());
		
		event.registerServerCommand(new CommandKill());
	}
}
