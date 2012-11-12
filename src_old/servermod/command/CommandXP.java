package servermod.command;

import java.util.List;

import servermod.core.Command;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityLightningBolt;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.PlayerNotFoundException;
import net.minecraft.src.WrongUsageException;

public class CommandXP extends Command {
	public CommandXP(String commandName) {
		super(commandName);
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length < 1) throw new WrongUsageException("commands.xp.usage", new Object[0]);
		EntityPlayer adding = var2.length < 2 ? (var1 instanceof EntityPlayer ? (EntityPlayer)var1 : null) : MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(var2[1]);

		if (adding == null) throw new PlayerNotFoundException();

		int xp = parseIntWithMin(var1, var2[0], 1);
		adding.addExperience(xp);
		notifyAdmins(var1, "commands.xp.success", xp, adding.username);
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
        return var1.translateString("commands.xp.usage", new Object[0]);
    }
	
	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
        return var2.length >= 1 ? getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
