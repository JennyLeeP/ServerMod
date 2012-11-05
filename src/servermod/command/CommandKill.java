package servermod.command;

import java.util.List;

import servermod.core.Command;
import servermod.core.DamageSourceForce;
import servermod.core.ServerMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.PlayerNotFoundException;

public class CommandKill extends Command {
	public CommandKill(String commandName) {
		super(commandName);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		EntityPlayer killer = getCommandSenderAsPlayer(var1);
		EntityPlayer killing = var2.length < 1 ? killer : MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(var2[0]);
		
		if (killing == null) throw new PlayerNotFoundException();
		
		MinecraftServer server = MinecraftServer.getServer();
		if (killer == killing) {
			killing.attackEntityFrom(DamageSourceForce.genericForce, 1000);
		} else if (killer.canCommandSenderUseCommand(1, "kill") || !server.isDedicatedServer()) {
			killing.attackEntityFrom(DamageSourceForce.causePlayerDamageForce(killer), 1000);
			notifyAdmins(var1, "commands.servermod_"+commandName+".success", killing.username);
		}
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
        return var2.length >= 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
    }
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return !ServerMod.instance().settings.require_op_kill_self || super.canCommandSenderUseCommand(var1);
	}
}
