package servermod.forgeirc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import servermod.core.ServerMod;

public class ForgeIRCHelper {
	private static boolean initialized = false;
	private static Class ForgeIRC;
	private static Field ForgeIRC_mBot;
	private static Class Config;
	private static Field Config_cName;
	private static Class IRCLib;
	private static Method IRCLib_sendMessage;
	
	public static void init() {
		if (initialized) return;
		initialized = true;
		
		try {
			ForgeIRC = Class.forName("com.forgeirc.ForgeIRC");
			ForgeIRC_mBot = ForgeIRC.getDeclaredField("mBot");
			ForgeIRC_mBot.setAccessible(true);
			
			Config = Class.forName("com.forgeirc.Config");
			Config_cName = Config.getDeclaredField("cName");
			Config_cName.setAccessible(true);
			
			IRCLib = Class.forName("irclib.IRCLib");
			IRCLib_sendMessage = IRCLib.getDeclaredMethod("sendMessage", String.class, String.class);
			IRCLib_sendMessage.setAccessible(true);
		} catch (Throwable e) {
			ServerMod.instance.log.log(Level.SEVERE, "Failed to initialize ForgeIRC. Ensure ForgeIRC is installed");
		}
	}
	
	public static void sendMessage(String message) {
		if (!initialized) init();
		if (ForgeIRC_mBot == null || Config_cName == null || IRCLib_sendMessage == null) return;
		
		try {
			IRCLib_sendMessage.invoke(ForgeIRC_mBot.get(null), Config_cName.get(null), message);
		} catch (Throwable e) {}
	}
}
