package servermod.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.PlayerNotFoundException;

public class CommandDisarm extends Command {
	public CommandDisarm() {
		super("disarm");
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		EntityPlayer disarming = var2.length < 1 ? getCommandSenderAsPlayer(var1) : MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(var2[0]);
		if (disarming == null) throw new PlayerNotFoundException();
		
		disarming.inventory.dropAllItems();
		
		notifyAdmins(var1, "Disarming "+disarming.username);
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public boolean isUsernameIndex(int var1) {
		return var1 == 0;
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/"+name+" [player]";
	}
}
