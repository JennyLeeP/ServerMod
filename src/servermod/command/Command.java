package servermod.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public abstract class Command extends CommandBase {
	public final String name;
	
	public Command(String name) {
		this.name = name;
	}
	
	@Override
	public String getCommandName() {
		return name;
	}
	
	@Override
	public abstract String getCommandUsage(ICommandSender var1);
	
	WrongUsageException showUsage(ICommandSender var1) {
		return new WrongUsageException(getCommandUsage(var1));
	}
}
