package servermod.motd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.logging.Level;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.ServerCommandManager;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import servermod.core.ServerMod;
import servermod.util.Util;

public class MOTD implements IPlayerTracker {
	private ServerMod sm;
	private static final String defaultMotd =
		"Hello, $PLAYER$!\n"+
		"This is a default installation of ServerMod. In order to change\n"+
		"this message, edit servermod/motd.txt or disable it by setting\n" +
		"enable-motd to false in servermod/servermod.properties\n"+
		"Happy playing!";
	;
	private String motd;
	
	public MOTD(ServerMod sm) {
		this.sm = sm;
		
		GameRegistry.registerPlayerTracker(this);
		
		try {
			File f = new File("servermod", "motd.txt");
			
			if (!f.exists()) {
				PrintWriter pw = new PrintWriter(new FileWriter(f));
				pw.print(defaultMotd);
				pw.close();
			}
			
			motd = Util.readFileToString(f);
		} catch (Throwable e) {
			sm.server.logger.log(Level.WARNING, "MOTD: Failed to read MOTD: "+e);
		}
		
		ServerCommandManager commands = (ServerCommandManager)sm.server.getCommandManager();
		LanguageRegistry lang = LanguageRegistry.instance();
		
		lang.addStringLocalization("commands.servermod_motd.usage", "/motd");
		commands.registerCommand(new CommandMotd("motd", this));
		
		sm.server.logger.log(Level.INFO, "MOTD: Initialized");
	}

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		serveMotd(player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
	}
	
	protected void serveMotd(ICommandSender player) {
		for (String line : motd.split("\n")) {
			player.sendChatToPlayer("\u00A77"+line.replace("\r", "").replace("$PLAYER$", player.getCommandSenderName()));
		}
	}
}
