package servermod.home;

import servermod.util.DimensionChunkCoordinates;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandSetHome extends CommandBase {
	private String commandName;
	private Home home;
	
	public CommandSetHome(String commandName, Home home) {
		this.commandName = commandName;
		this.home = home;
	}

	@Override
	public String getCommandName() {
		return commandName;
	}
	
	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		EntityPlayer player = getCommandSenderAsPlayer(var1);
		
		int x = (int)Math.round(player.posX);
		int y = (int)Math.round(player.posY);
		int z = (int)Math.round(player.posZ);
		home.homes.put(player.username, new DimensionChunkCoordinates(x, y, z, player.dimension));
		var1.sendChatToPlayer(var1.translateString("commands.servermod_"+commandName+".success", player.dimension, x, y, z));
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
