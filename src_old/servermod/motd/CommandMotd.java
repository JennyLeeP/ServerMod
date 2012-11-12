package servermod.motd;

import net.minecraft.src.ICommandSender;
import servermod.core.Command;

public class CommandMotd extends Command {
	private final MOTD motd;
	
	public CommandMotd(String commandName, MOTD motd) {
		super(commandName);
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
}
