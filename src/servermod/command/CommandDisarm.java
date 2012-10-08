package servermod.command;

import java.util.List;

import servermod.core.Command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityLightningBolt;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.PlayerNotFoundException;

public class CommandDisarm extends Command {	
	public CommandDisarm(String commandName) {
		super(commandName);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		EntityPlayer disarming = var2.length < 1 ? (var1 instanceof EntityPlayer ? (EntityPlayer)var1 : null) : MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(var2[0]);

		if (disarming == null) throw new PlayerNotFoundException();

		disarming.inventory.dropAllItems();
		notifyAdmins(var1, "commands.servermod_"+commandName+".success", disarming.username);
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
        return var2.length >= 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
