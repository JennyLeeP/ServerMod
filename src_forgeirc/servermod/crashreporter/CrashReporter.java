package servermod.crashreporter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import servermod.core.ServerMod;
import servermod.core.Util;
import servermod.forgeirc.ForgeIRCHelper;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CrashReport;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ServerConfigurationManager;
import cpw.mods.fml.common.Loader;
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
		Loader.instance().getIndexedModList().get("ServerMod|CrashReporter").getMetadata().parent = "ServerMod";
		
		MinecraftServer.logger.addHandler(new ServerLogHandler());
		
		ForgeIRCHelper.init();
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
			
			String text;
			try {
				text = ServerMod.instance.pastebin.paste("ServerMod Crash Report "+(new SimpleDateFormat().format(new Date())), reportText);
			} catch (Throwable e) {
				text = "Failed to paste!";
			}
			
			ForgeIRCHelper.sendMessage("\u0002General server crash:\u0002 "+text);
		} else if (report instanceof Throwable) {
			String reportText = new CrashReport("Failed to save crash report", (Throwable)report).getCompleteReport();
			
			String text;
			try {
				text = ServerMod.instance.pastebin.paste("ServerMod Crash Report "+(new SimpleDateFormat().format(new Date())), reportText);
			} catch (Throwable e) {
				text = "Failed to paste!";
			}
			
			ForgeIRCHelper.sendMessage("\u0002General server crash, failed to save report:\u0002 "+text);
		}
	}
	
	public void handlePlayerCrash(String username, Throwable throwable) {
		String reportText = new CrashReport("Exception while reading packet from user "+username, throwable).getCompleteReport();
		
		String text;
		try {
			text = ServerMod.instance.pastebin.paste("ServerMod Crash Report "+(new SimpleDateFormat().format(new Date())), reportText);
		} catch (Throwable e) {
			text = "Failed to paste!";
		}
		
		ForgeIRCHelper.sendMessage("\u0002Player crash for "+username+":\u0002 "+text);
	}
}
