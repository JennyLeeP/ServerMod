package servermod.command;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public abstract class Command extends CommandBase {
	public final String name;
	
	public Command(String name) {
		this.name = name;
	}
	
	@Override
	public String getCommandName() {
		return name;
	}
}
