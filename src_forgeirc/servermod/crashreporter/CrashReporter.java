package servermod.crashreporter;

import java.io.File;

import servermod.core.Util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CrashReport;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ServerConfigurationManager;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "ServerMod|CrashReporter", name = "ServerMod Crash Reporter", version = "1.0", dependencies = "required-after:ServerMod")
public class CrashReporter {
	@Instance("ServerMod|CrashReporter")
	public static CrashReporter instance;
	public static MinecraftServer server;
	
	@ServerStarting
	public void onServerStarting(FMLServerStartingEvent event) {
		server = event.getServer();
		
		MinecraftServer.logger.addHandler(new ServerLogHandler());
		FMLLog.getLogger().addHandler(new FMLLogHandler());
	}
	
	public void handleServerCrash(Object report) {
		ServerConfigurationManager manager = server.getConfigurationManager();
		while (!manager.playerEntityList.isEmpty()) {
			((EntityPlayerMP)manager.playerEntityList.get(0)).playerNetServerHandler.kickPlayerFromServer("Server crashed");
		}
		
		if (report instanceof File) {
			String reportText;
			
			try {
				reportText = Util.readFileToString((File)report);
			} catch (Throwable e) {
				reportText = "Failed to read report: "+e;
			}
		} else if (report instanceof Throwable) {
			
		}
	}
	
	public void handlePlayerCrash(String username, Throwable throwable) {
		CrashReport report = new CrashReport("Exception while reading packet from user "+username, throwable);
	}
}
