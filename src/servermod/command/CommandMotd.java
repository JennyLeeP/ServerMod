package servermod.command;

import servermod.core.Motd;
import net.minecraft.src.ICommandSender;

public class CommandMotd extends Command {
	private final Motd motd;
	
	public CommandMotd(Motd motd) {
		super("motd");
		
		this.motd = motd;
	}
	
	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		motd.serveMotd(var1);
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return true;
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/"+name;
	}
}
