package servermod.core;

import java.io.File;
import java.io.PrintWriter;
import java.util.logging.Level;

import servermod.command.CommandMotd;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.ServerCommandManager;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;

public class Motd implements IPlayerTracker {
	private static final String defaultMotd =
		"Hello, $PLAYER$!\n"+
		"This is a default installation of ServerMod. In order to change\n"+
		"this message, edit servermod/motd.txt or disable it by setting\n" +
		"enable-motd to false in servermod/config/servermod.cfg\n"+
		"Happy playing!";
	;
	private String motd;
	
	public Motd() {
		GameRegistry.registerPlayerTracker(this);
		
		((ServerCommandManager)ServerMod.server.getCommandManager()).registerCommand(new CommandMotd(this));
		
		try {
			File file = new File("servermod", "motd.txt");
			
			if (!file.exists()) {
				PrintWriter pw = new PrintWriter(file);
				pw.print(defaultMotd);
				pw.close();
			}
			
			motd = Util.readFileToString(file);
		} catch (Throwable e) {
			ServerMod.instance.log.log(Level.WARNING, "Unable to read the MOTD", e);
		}
	}
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		serveMotd(player);
	}
	
	@Override
	public void onPlayerLogout(EntityPlayer player) {}
	
	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {}
	
	@Override
	public void onPlayerRespawn(EntityPlayer player) {}
	
	public void serveMotd(ICommandSender sender) {
		for (String line : motd.split("\n")) {
			sender.sendChatToPlayer("\u00a77"+line.replace("\r", "").replace("$PLAYER$", sender.getCommandSenderName()));
		}
	}
}
