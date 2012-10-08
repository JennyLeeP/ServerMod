package servermod.command;

import java.util.Arrays;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.PlayerNotFoundException;
import servermod.core.Command;

public class CommandHeal extends Command {
	public CommandHeal(String commandName) {
		super(commandName);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		EntityPlayer player;
		String[] var2a;
		if (var2.length > 0) {
			player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(var2[0]);
			var2a = Arrays.copyOfRange(var2, 1, var2.length);
		} else {
			player = getCommandSenderAsPlayer(var1);
			var2a = var2;
		}
		if (player == null) throw new PlayerNotFoundException();
		
		int amount = player.getMaxHealth() - player.getHealth();
		if (var2a.length > 1) amount = parseIntBounded(var1, var2[1], 1, 20);
		else if (var2a.length > 0) amount = parseIntBounded(var1, var2[0], 1, 20);
		
		player.heal(amount);
		player.getFoodStats().addStats(amount, 20.0F);
		
		notifyAdmins(var1, "commands.servermod_"+commandName+".success", player.username, amount);
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
        return var2.length >= 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
