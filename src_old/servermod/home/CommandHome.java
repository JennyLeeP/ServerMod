package servermod.home;

import servermod.util.DimensionChunkCoordinates;
import servermod.util.TeleporterCustom;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;

public class CommandHome extends CommandBase {
	private String commandName;
	private Home home;
	
	public CommandHome(String commandName, Home home) {
		this.commandName = commandName;
		this.home = home;
	}
	
	@Override
	public String getCommandName() {
		return commandName;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		home.respawnPlayer((EntityPlayerMP)getCommandSenderAsPlayer(var1));
	}
	
	@Override
	public String getCommandUsage(ICommandSender var1) {
        return var1.translateString("commands.servermod_"+commandName+".usage", new Object[0]);
    }
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return !home.sm.settings.home_require_op || super.canCommandSenderUseCommand(var1);
	}
}
